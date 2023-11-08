package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.MD5;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.CommercialTenantMapper;
import com.ruoyi.zlyyh.utils.MapUtils;
import com.ruoyi.zlyyhmobile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.GeoEntry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商户Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommercialTenantServiceImpl implements ICommercialTenantService {

    private final CommercialTenantMapper baseMapper;
    private final ICategoryService categoryService;
    private final ICategoryProductService categoryProductService;
    private final IProductService productService;
    private final IShopService shopService;
    private final IShopProductService shopProductService;

    /**
     * 查询门店商户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页条件
     * @return 结果
     */
    @Override
    public TableDataInfo<CommercialTenantVo> getShopCommercialTenantList(CommercialTenantBo bo, PageQuery pageQuery) {
        String cityCode = ServletUtils.getHeader(ZlyyhConstants.CITY_CODE);
        String geoKey = ZlyyhConstants.SHOP_GEO_CACHE_KEY + ":" + cityCode;
        return getCommercialGeoPage(geoKey, cityCode, bo, pageQuery);
    }

//    private void delGeoCache(String geoKey, Long commercialTenantId, Long shopId, double lng, double lat, double radius, GeoUnit geoUnit, GeoOrder geoOrder) {
//        // 剔除对应缓存内容
//        String merber = commercialTenantId + "_" + shopId;
//        RedisUtils.geoDel(geoKey, merber);
//        String geoZSetKey = RedisUtils.getGeoZSetKey(geoKey, lng, lat, radius, geoUnit, geoOrder);
//        RedisUtils.geoDel(geoZSetKey, merber);
//    }

    /**
     * 查询商户
     */
    @Override
    public CommercialTenantVo queryById(CommercialTenantBo bo) {
        String cityCode = ServletUtils.getHeader(ZlyyhConstants.CITY_CODE);
        CommercialTenantVo commercialTenantVo = CacheUtils.get(CacheNames.COMMERCIAL, bo.getCommercialTenantId());
        if (null == commercialTenantVo) {
            commercialTenantVo = baseMapper.selectVoById(bo.getCommercialTenantId());
            CacheUtils.put(CacheNames.COMMERCIAL, bo.getCommercialTenantId(), commercialTenantVo);
        }
        // 如果是今日特惠 商品根据星期查
        this.setProduct(commercialTenantVo, bo.getPlatformKey(), bo.getWeekDate(), cityCode, bo.getShopId());
        ShopVo shopVo = null;
        if (null != bo.getShopId()) {
            shopVo = shopService.queryById(bo.getShopId());
            if (null != bo.getLatitude() && null != bo.getLongitude()) {
                // 计算距离
                double v = MapUtils.distance(bo.getLongitude(), bo.getLatitude(), shopVo.getLongitude(), shopVo.getLatitude()) / 1000;
                String mv = "" + v;
                shopVo.setDistance(new BigDecimal(mv));
                commercialTenantVo.setDistance(shopVo.getDistance());
            }
            commercialTenantVo.setShopVo(shopVo);
        }
        if (null == shopVo) {
            this.setShop(commercialTenantVo, bo, cityCode);
        }
        return commercialTenantVo;
    }

    /**
     * 查询商户列表
     */
    @Override
    public TableDataInfo<CommercialTenantVo> queryPageListByDayProduct(CommercialTenantBo bo, PageQuery pageQuery) {
        String cityCode = ServletUtils.getHeader(ZlyyhConstants.CITY_CODE);
        List<Long> commercialTenantIdList;
        if (null != bo.getCategoryId()) {
            //查询类别下的商品列表
            CategoryVo categoryVo = categoryService.queryById(bo.getCategoryId());
            if (ObjectUtil.isNull(categoryVo) || "1".equals(categoryVo.getStatus())) {
                return TableDataInfo.build(new ArrayList<>());
            }
            List<CategoryProductVo> categoryProductVos = categoryProductService.queryList(bo.getCategoryId());
            if (ObjectUtil.isEmpty(categoryProductVos)) {
                return TableDataInfo.build(new ArrayList<>());
            }
            commercialTenantIdList = categoryProductVos.stream().map(CategoryProductVo::getProductId).collect(Collectors.toList());
        } else {
            // 如果是今日特惠 先查商品，再查商户
            commercialTenantIdList = productService.queryCommercialTenantIdList(bo.getWeekDate(), cityCode, bo.getPlatformKey());
            if (ObjectUtil.isEmpty(commercialTenantIdList)) {
                return TableDataInfo.build(new ArrayList<>());
            }
        }
        // geo 缓存key
        String geoKey = "geoDayProduct:" + cityCode + ":" + MD5.create().digestHex(JsonUtils.toJsonString(commercialTenantIdList));
        // 缓存今日特惠门店信息
//        if (!RedisUtils.isExistsObject(geoKey)) {
//            cacheShopGeo(geoKey, cityCode, commercialTenantIdList);
//        }
        TableDataInfo<CommercialTenantVo> build = TableDataInfo.build(new ArrayList<>());
        // 根据商户查询门店
        ShopBo shopBo = new ShopBo();
        shopBo.setCommercialTenantIds(commercialTenantIdList);
        shopBo.setCitycode(cityCode);
        shopBo.setLongitude(bo.getLongitude());
        shopBo.setLatitude(bo.getLatitude());
        TableDataInfo<ShopVo> shopVoTableDataInfo = shopService.selectShopListByCommercialTenantId(shopBo, pageQuery);
        if (ObjectUtil.isEmpty(shopVoTableDataInfo.getRows())) {
            return build;
        }
        List<CommercialTenantVo> commercialTenantVos = new ArrayList<>(shopVoTableDataInfo.getRows().size());
        Map<Object, CommercialTenantVo> commercialMaps = CacheUtils.getByKeys(CacheNames.COMMERCIAL, shopVoTableDataInfo.getRows().stream().map(ShopVo::getCommercialTenantId).collect(Collectors.toSet()));
        for (ShopVo shopVo : shopVoTableDataInfo.getRows()) {
            CommercialTenantVo ct = commercialMaps.get(shopVo.getCommercialTenantId());
            if (null == ct) {
                ct = baseMapper.selectVoById(shopVo.getCommercialTenantId());
                commercialMaps.put(shopVo.getCommercialTenantId(), ct);
                CacheUtils.put(CacheNames.COMMERCIAL, shopVo.getCommercialTenantId(), ct);
            }
            // 查询商品信息
            this.setProduct(ct, bo.getPlatformKey(), bo.getWeekDate(), cityCode, shopVo.getShopId());
            ct.setShopVo(shopVo);
            commercialTenantVos.add(ObjectUtil.clone(ct));
        }
        build.setRows(commercialTenantVos);
        build.setTotal(shopVoTableDataInfo.getTotal());
        return build;
    }

    private TableDataInfo<CommercialTenantVo> getCommercialGeoPage(String geoKey, String cityCode, CommercialTenantBo bo, PageQuery pageQuery) {
        TableDataInfo<CommercialTenantVo> build = TableDataInfo.build(new ArrayList<>());
        ShopBo shopBo = new ShopBo();
        shopBo.setCitycode(cityCode);
        shopBo.setLongitude(bo.getLongitude());
        shopBo.setLatitude(bo.getLatitude());
        TableDataInfo<ShopVo> shopVoTableDataInfo = shopService.queryPageList(shopBo, pageQuery);
        if (ObjectUtil.isEmpty(shopVoTableDataInfo.getRows())) {
            return build;
        }
        List<CommercialTenantVo> commercialTenantVos = new ArrayList<>(shopVoTableDataInfo.getRows().size());
        Map<Object, CommercialTenantVo> commercialMaps = CacheUtils.getByKeys(CacheNames.COMMERCIAL, shopVoTableDataInfo.getRows().stream().map(ShopVo::getCommercialTenantId).collect(Collectors.toSet()));
        for (ShopVo shopVo : shopVoTableDataInfo.getRows()) {
            CommercialTenantVo ct = commercialMaps.get(shopVo.getCommercialTenantId());
            if (null == ct) {
                ct = baseMapper.selectVoById(shopVo.getCommercialTenantId());
                commercialMaps.put(shopVo.getCommercialTenantId(), ct);
                CacheUtils.put(CacheNames.COMMERCIAL, shopVo.getCommercialTenantId(), ct);
            }
            // 查询商品信息
            this.setProduct(ct, bo.getPlatformKey(), bo.getWeekDate(), cityCode, shopVo.getShopId());
            if (ObjectUtil.isNotEmpty(shopVo.getDistance())){
                BigDecimal distance = shopVo.getDistance().setScale(2, BigDecimal.ROUND_DOWN);
                shopVo.setDistanceString(distance.toString());
            }
            ct.setShopVo(shopVo);
            commercialTenantVos.add(ObjectUtil.clone(ct));
        }
        build.setRows(commercialTenantVos);
        build.setTotal(shopVoTableDataInfo.getTotal());
        return build;
    }

//    private TableDataInfo<CommercialTenantVo> getCommercialGeoPage(String geoKey, String cityCode, CommercialTenantBo bo, PageQuery pageQuery) {
//        TableDataInfo<CommercialTenantVo> build = TableDataInfo.build(new ArrayList<>());
//        Collection<ScoredEntry<String>> scoredEntries;
//        if (null != bo.getLongitude() && null != bo.getLatitude() && bo.getLongitude().signum() > 0 && bo.getLatitude().signum() > 0) {
//            scoredEntries = RedisUtils.geoRadiusWithDistancePage(geoKey, bo.getLongitude().doubleValue(), bo.getLatitude().doubleValue(), 3000, GeoUnit.KILOMETERS, GeoOrder.ASC, Duration.ofMinutes(RandomUtil.randomInt(30, 60)), pageQuery.getPageNum(), pageQuery.getPageSize());
//            build.setTotal(RedisUtils.geoRadiusWithDistanceCount(geoKey, bo.getLongitude().doubleValue(), bo.getLatitude().doubleValue(), 3000, GeoUnit.KILOMETERS, GeoOrder.ASC));
//        } else {
//            scoredEntries = RedisUtils.zsetPage(geoKey, pageQuery.getPageNum(), pageQuery.getPageSize());
//            build.setTotal(RedisUtils.zsetSize(geoKey));
//        }
//        // 查询商户门店等相关信息
//        Set<Object> shopIds = new HashSet<>(scoredEntries.size());
//        Set<Object> commercialTenantIds = new HashSet<>(scoredEntries.size());
//        List<Long> commercialTenantIdList = new ArrayList<>(scoredEntries.size());
//        List<Long> shopIdList = new ArrayList<>(scoredEntries.size());
//        List<Double> distanceList = new ArrayList<>(scoredEntries.size());
//        for (ScoredEntry<String> scoredEntry : scoredEntries) {
//            try {
//                String[] s = scoredEntry.getValue().split("_");
//                Long commercialTenantId = Long.valueOf(s[0]);
//                commercialTenantIds.add(commercialTenantId);
//                commercialTenantIdList.add(commercialTenantId);
//
//                Long shopId = Long.valueOf(s[1]);
//                shopIds.add(shopId);
//                shopIdList.add(shopId);
//
//                distanceList.add(scoredEntry.getScore());
//            } catch (Exception ignored) {
//            }
//        }
//        List<CommercialTenantVo> commercialTenantVos = new ArrayList<>(commercialTenantIdList.size());
//        Map<Object, CommercialTenantVo> commercialMaps = CacheUtils.getByKeys(CacheNames.COMMERCIAL, commercialTenantIds);
//        Map<Object, ShopVo> shopMaps = CacheUtils.getByKeys(CacheNames.SHOP, shopIds);
//        for (Object commercialTenantId : commercialTenantIds) {
//            CommercialTenantVo ct = commercialMaps.get(commercialTenantId);
//            if (null == ct) {
//                ct = baseMapper.selectVoById((Long) commercialTenantId);
//                commercialMaps.put(commercialTenantId, ct);
//                CacheUtils.put(CacheNames.COMMERCIAL, commercialTenantId, ct);
//            }
//            // 查询商品信息
//            this.setProduct(ct, bo.getPlatformKey(), bo.getWeekDate(), cityCode);
//        }
//        for (int i = 0; i < commercialTenantIdList.size(); i++) {
//            Long commercialTenantId = commercialTenantIdList.get(i);
//            Long shopId = shopIdList.get(i);
//            CommercialTenantVo ct = commercialMaps.get(commercialTenantId);
//            if (null == ct || !"0".equals(ct.getStatus())) {
//                // 剔除对应缓存内容
////                delGeoCache(geoKey, commercialTenantId, shopId, bo.getLongitude().doubleValue(), bo.getLatitude().doubleValue(), 3000, GeoUnit.KILOMETERS, GeoOrder.ASC);
//                continue;
//            }
//            ShopVo shopVo = shopMaps.get(shopId);
//            if (null == shopVo) {
//                shopVo = shopService.queryById(shopId);
//            }
//            if (null == shopVo || !"0".equals(shopVo.getStatus())) {
////                delGeoCache(geoKey, commercialTenantId, shopId, bo.getLongitude().doubleValue(), bo.getLatitude().doubleValue(), 3000, GeoUnit.KILOMETERS, GeoOrder.ASC);
//                continue;
//            }
//            shopVo.setDistance(new BigDecimal("" + distanceList.get(i)));
//            ct.setShopVo(shopVo);
//            commercialTenantVos.add(ObjectUtil.clone(ct));
//        }
//        build.setRows(commercialTenantVos);
//        return build;
//    }

    private void cacheShopGeo(String geoKey, String cityCode, List<Long> commercialTenantIdList) {
        // 根据商户查询门店
        List<ShopVo> shopVos = shopService.queryListByCommercialIds(commercialTenantIdList, cityCode);
        List<GeoEntry> geoEntries = new ArrayList<>(shopVos.size());
        Map<Object, CommercialTenantVo> commercialMaps = CacheUtils.getByKeys(CacheNames.COMMERCIAL, commercialTenantIdList.stream().collect(Collectors.toSet()));
        for (ShopVo shopVo : shopVos) {
            if (null == shopVo.getCommercialTenantId() || shopVo.getLatitude().signum() < 1 || shopVo.getLongitude().signum() < 1) {
                continue;
            }
            CommercialTenantVo commercialTenantVo = commercialMaps.get(shopVo.getCommercialTenantId());
            if (null == commercialTenantVo) {
                commercialTenantVo = baseMapper.selectVoById(shopVo.getCommercialTenantId());
                commercialMaps.put(shopVo.getCommercialTenantId(), commercialTenantVo);
                CacheUtils.put(CacheNames.COMMERCIAL, shopVo.getCommercialTenantId(), commercialTenantVo);
            }
            // 查询商户
            if (null == commercialTenantVo || !"0".equals(commercialTenantVo.getStatus()) || "1".equals(commercialTenantVo.getIndexShow())) {
                continue;
            }
            String member = shopVo.getCommercialTenantId() + "_" + shopVo.getShopId();
            geoEntries.add(new GeoEntry(shopVo.getLongitude().doubleValue(), shopVo.getLatitude().doubleValue(), member));
        }
//        // 缓存门店信息
//        if (ObjectUtil.isNotEmpty(geoEntries)) {
//            RedisUtils.geoAdd(geoKey, geoEntries);
//        }
    }

    private void setProduct(CommercialTenantVo next, Long platformKey, String weekDate, String cityCode, Long shopId) {
        // 如果是今日特惠 商品根据星期查
        if (shopId != null) {
            //如果传了shopId 查询商品门店关联表 就不按照商品类别分类了 统一进行作展示
            List<ProductVo> productVos1 = productService.queryListByShopId(platformKey, shopId, weekDate, cityCode);
            //Map<String, List<ProductVo>> collect1 = productVos1.stream().collect(Collectors.groupingBy(ProductVo::getProductType));
            next.setProductFoodList(productVos1);
        }
//        List<ProductVo> productVos = productService.queryListByCommercialId(platformKey, next.getCommercialTenantId(), weekDate, cityCode);
//        if (ObjectUtil.isNotEmpty(productVos)) {
//            Map<String, List<ProductVo>> collect = productVos.stream().collect(Collectors.groupingBy(ProductVo::getProductType));
//            next.setProductCouponList(collect.get("0"));
//            next.setProductActivityList(collect.get("2"));
//        }

    }

    private void setShop(CommercialTenantVo next, CommercialTenantBo bo, String cityCode) {
        ShopBo shopBo = new ShopBo();
        shopBo.setCommercialTenantId(next.getCommercialTenantId());
        shopBo.setLatitude(bo.getLatitude());
        shopBo.setLongitude(bo.getLongitude());
        shopBo.setCitycode(cityCode);
        PageQuery pageQueryShop = new PageQuery();
        pageQueryShop.setPageNum(1);
        pageQueryShop.setPageSize(1);
        TableDataInfo<ShopVo> shopVoTableDataInfo = shopService.queryPageList(shopBo, pageQueryShop);
        List<ShopVo> shopVos = shopVoTableDataInfo.getRows();
        if (ObjectUtil.isNotEmpty(shopVos)) {
            next.setDistance(shopVos.get(0).getDistance());
            next.setShopVo(shopVos.get(0));
        }
    }

//    /**
//     * 查询商户列表
//     */
//    @Override
//    public TableDataInfo<CommercialTenantVo> queryPageList(CommercialTenantBo bo, PageQuery pageQuery) {
//        Page<CommercialTenantVo> result = CommercialCacheUtils.getCache(bo.getPlatformKey(), bo.getCategoryId(), bo.getIndexShow(), pageQuery.getPageNum());
//        if (null == result) {
//            LambdaQueryWrapper<CommercialTenant> lqw = buildQueryWrapper(bo);
//            if (lqw == null) {
//                return TableDataInfo.build(new ArrayList<>());
//            }
//            result = baseMapper.selectVoPage(pageQuery.build(), lqw);
//            CommercialCacheUtils.setCache(bo.getPlatformKey(), bo.getCategoryId(), bo.getIndexShow(), pageQuery.getPageNum(), result);
//        }
//        List<CommercialTenantVo> rows = result.getRecords();
//        //迭代器删除无门店或者无商品的商户
//        Iterator<CommercialTenantVo> iterator = rows.iterator();
//        while (iterator.hasNext()) {
//            CommercialTenantVo next = iterator.next();
//            ProductBo productBo = new ProductBo();
//            //如果是今日特惠 商品根据星期查
//            if (StringUtils.isNotBlank(bo.getWeekDate())) {
//                productBo.setWeekDate(bo.getWeekDate());
//            }
//            productBo.setCommercialTenantId(next.getCommercialTenantId().toString());
//            List<ProductVo> productVos = productService.queryList(productBo);
//            if (ObjectUtil.isNotEmpty(productVos)) {
//                Map<String, List<ProductVo>> collect = productVos.stream().collect(Collectors.groupingBy(ProductVo::getProductType));
//
//                next.setProductCouponList(collect.get("0"));
//                next.setProductActivityList(collect.get("2"));
//            }
//
//            ShopBo shopBo = new ShopBo();
//            shopBo.setCommercialTenantId(next.getCommercialTenantId());
//            shopBo.setLatitude(bo.getLatitude());
//            shopBo.setLongitude(bo.getLongitude());
//            shopBo.setCitycode(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
//            PageQuery pageQueryShop = new PageQuery();
//            pageQueryShop.setPageNum(1);
//            pageQueryShop.setPageSize(1);
//            TableDataInfo<ShopVo> shopVoTableDataInfo = shopService.queryPageList(shopBo, pageQueryShop);
//            List<ShopVo> shopVos = shopVoTableDataInfo.getRows();
//            if (ObjectUtil.isNotEmpty(shopVos)) {
//                next.setDistance(shopVos.get(0).getDistance());
//                next.setShopVo(shopVos.get(0));
//            }
//        }
//        rows = rows.stream()
//            .sorted(Comparator.comparing(CommercialTenantVo::getDistance, Comparator.nullsLast(BigDecimal::compareTo)))
//            .collect(Collectors.toList());
//        result.setRecords(rows);
//        return TableDataInfo.build(result);
//    }
}
