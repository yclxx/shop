package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.resource.api.RemoteFileService;
import com.ruoyi.resource.api.domain.SysFile;
import com.ruoyi.zlyyh.domain.CategoryProduct;
import com.ruoyi.zlyyh.domain.CommercialTenantProduct;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.ProductInfo;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyh.param.LianLianParam;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.LianLianUtils;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.domain.bo.LianLianProductBo;
import com.ruoyi.zlyyhadmin.domain.vo.LianLianProductItem;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商品Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {

    private final ProductMapper baseMapper;
    private final ICategoryProductService categoryProductService;
    private final ICommercialTenantProductService commercialTenantProductService;
    private final IProductTicketService productTicketService;
    private final IProductTicketSessionService productTicketSessionService;
    private final IShopProductService shopProductService;
    private final IProductInfoService productInfoService;
    private final YsfConfigService ysfConfigService;
    @DubboReference
    private RemoteFileService remoteFileService;

    /**
     * 查询商品
     */
    @Override
    public ProductVo queryById(Long productId) {
        ProductVo productVo = baseMapper.selectVoById(productId);
        CategoryProductBo productBo = new CategoryProductBo();
        productBo.setProductId(productId);
        List<CategoryProductVo> categoryProducts = categoryProductService.queryList(productBo);
        if (ObjectUtil.isNotEmpty(categoryProducts)) {
            StringBuilder categoryId = new StringBuilder();
            for (CategoryProductVo categoryProduct : categoryProducts) {
                categoryId.append(categoryProduct.getCategoryId()).append(",");
            }
            if (StringUtils.isNotBlank(categoryId)) {
                productVo.setCategoryId(categoryId.substring(0, categoryId.length() - 1));
            }
        }
        CommercialTenantProductBo categoryProductBo = new CommercialTenantProductBo();
        categoryProductBo.setProductId(productId);
        List<CommercialTenantProductVo> commercialTenantProductVos = commercialTenantProductService.queryList(categoryProductBo);
        if (ObjectUtil.isNotEmpty(commercialTenantProductVos)) {
            StringBuilder commercialTenantProductId = new StringBuilder();
            for (CommercialTenantProductVo commercialTenantProductVo : commercialTenantProductVos) {
                commercialTenantProductId.append(commercialTenantProductVo.getCommercialTenantId()).append(",");
            }
            if (StringUtils.isNotBlank(commercialTenantProductId)) {
                productVo.setCommercialTenantId(commercialTenantProductId.substring(0, commercialTenantProductId.length() - 1));
            }
        }
        if (productVo.getProductType().equals("13")) {
            ProductTicketVo productTicketVo = productTicketService.queryByProductId(productId);
            if (ObjectUtil.isNotEmpty(productTicketVo)) {
                productVo.setTicket(productTicketVo);
            }
            ProductTicketSessionBo sessionBo = new ProductTicketSessionBo();
            sessionBo.setProductId(productId);
            List<ProductTicketSessionVo> ticketSessionVos = productTicketSessionService.queryLists(sessionBo);
            productVo.setTicketSession(ticketSessionVos);
        }
        //List<Long> shopIds = shopProductService.queryByProductId(productId);
        //if (!shopIds.isEmpty()) {
        //    productVo.setShopId(StringUtils.join(shopIds, ","));
        //}
        return productVo;
    }

    @Override
    public ProductVo queryByExternalProductId(String externalProductId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getExternalProductId, externalProductId);
        return baseMapper.selectVoOne(wrapper);
    }

    @Override
    public Product queryByExternalProductId(String externalProductId, String productType, Long platformKey) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getExternalProductId, externalProductId);
        wrapper.eq(Product::getProductType, productType);
        wrapper.eq(Product::getPlatformKey, platformKey);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 查询商品列表
     */
    @Override
    public TableDataInfo<ProductVo> queryPageList(ProductBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Product> lqw = buildQueryWrapper(bo);
        Page<ProductVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商品列表
     */
    @Override
    public List<ProductVo> queryList(ProductBo bo) {
        LambdaQueryWrapper<Product> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询商品下拉列表
     */
    @Override
    public List<ProductVo> queryProductList(ProductBo bo) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Product::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getSearchStatus()), Product::getSearchStatus, bo.getSearchStatus());
        lqw.and(lq -> lq.ge(Product::getShowEndDate, DateUtils.dateTimeNow()).or(e -> e.isNull(Product::getShowEndDate)));
        lqw.and(lq -> lq.ge(Product::getSellEndDate, DateUtils.dateTimeNow()).or(e -> e.isNull(Product::getSellEndDate)));
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Product> buildQueryWrapper(ProductBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.eq(null != bo.getProductId(), Product::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getExternalProductId()), Product::getExternalProductId, bo.getExternalProductId());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), Product::getProductName, bo.getProductName());
        lqw.eq(StringUtils.isNotBlank(bo.getProductAbbreviation()), Product::getProductAbbreviation, bo.getProductAbbreviation());
        lqw.eq(StringUtils.isNotBlank(bo.getProductSubhead()), Product::getProductSubhead, bo.getProductSubhead());
        lqw.eq(StringUtils.isNotBlank(bo.getProductImg()), Product::getProductImg, bo.getProductImg());
        lqw.eq(StringUtils.isNotBlank(bo.getProductAffiliation()), Product::getProductAffiliation, bo.getProductAffiliation());
        lqw.eq(StringUtils.isNotBlank(bo.getProductType()), Product::getProductType, bo.getProductType());
        lqw.eq(StringUtils.isNotBlank(bo.getPickupMethod()), Product::getPickupMethod, bo.getPickupMethod());
        lqw.eq(StringUtils.isNotBlank(bo.getShowOriginalAmount()), Product::getShowOriginalAmount, bo.getShowOriginalAmount());
        lqw.eq(bo.getOriginalAmount() != null, Product::getOriginalAmount, bo.getOriginalAmount());
        lqw.eq(bo.getSellAmount() != null, Product::getSellAmount, bo.getSellAmount());
        lqw.eq(bo.getVipUpAmount() != null, Product::getVipUpAmount, bo.getVipUpAmount());
        lqw.eq(bo.getVipAmount() != null, Product::getVipAmount, bo.getVipAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getToType()), Product::getToType, bo.getToType());
        lqw.eq(StringUtils.isNotBlank(bo.getAppId()), Product::getAppId, bo.getAppId());
        lqw.eq(StringUtils.isNotBlank(bo.getUrl()), Product::getUrl, bo.getUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Product::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getSearchStatus()), Product::getSearchStatus, bo.getSearchStatus());
        lqw.eq(bo.getSellStartDate() != null, Product::getSellStartDate, bo.getSellStartDate());
        lqw.eq(bo.getSellEndDate() != null, Product::getSellEndDate, bo.getSellEndDate());
        lqw.eq(StringUtils.isNotBlank(bo.getAssignDate()), Product::getAssignDate, bo.getAssignDate());
        lqw.eq(StringUtils.isNotBlank(bo.getWeekDate()), Product::getWeekDate, bo.getWeekDate());
        lqw.eq(StringUtils.isNotBlank(bo.getSellTime()), Product::getSellTime, bo.getSellTime());
        lqw.eq(bo.getTotalCount() != null, Product::getTotalCount, bo.getTotalCount());
        lqw.eq(bo.getMonthCount() != null, Product::getMonthCount, bo.getMonthCount());
        lqw.eq(bo.getWeekCount() != null, Product::getWeekCount, bo.getWeekCount());
        lqw.eq(bo.getDayCount() != null, Product::getDayCount, bo.getDayCount());
        lqw.eq(bo.getDayUserCount() != null, Product::getDayUserCount, bo.getDayUserCount());
        lqw.eq(bo.getWeekUserCount() != null, Product::getWeekUserCount, bo.getWeekUserCount());
        lqw.eq(bo.getMonthUserCount() != null, Product::getMonthUserCount, bo.getMonthUserCount());
        lqw.eq(bo.getTotalUserCount() != null, Product::getTotalUserCount, bo.getTotalUserCount());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), Product::getDescription, bo.getDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getProviderLogo()), Product::getProviderLogo, bo.getProviderLogo());
        lqw.like(StringUtils.isNotBlank(bo.getProviderName()), Product::getProviderName, bo.getProviderName());
        lqw.eq(StringUtils.isNotBlank(bo.getTags()), Product::getTags, bo.getTags());
        if (StringUtils.isNotEmpty(bo.getShowCity())) {
            lqw.and(lq -> lq.like(Product::getShowCity, bo.getShowCity()).or(e -> e.eq(Product::getShowCity, "ALL")));
        }
        lqw.eq(bo.getMerchantId() != null, Product::getMerchantId, bo.getMerchantId());
        lqw.eq(bo.getShopGroupId() != null, Product::getShopGroupId, bo.getShopGroupId());
        lqw.eq(StringUtils.isNotBlank(bo.getBtnText()), Product::getBtnText, bo.getBtnText());
        lqw.eq(StringUtils.isNotBlank(bo.getShareTitle()), Product::getShareTitle, bo.getShareTitle());
        lqw.like(StringUtils.isNotBlank(bo.getShareName()), Product::getShareName, bo.getShareName());
        lqw.eq(StringUtils.isNotBlank(bo.getShareImage()), Product::getShareImage, bo.getShareImage());
        lqw.eq(StringUtils.isNotBlank(bo.getSearch()), Product::getSearch, bo.getSearch());
        lqw.eq(StringUtils.isNotBlank(bo.getPayUser()), Product::getPayUser, bo.getPayUser());
        lqw.eq(StringUtils.isNotBlank(bo.getShowIndex()), Product::getShowIndex, bo.getShowIndex());
        lqw.eq(bo.getPlatformKey() != null, Product::getPlatformKey, bo.getPlatformKey());
        lqw.eq(bo.getSort() != null, Product::getSort, bo.getSort());
        lqw.between(params.get("beginStartDate") != null && params.get("endStartDate") != null, Product::getShowStartDate, params.get("beginStartDate"), params.get("endStartDate"));
        lqw.between(params.get("beginEndDate") != null && params.get("endEndDate") != null, Product::getShowEndDate, params.get("beginEndDate"), params.get("endEndDate"));
        return lqw;
    }

    /**
     * 设置商品城市
     *
     * @param productId 商品编号
     */
    public void setProductCity(Long productId) {
        if (null == productId) {
            return;
        }
        // 查询商品门店
        String s = shopProductService.queryCityCode(productId);
        if (StringUtils.isBlank(s)) {
            return;
        }
        Product product = new Product();
        product.setProductId(productId);
        // 修改商品城市
        product.setShowCity(s);
        baseMapper.updateById(product);
    }

    private void processCategory(Long productId, String categoryId, boolean update) {
        if (null == productId) {
            return;
        }
        if (ObjectUtil.isNotEmpty(categoryId)) {
            if (update) {
                categoryProductService.remove(new LambdaQueryWrapper<CategoryProduct>().eq(CategoryProduct::getProductId, productId));
            }
            String[] split = categoryId.split(",");
            for (String s : split) {
                CategoryProductBo categoryProduct = new CategoryProductBo();
                categoryProduct.setProductId(productId);
                categoryProduct.setCategoryId(Long.parseLong(s));
                categoryProductService.insertByBo(categoryProduct);
            }
        }
    }

    private void processCommercialTenantProduct(Long productId, String commercialTenantProductId, boolean update) {
        if (null == productId) {
            return;
        }
        if (ObjectUtil.isNotEmpty(commercialTenantProductId)) {
            if (update) {
                commercialTenantProductService.remove(new LambdaQueryWrapper<CommercialTenantProduct>().eq(CommercialTenantProduct::getProductId, productId));
            }
            String[] split = commercialTenantProductId.split(",");
            for (String s : split) {
                CommercialTenantProductBo commercialTenantProductBo = new CommercialTenantProductBo();
                commercialTenantProductBo.setProductId(productId);
                commercialTenantProductBo.setCommercialTenantId(Long.parseLong(s));
                commercialTenantProductService.insertByBo(commercialTenantProductBo);
            }
        }
    }

    /**
     * 新增商品
     */
    @CacheEvict(cacheNames = CacheNames.productList, allEntries = true)
    @Override
    public Boolean insertByBo(ProductBo bo) {
        Product add = BeanUtil.toBean(bo, Product.class);
        validEntityBeforeSave(add);
        PermissionUtils.setProductDeptIdAndUserId(add);
        if (null == add.getProductId()) {
            add.setProductId(IdUtil.getSnowflakeNextId());
        }
        if (add.getProductType().equals("13")) {
            bo.getTicket().setProductId(add.getProductId());
            bo.getTicket().setTicketId(IdUtil.getSnowflakeNextId());
            Boolean b = productTicketService.insertByBo(bo.getTicket());
            if (!b) throw new ServiceException("演出票添加异常。");
            productTicketSessionService.insertByBoList(bo.getTicketSession(), add.getProductId());
        }
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProductId(add.getProductId());
            processCategory(bo.getProductId(), bo.getCategoryId(), false);
            processCommercialTenantProduct(bo.getProductId(), bo.getCommercialTenantId(), false);
        }
        // 处理门店与产品关联表
        //if (StringUtils.isNotEmpty(bo.getShopId())) {
        //    String[] split = bo.getShopId().split(",");
        //    for (String s : split) {
        //        ShopProductBo shopBo = new ShopProductBo();
        //        shopBo.setProductId(bo.getProductId());
        //        shopBo.setShopId(Long.valueOf(s));
        //        shopProductService.insertByBo(shopBo);
        //    }
        //}
        setProductCity(add.getProductId());
        return flag;
    }

    public Boolean insert(ProductBo bo) {
        Product add = BeanUtil.toBean(bo, Product.class);
        validEntityBeforeSave(add);
        if (null == add.getProductId()) {
            add.setProductId(IdUtil.getSnowflakeNextId());
        }
        if (add.getProductType().equals("13")) {
            bo.getTicket().setProductId(add.getProductId());
            bo.getTicket().setTicketId(IdUtil.getSnowflakeNextId());
            Boolean b = productTicketService.insertByBo(bo.getTicket());
            if (!b) throw new ServiceException("演出票添加异常。");
            productTicketSessionService.insertByBoList(bo.getTicketSession(), add.getProductId());
        }
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProductId(add.getProductId());
        }
        return flag;
    }

    /**
     * 修改商品
     */
    @CacheEvict(cacheNames = CacheNames.PRODUCT, key = "#bo.getProductId()")
    @Override
    public Boolean updateByBo(ProductBo bo) {
        Product update = BeanUtil.toBean(bo, Product.class);
        validEntityBeforeSave(update);
        if (bo.getProductType().equals("13")) {
            Boolean b = productTicketService.updateByBo(bo.getTicket());
            if (!b) throw new ServiceException("演出票修改异常。");
            if (!bo.getTicketSession().isEmpty())
                productTicketSessionService.updateByBoList(bo.getTicketSession(), bo.getProductId());
        }
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
            processCategory(bo.getProductId(), bo.getCategoryId(), true);
            processCommercialTenantProduct(bo.getProductId(), bo.getCommercialTenantId(), true);
            try {
                CacheUtils.clear(CacheNames.productList);
            } catch (Exception ignored) {
            }
        }
        // 处理门店与产品关联表
        //if (StringUtils.isNotEmpty(bo.getShopId())) {
        //    shopProductService.deleteByProductId(bo.getProductId());
        //    String[] split = bo.getShopId().split(",");
        //    for (String s : split) {
        //        ShopProductBo shopBo = new ShopProductBo();
        //        shopBo.setProductId(bo.getProductId());
        //        shopBo.setShopId(Long.valueOf(s));
        //        shopProductService.insertByBo(shopBo);
        //    }
        //}
        setProductCity(update.getProductId());
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Product entity) {
        CacheUtils.clear(CacheNames.COMMERCIAL_PRODUCT);
        CacheUtils.clear(CacheNames.COMMERCIAL_PRODUCT_IDS);
    }

    /**
     * 批量删除商品
     */
    @CacheEvict(cacheNames = CacheNames.productList, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (ObjectUtil.isEmpty(ids)) {
            return false;
        }
        for (Long id : ids) {
            CacheUtils.evict(CacheNames.PRODUCT, id);
        }
        CacheUtils.clear(CacheNames.COMMERCIAL_PRODUCT);
        CacheUtils.clear(CacheNames.COMMERCIAL_PRODUCT_IDS);
        categoryProductService.remove(new LambdaQueryWrapper<CategoryProduct>().in(CategoryProduct::getProductId, ids));
        commercialTenantProductService.remove(new LambdaQueryWrapper<CommercialTenantProduct>().in(CommercialTenantProduct::getProductId, ids));
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 根据id批量下架商品
     *
     * @param ids
     */
    @Override
    public void updateProducts(Collection<Long> ids, String productType) {
        for (Long id : ids) {
            ProductBo productBo = new ProductBo();
            productBo.setProductId(id);
            productBo.setStatus("1");
            productBo.setProductType(productType);
            updateByBo(productBo);
        }

    }

    /**
     * 联联产品更新通知
     *
     * @param param
     */
    @Override
    public void lianProductCall(JSONObject param) {
        String secret = ysfConfigService.queryValueByKeys("LianLian.secret");
        log.info("新联联商品更新通知接收参数解密前：{}", param);
        String decryptedData = LianLianUtils.aesDecryptBack(param, secret);
        log.info("新联联商品更新通知接收参数解密后：{}", decryptedData);
        if (ObjectUtil.isNotEmpty(decryptedData)) {
            BigDecimal HUNDRED = new BigDecimal(100);
            JSONObject jsonObject = JSONObject.parseObject(decryptedData);
            LianLianProductBo lianProduct = jsonObject.toJavaObject(LianLianProductBo.class);
            List<LianLianProductItem> items = JSONArray.parseArray(lianProduct.getItemList(), LianLianProductItem.class);
            for (LianLianProductItem item : items) {
                ProductInfoVo productInfoVo = productInfoService.queryByItemId(item.getItemId().toString());
                if (ObjectUtil.isEmpty(productInfoVo)) continue;
                Product product = baseMapper.selectById(productInfoVo.getProductId());
                // 处理价格单位
                item.setChannelPrice(item.getChannelPrice().divide(HUNDRED, 2, RoundingMode.HALF_UP)); // 渠道结算价
                item.setSalePrice(item.getSalePrice().divide(HUNDRED, 2, RoundingMode.HALF_UP)); // 售价
                item.setOriginPrice(item.getOriginPrice().divide(HUNDRED, 2, RoundingMode.HALF_UP));// 原价
                //主体信息
                if (lianProduct.getOnlyName().equals(item.getSubTitle())) {
                    product.setProductName(lianProduct.getOnlyName());//产品名称
                } else {
                    product.setProductName(lianProduct.getOnlyName() + " " + item.getSubTitle());//产品名称
                }
                product.setProductImg(lianProduct.getFaceImg());//产品图片
                product.setProductSubhead(lianProduct.getTitle());//产品副标题
                product.setOriginalAmount(item.getOriginPrice());//产品原价
                product.setSellAmount(item.getSalePrice());//产品售价
                if (lianProduct.getItemStock().equals(0)) {
                    product.setTotalCount(Long.valueOf(lianProduct.getStockAmount()));//库存
                } else if (lianProduct.getItemStock().equals(1)) {
                    product.setTotalCount(item.getStock());//库存
                }


                String channelId = ysfConfigService.queryValueByKey(product.getPlatformKey(), "LianLian.channelId");
                String basePath = ysfConfigService.queryValueByKey(product.getPlatformKey(), "LianLian.basePath");
                //产品图文详情
                String detailHtml = ysfConfigService.queryValueByKey(product.getPlatformKey(), "LianLian.detailHtml");

                // 查询产品图文详情(文案)
                String htmlContent = null;
                String spxz = null;
                JSONObject htmlObject = LianLianUtils.getProductDetail(channelId, secret, basePath + detailHtml, lianProduct.getProductId().toString());
                if (htmlObject != null) {
                    htmlContent = htmlObject.getString("htmlContent");
                    if (StringUtils.isNotEmpty(htmlContent)) { // 获取商品须知
                        //Document doc = Jsoup.parse(htmlContent);
                        //Elements elementsByClass = doc.getElementsByClass("body-center body-center-border-bottom");
                        //Element body = doc.body();
                        //body.html(elementsByClass.html());
                        //// 获取联联商品购买须知内容
                        //spxz = doc.outerHtml();
                        String fileName = product.getProductId() + ".html";
                        InputStream inputStream = new ByteArrayInputStream(htmlContent.getBytes());
                        byte[] bytes = IoUtil.readBytes(inputStream);
                        SysFile upload = remoteFileService.upload(fileName, fileName, "text/html;charset:utf-8", bytes);
                        spxz = upload.getUrl();
                    }
                }
                product.setDescription(spxz);

                //product.setVipAmount(item.getChannelPrice());
                ProductInfo productInfo = new ProductInfo();
                productInfo.setTitle(lianProduct.getTitle());
                productInfo.setMainPicture(lianProduct.getFaceImg());
                productInfo.setActivityPriceCent(item.getSalePrice());
                productInfo.setOriginalPriceCent(item.getOriginPrice());
                productInfo.setStock(product.getTotalCount());
                productInfo.setDiscount(item.getSalePrice().divide(item.getOriginPrice(), 2, RoundingMode.HALF_UP).toString());
                // 使用时间
                productInfo.setTicketTimeRule(lianProduct.getValidBeginDate() + "-" + lianProduct.getValidEndDate());
                productInfo.setBrandName(lianProduct.getOnlyName());
                //productInfo.setItemBuyNote(spxz);
                productInfo.setItemContentGroup(JSONObject.toJSONString(items).replace("&", ""));
                // 将原先海报地址改为套餐图片
                productInfo.setItemContentImage(lianProduct.getFaceImg());
                productInfo.setBuyLimit(Long.valueOf(lianProduct.getSingleMax()));
                productInfo.setReserveDesc(lianProduct.getAttention()); // 订单注意事项配置补充说明
                //String appointMent = lianProductVo.getBookingType().equals("0") ? "1" : lianProductVo.getBookingType().equals("1") ? "3" : "2";
                //productInfo.setAppointMent(appointMent);
                // 如果需要填身份证，日期，配送地址 设置产品为下架状态
                if (StringUtils.isNotEmpty(lianProduct.getBeginTime())) {
                    product.setShowStartDate(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, lianProduct.getBeginTime()));
                    productInfo.setSaleStartTime(lianProduct.getBeginTime());
                }
                if (StringUtils.isNotEmpty(lianProduct.getEndTime())) {
                    product.setShowEndDate(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, lianProduct.getEndTime()));
                    productInfo.setSaleEndTime(lianProduct.getEndTime());
                }
                if (!"0".equals(lianProduct.getBookingShowAddress()) || !"0".equals(lianProduct.getOrderShowDate()) || !"0".equals(lianProduct.getOrderShowIdCard())) {
                    //将状态改成下架
                    product.setStatus("1");
                }
                //存在则修改
                baseMapper.updateById(product);
                ProductInfoBo productInfoBo = BeanUtil.toBean(productInfo, ProductInfoBo.class);
                productInfoService.updateByBo(productInfoBo);
            }
        }
    }


    /**
     * 联联订单状态通知(上架/下架/售空)
     *
     * @param param
     */
    @Override
    public void lianProductStatusCall(JSONObject param) {
        String secret = ysfConfigService.queryValueByKeys("LianLian.secret");
        log.info("新联联商品上架下架售罄通知接收参数解密前：{}", param);
        String decryptedData = LianLianUtils.aesDecryptBack(param, secret);
        log.info("新联联商品上架下架售罄通知接收参数解密后：{}", decryptedData);
        if (ObjectUtil.isNotEmpty(decryptedData)) {
            LianLianParam.ProductStateParam productStateParam =
                JSONObject.parseObject(decryptedData, LianLianParam.ProductStateParam.class);
            if (productStateParam != null && StringUtils.isNotEmpty(productStateParam.getItems())) {
                String[] items = productStateParam.getItems().replace("[", "").replace("]", "").split(",");
                for (String itemId : items) {
                    ProductInfoVo productInfoVo = productInfoService.queryByItemId(itemId);
                    if (ObjectUtil.isNotEmpty(productInfoVo)) {
                        Product product = baseMapper.selectById(productInfoVo.getProductId());
                        if (productStateParam.getType() == 2) {
                            //如果是售罄，则将状态改成下架，并且库存改成0
                            product.setStatus("1");
                        } else if (productStateParam.getType() == 0) {
                            product.setStatus("1");
                        } else if (productStateParam.getType() == 1) {
                            product.setStatus("0");
                        }
                        baseMapper.updateById(product);
                    }
                }
            }

        }
    }


}
