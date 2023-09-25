package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.CommercialTenantMapper;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyhmobile.service.*;
import com.ruoyi.zlyyhmobile.utils.ProductUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {

    private final ProductMapper baseMapper;
    private final CommercialTenantMapper commercialTenantMapper;
    private final ICommercialTenantProductService commercialTenantProductService;
    private final ICategoryService categoryService;
    private final ICategoryProductService categoryProductService;
    private final IShopProductService shopProductService;
    private final IShopService shopService;
    private final IProductInfoService productInfoService;
    private final IProductTicketService productTicketService;

    /**
     * 查询商品
     */
    @Cacheable(cacheNames = CacheNames.PRODUCT, key = "#productId")
    @Override
    public ProductVo queryById(Long productId) {
        return baseMapper.selectVoById(productId);
    }

    /**
     * 查询美食商品详情
     */
    @Cacheable(cacheNames = CacheNames.FOOD_PRODUCT, key = "#productId")
    @Override
    public ProductVo queryFoodById(Long productId) {
        ProductVo productVo = baseMapper.selectVoById(productId);
        if (null == productVo) {
            return null;
        }
        ProductInfoVo productInfoVo = productInfoService.queryById(productId);
        productVo.setProductInfoVo(productInfoVo);
        return productVo;
    }

    @Override
    public ProductVo queryTicketById(Long productId) {
        ProductVo productVo = baseMapper.selectVoById(productId);
        if (null == productVo) {
            return null;
        }
        ProductTicketVo productTicketVo = productTicketService.queryProductTicket(productId);
        List<ProductTicketSessionVo> ticketSessionVos = productTicketService.querySessionAndLineByProductId(productId);
        productVo.setTicket(productTicketVo);
        productVo.setTicketSession(ticketSessionVos);
        return productVo;
    }

    /**
     * 查询商品列表
     */
    @Override
    public TableDataInfo<ProductVo> queryPageList(ProductBo bo, PageQuery pageQuery) {
        pageQuery.setIsAsc(null);
        pageQuery.setOrderByColumn(null);
        String str = JsonUtils.toJsonString(bo) + JsonUtils.toJsonString(pageQuery);
        String key = SecureUtil.md5(str);
        Page<ProductVo> result = null;
        try {
            result = CacheUtils.get(CacheNames.productList, key);
        } catch (Exception ignored) {
        }
        if (null == result) {
            LambdaQueryWrapper<Product> lqw = buildQueryWrapper(bo);
            if (lqw == null) {
                return TableDataInfo.build(new ArrayList<>());
            }
            result = baseMapper.selectVoPage(pageQuery.build(), lqw);
            CacheUtils.put(CacheNames.productList, key, result);
        }
        TableDataInfo<ProductVo> build = TableDataInfo.build(result);
        Long userId = null;
        try {
            userId = LoginHelper.getUserId();
        } catch (Exception ignored) {
        }
        List<ProductVo> resultPro = new ArrayList<>(build.getRows().size());
        for (ProductVo productVo : build.getRows()) {
            //把门店加上
            ShopBo shopBo = new ShopBo();
            shopBo.setProductId(productVo.getProductId());
            PageQuery pageQuery1 = new PageQuery();
            pageQuery1.setPageSize(1);
            pageQuery1.setPageNum(1);
            TableDataInfo<ShopVo> shop= shopService.getShopListByProductId(shopBo, pageQuery1);
            if (ObjectUtil.isNotEmpty(shop) && ObjectUtil.isNotEmpty(shop.getRows())){
                ShopVo shopVo = shop.getRows().get(0);
                productVo.setShopVo(shopVo);
            }
            boolean dayFlag = StringUtils.isBlank(bo.getWeekDate()) || bo.getWeekDate().equals("" + DateUtil.dayOfWeek(new Date()));
            if (dayFlag) {
                resultPro.add(ProductUtils.getProductVoCheck(productVo, userId, bo.getPlatformKey(), bo.getShowCity(), true));
            }
        }
        build.setRows(resultPro);
        return build;
    }

    /**
     * 查询商品列表
     */
    @Override
    public List<ProductVo> queryList(ProductBo bo) {
        LambdaQueryWrapper<Product> lqw = buildQueryWrapper(bo);
        if (lqw == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询商品列表
     */
    @Override
    public List<ProductVo> queryListByCommercialId(Long platformKey, Long commercialId, String weekDate, String showCity) {
        //查询商户下的商品列表
        List<CommercialTenantProductVo> commercialTenantProductVos =
            commercialTenantProductService.queryListByCommercialTenantId(commercialId);
        if (ObjectUtil.isEmpty(commercialTenantProductVos)) {
            return new ArrayList<>();
        }
        Set<Long> collect = commercialTenantProductVos.stream().map(CommercialTenantProductVo::getProductId).collect(Collectors.toSet());
        return this.queryGrabPeriodProduct((Set) collect, showCity, weekDate, platformKey);
    }

    /**
     * 根据商品门店表查询商品列表
     */
    @Override
    public List<ProductVo> queryListByShopId(Long platformKey, Long shopId, String weekDate, String showCity) {
        //查询商户下的商品列表
        List<ShopProductVo> shopProductVos =
            shopProductService.queryByShopId(shopId);
        if (ObjectUtil.isEmpty(shopProductVos)) {
            return new ArrayList<>();
        }
        Set<Long> collect = shopProductVos.stream().map(ShopProductVo::getProductId).collect(Collectors.toSet());
        return this.queryGrabPeriodProduct((Set) collect, showCity, weekDate, platformKey);
    }

    /**
     * 查询商品列表
     */
    @Cacheable(cacheNames = CacheNames.COMMERCIAL_PRODUCT, key = "#platformId + '-' + #showCity + '-' + #weekDate")
    @Override
    public List<Long> queryCommercialTenantIdList(String weekDate, String showCity, Long platformId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getProductId);
        lqw.eq(Product::getPlatformKey, platformId);
        lqw.eq(Product::getStatus, "0");
        lqw.and(lm -> {
            lm.isNull(Product::getShowStartDate).or().lt(Product::getShowStartDate, new Date());
        });
        lqw.and(lm -> {
            lm.isNull(Product::getShowEndDate).or().gt(Product::getShowEndDate, new Date());
        });
        lqw.and(lm -> {
            lm.eq(Product::getShowCity, "ALL").or().like(Product::getShowCity, showCity);
        });
        lqw.and(lm -> {
            lm.eq(Product::getAssignDate, "0").or().like(Product::getWeekDate, weekDate);
        });
        List<ProductVo> productVos = baseMapper.selectVoList(lqw);
        if (ObjectUtil.isEmpty(productVos)) {
            return new ArrayList<>();
        }
        return commercialTenantProductService.queryListByProductIds(productVos.stream().map(ProductVo::getProductId).collect(Collectors.toList()));
    }

    /**
     * 查询秒杀活动商品
     *
     * @param productIds  产品ID 集合
     * @param cityCode    城市编码
     * @param weekDate    周几
     * @param platformKey 平台标识
     * @return 商品信息
     */
    @Override
    public List<ProductVo> queryGrabPeriodProduct(Set<Object> productIds, String cityCode, String weekDate, Long platformKey) {
        if (ObjectUtils.isEmpty(productIds)) {
            return new ArrayList<>(0);
        }
        List<ProductVo> result = new ArrayList<>(productIds.size());
        Map<Object, ProductVo> productMaps = CacheUtils.getByKeys(CacheNames.PRODUCT, productIds);
        for (Object productId : productIds) {
            ProductVo productVo = null;
            try {
                productVo = productMaps.get(productId);
            } catch (Exception e) {
                // 转换异常，删除缓存
                CacheUtils.evict(CacheNames.PRODUCT, productId);
            }
            if (ObjectUtil.isNull(productVo)) {
                productVo = this.queryById((Long) productId);
                if (null != productVo) {
                    // 同类调用 cache注解不生效，需手动缓存
                    CacheUtils.put(CacheNames.PRODUCT, productId, productVo);
                }
            }
            if (null == productVo) {
                continue;
            }
            if (!productVo.getPlatformKey().equals(platformKey)) {
                continue;
            }
            if (!"0".equals(productVo.getStatus())) {
                continue;
            }
            if (StringUtils.isNotBlank(weekDate) && !"0".equals(productVo.getAssignDate())) {
                if (!productVo.getWeekDate().contains(weekDate)) {
                    continue;
                }
            }
            if (null != productVo.getShowStartDate() && DateUtils.compare(productVo.getShowStartDate()) > 0) {
                continue;
            }
            if (null != productVo.getShowEndDate() && DateUtils.compare(productVo.getShowEndDate()) < 0) {
                continue;
            }
            if (!"ALL".equalsIgnoreCase(productVo.getShowCity())) {
                if (StringUtils.isBlank(cityCode)) {
                    continue;
                }
                if (!productVo.getShowCity().contains(cityCode)) {
                    continue;
                }
            }
            result.add(productVo);
        }
        return result;
    }

    /**
     * 查询秒杀活动商品
     *
     * @param productIds  产品ID 集合
     * @param cityCode    城市编码
     * @param weekDate    周几
     * @param platformKey 平台标识
     * @return 商品信息
     */
    @Override
    public Map<Long, ProductVo> queryGrabPeriodProductMap(Set<Object> productIds, String cityCode, String weekDate, Long platformKey) {
        if (ObjectUtils.isEmpty(productIds)) {
            return new HashMap<>(0);
        }
        Map<Long, ProductVo> result = new HashMap<>(productIds.size());
        Map<Object, ProductVo> productMaps = CacheUtils.getByKeys(CacheNames.PRODUCT, productIds);
        for (Object productId : productIds) {
            ProductVo productVo = null;
            try {
                productVo = productMaps.get(productId);
            } catch (Exception e) {
                // 转换异常，删除缓存
                CacheUtils.evict(CacheNames.PRODUCT, productId);
            }
            if (ObjectUtil.isNull(productVo)) {
                productVo = this.queryById((Long) productId);
                if (null != productVo) {
                    // 同类调用 cache注解不生效，需手动缓存
                    CacheUtils.put(CacheNames.PRODUCT, productId, productVo);
                }
            }
            if (null == productVo) {
                continue;
            }
            if (!productVo.getPlatformKey().equals(platformKey)) {
                continue;
            }
            if (!"0".equals(productVo.getStatus())) {
                continue;
            }
            if (StringUtils.isNotBlank(weekDate) && !"0".equals(productVo.getAssignDate())) {
                if (!productVo.getWeekDate().contains(weekDate)) {
                    continue;
                }
            }
            if (null != productVo.getShowStartDate() && DateUtils.compare(productVo.getShowStartDate()) > 0) {
                continue;
            }
            if (null != productVo.getShowEndDate() && DateUtils.compare(productVo.getShowEndDate()) < 0) {
                continue;
            }
            if (!"ALL".equalsIgnoreCase(productVo.getShowCity())) {
                if (StringUtils.isBlank(cityCode)) {
                    continue;
                }
                if (!productVo.getShowCity().contains(cityCode)) {
                    continue;
                }
            }
            result.put(productVo.getProductId(), productVo);
        }
        return result;
    }

    private LambdaQueryWrapper<Product> buildQueryWrapper(ProductBo bo) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getProductType()), Product::getProductType, bo.getProductType());
        lqw.eq(bo.getPlatformKey() != null, Product::getPlatformKey, bo.getPlatformKey());
        lqw.eq(Product::getStatus, "0");
        lqw.eq(StringUtils.isNotBlank(bo.getSearch()), Product::getSearch, bo.getSearch());
        lqw.eq(StringUtils.isNotBlank(bo.getSearchStatus()), Product::getSearchStatus, bo.getSearchStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getShowIndex()), Product::getShowIndex, bo.getShowIndex());
        lqw.and(lm -> {
            lm.isNull(Product::getShowStartDate).or().lt(Product::getShowStartDate, new Date());
        });
        lqw.and(lm -> {
            lm.isNull(Product::getShowEndDate).or().gt(Product::getShowEndDate, new Date());
        });
        lqw.and(lm -> {
            lm.eq(Product::getShowCity, "ALL").or().like(Product::getShowCity, bo.getShowCity());
        });
        if (StringUtils.isNotBlank(bo.getProductName())) {
            lqw.and(lm -> {
                lm.like(Product::getProductName, bo.getProductName()).or().like(Product::getProductAbbreviation, bo.getProductName())
                    .or().like(Product::getProductSubhead, bo.getProductName());
            });
        }
        if (StringUtils.isNotBlank(bo.getWeekDate())) {
            lqw.and(lm -> {
                lm.eq(Product::getAssignDate, "0").or().like(Product::getWeekDate, bo.getWeekDate());
            });
        }
        if (ObjectUtil.isNotEmpty(bo.getCategoryId())) {
            //查询类别下的商品列表
            CategoryVo categoryVo = categoryService.queryById(Long.valueOf(bo.getCategoryId()));
            if (ObjectUtil.isEmpty(categoryVo) || "1".equals(categoryVo.getStatus())) {
                return null;
            }
            List<CategoryProductVo> categoryProductVos =
                categoryProductService.queryList(Long.valueOf(bo.getCategoryId()));
            if (ObjectUtil.isEmpty(categoryProductVos)) {
                return null;
            }
            lqw.in(Product::getProductId, categoryProductVos.stream().map(CategoryProductVo::getProductId).collect(Collectors.toList()));
        }

        if (ObjectUtil.isNotEmpty(bo.getCommercialTenantId())) {
            //查询商户下的商品列表
            CommercialTenantVo commercialTenantVo = commercialTenantMapper.selectVoById(bo.getCommercialTenantId());
            if (ObjectUtil.isEmpty(commercialTenantVo) || "1".equals(commercialTenantVo.getStatus())) {
                return null;
            }
            List<CommercialTenantProductVo> commercialTenantProductVos =
                commercialTenantProductService.queryListByCommercialTenantId(Long.valueOf(bo.getCommercialTenantId()));
            if (ObjectUtil.isEmpty(commercialTenantProductVos)) {
                return null;
            }
            lqw.in(Product::getProductId, commercialTenantProductVos.stream().map(CommercialTenantProductVo::getProductId).collect(Collectors.toList()));
        }
        lqw.last("order by sort asc,update_time desc");
        return lqw;
    }

}
