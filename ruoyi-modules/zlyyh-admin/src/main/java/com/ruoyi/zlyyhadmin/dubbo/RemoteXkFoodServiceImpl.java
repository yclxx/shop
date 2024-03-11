package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.RemoteXkFoodService;
import com.ruoyi.zlyyh.domain.CategorySupplier;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.ShopProduct;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyh.mapper.ShopProductMapper;
import com.ruoyi.zlyyh.properties.CtripConfig;
import com.ruoyi.zlyyh.properties.XKConfig;
import com.ruoyi.zlyyh.utils.XkUtils;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 享库接口
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteXkFoodServiceImpl implements RemoteXkFoodService {
    private static final XKConfig xkConfig = SpringUtils.getBean(XKConfig.class);

    @Autowired
    private IProductService productService;
    @Autowired
    private IProductInfoService productInfoService;
    @Autowired
    private ICommercialTenantService commercialTenantService;
    @Autowired
    private ICommercialTenantProductService commercialTenantProductService;
    @Autowired
    private ICategorySupplierService categorySupplierService;
    @Autowired
    private IShopService shopService;
    @Autowired
    private ICategoryProductService categoryProductService;
    private final ShopProductMapper shopProductMapper;
    private final ProductMapper productMapper;
    private Map<String, Long> productHashMap;
    @Override
    public void getXkFoodList(Long platformKey) {
        productHashMap = new HashMap<>();
        Integer pageIndex = 1;
        Integer pageSize = 20;
        String url = xkConfig.getUrl();
        String appId = xkConfig.getAppId();
        String appSecret = xkConfig.getAppSecret();
        String sourceType = xkConfig.getSourceType();
        while (true) {
            JSONObject productList = XkUtils.getProductList(pageIndex, pageSize, url, appId, appSecret, sourceType);
            if (null == productList) {
                continue;
            }
            JSONObject data = productList.getJSONObject("data");
            if (null == data){
                continue;
            }
            //获取享库商品列表
            JSONArray dataList = data.getJSONArray("data");
            if (ObjectUtil.isEmpty(dataList)){
                continue;
            }
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject productObject = dataList.getJSONObject(i);
                ThreadUtil.sleep(3000);
                String goods_id = productObject.getString("goods_id");
                if (ObjectUtil.isEmpty(goods_id)){
                    continue;
                }
                //查询到商品id 请求接口查询商品详情
                JSONObject productInfo = XkUtils.getProductInfo(url, appId, appSecret, goods_id, sourceType);
                if (null == productInfo){
                    continue;
                }
                JSONObject productData = productInfo.getJSONObject("data");
                //查询商品是否存在
                ProductVo productVo = productService.queryByExternalProductId(goods_id);
                ProductBo productBo = new ProductBo();
                ProductInfoBo productInfoBo = new ProductInfoBo();
                CategorySupplier categorySupplier = new CategorySupplier();
                try {
                    categorySupplier = setProduct(productData, productBo, goods_id, categorySupplier);
                    setProductInfo(productData, productInfoBo,goods_id);
                }catch (Exception e){
                    continue;
                }
                if (ObjectUtil.isNotEmpty(productVo)) {
                    //商品不为空 修改商品
                    //商品修改为可以在用户侧退款
                    productBo.setCusRefund("1");
                    productBo.setProductId(productVo.getProductId());
                    productBo.setExternalProductId(goods_id);
                    productService.updateByBo(productBo);
                    productInfoBo.setProductId(productVo.getProductId());
                    ProductInfoVo productInfoVo = productInfoService.queryById(productVo.getProductId());
                    if (ObjectUtil.isEmpty(productInfoVo)) {
                        productInfoService.insertByBo(productInfoBo);
                    } else {
                        productInfoService.updateByBo(productInfoBo);
                    }
                } else {
                    //新增商品
                    //商品修改为可以在用户侧退款
                    productBo.setCusRefund("1");
                    productBo.setExternalProductId(goods_id);
                    productService.insertByBo(productBo);
                    productInfoBo.setProductId(productBo.getProductId());
                    productInfoService.insertByBo(productInfoBo);
                }
                //二次查询商品
                productVo = productService.queryByExternalProductId(goods_id);
                //处理品牌信息
                Long commercialTenantId = setBrandAndCommercialTenant(productData, productVo.getProductId());
                //处理门店信息
                setShop(productData,productVo.getProductId(),commercialTenantId);
                //自动分类
                try {
                    if (ObjectUtil.isNotNull(categorySupplier) && ObjectUtil.isNotNull(categorySupplier.getCategoryId())) {
                        categoryProductLink(categorySupplier.getCategoryId(),productVo.getProductId());
                    }
                }catch (Exception e){
                    log.error("商品Id：{}自动分类失败",productBo.getProductId());
                }

            }

            Integer total = data.getInteger("total");
            if (pageIndex * pageSize >= total) {
                // 如果获取的数据超过了总条数，终止循环
                log.info("新享库产品获取完成：total=" + total + ",pageIndex=" + pageIndex);
                break;
            }
            pageIndex ++;
            ThreadUtil.sleep(3000);
        }
    }



    /**
     * 商品赋值
     */
    private CategorySupplier setProduct(JSONObject data, ProductBo productBo,String goodsId,CategorySupplier categorySupplier) {
        JSONArray goods_sku = data.getJSONArray("goods_sku");
        if (ObjectUtil.isEmpty(goods_sku)){
            throw new ServiceException("商品id"+goodsId+"未获取到价格");
        }
        JSONObject priceObject = goods_sku.getJSONObject(0);
        //门市价
        BigDecimal line_price = priceObject.getBigDecimal("line_price");
        //售价
        BigDecimal goods_price = priceObject.getBigDecimal("goods_price");
        Long stock_num = priceObject.getLongValue("stock_num");
        //这里把供应商商品分类加一下
        try{
            JSONArray newCategory = data.getJSONArray("new_category");
            //获取新享库商品分类
            if (ObjectUtil.isNotEmpty(newCategory) && newCategory.size()>0) {
                String fullName = "";
                for (int j = 0; j <newCategory.size() ; j++) {
                    fullName +=newCategory.getJSONObject(j).getString("name") +"/";
                }
                //查询分类表
                categorySupplier = categorySupplierService.queryBySupplierId("23", fullName);
                if (ObjectUtil.isEmpty(categorySupplier)) {
                    categorySupplier = new CategorySupplier();
                    categorySupplier.setSupplierId("23");
                    categorySupplier.setSupplierName("新享库商品");
                    categorySupplier.setFullName(fullName);
                    categorySupplierService.insert(categorySupplier);
                }
            }
        }catch (Exception e){
            log.error("新增分类报错");
        }
        JSONObject goods = data.getJSONObject("goods");
        String goods_name = goods.getString("goods_name");
        String subtitle_title = goods.getString("subtitle_title");
        JSONArray image = goods.getJSONArray("image");
        String productImg = "";
        if (!CollectionUtils.isEmpty(image)) {
            JSONObject jsonObject3 = image.getJSONObject(0);
            productImg = jsonObject3.getString("file_path");

        } else {
            //图片可能没有 先查同名称商品 要是还是没有 抛出异常跳过
            ProductVo productVo = productMapper.selectVoOne(new LambdaQueryWrapper<Product>().eq(Product::getProductSubhead, goods_name).isNotNull(Product::getProductImg).last("limit 1"));
            if (ObjectUtil.isNotEmpty(productVo.getProductImg())){
                productImg = productVo.getProductImg();

            }
            if (ObjectUtil.isEmpty(productImg)){
                throw new ServiceException("无商品图片跳过");
            }
        }
        productBo.setProductImg(productImg);
        //售卖时间
        if (ObjectUtil.isNotEmpty(goods.getString("uppertime"))){
            productBo.setSellStartDate(DateUtils.TimesToDate(goods.getString("uppertime")));
            productBo.setShowStartDate(DateUtils.TimesToDate(goods.getString("uppertime")));
        }
        if (ObjectUtil.isNotEmpty(goods.getString("under_time"))){
            productBo.setShowEndDate(DateUtils.TimesToDate(goods.getString("under_time")));
            productBo.setSellEndDate(DateUtils.TimesToDate(goods.getString("under_time")));
        }

        //可能需要加一个商品详情链接

        productBo.setExternalProductId(goodsId);
        if (ObjectUtil.isNotEmpty(subtitle_title)){
            productBo.setProductName(subtitle_title);
        }else {
            productBo.setProductName(goods_name);
        }
//        productBo.setProductAbbreviation(subtitle_title);
        productBo.setProductSubhead(goods_name);
        productBo.setOriginalAmount(line_price);
        productBo.setSellAmount(goods_price);
        productBo.setTotalCount(stock_num);
        //接口进来类型为新享库美食套餐
        productBo.setProductType("23");
        productBo.setPickupMethod("1");
        productBo.setShowOriginalAmount("1");
        productBo.setProductAffiliation("0");
        //重新调整为上架状态
        productBo.setStatus("0");
        productBo.setLineUpperLimit(1L);
        productBo.setSupplier("1765214556564279298");
        return categorySupplier;
    }

    /**
     * 商品详情赋值
     */
    /**
     * 商品详情赋值
     */
    private void setProductInfo(JSONObject data, ProductInfoBo productInfoBo,String goodsId) {
        JSONObject distribution = data.getJSONObject("distribution");
        //结算价
        BigDecimal supply_price = distribution.getBigDecimal("supply_price");
        JSONArray goods_sku = data.getJSONArray("goods_sku");
        if (ObjectUtil.isEmpty(goods_sku)){
            throw new ServiceException("商品id"+goodsId+"未获取到价格");
        }
        JSONObject priceObject = goods_sku.getJSONObject(0);
        //门市价
        BigDecimal line_price = priceObject.getBigDecimal("line_price");
        //售价
        BigDecimal goods_price = priceObject.getBigDecimal("goods_price");
        Long stock_num = priceObject.getLongValue("stock_num");
        BigDecimal divide = goods_price.divide(line_price, 2, RoundingMode.HALF_UP);
        JSONObject goods = data.getJSONObject("goods");
        String goods_name = goods.getString("goods_name");
        String image = goods.getString("image");
        String setmeal = goods.getString("setmeal");
        //购买须知
        String byKnow = goods.getString("by_know");
        if (ObjectUtil.isNotEmpty(byKnow)) {
            byKnow = StringEscapeUtils.unescapeHtml3(byKnow);
            if (ObjectUtil.isNotEmpty(setmeal)) {
                byKnow = byKnow.replace(setmeal, "");
            }
            while (true) {
                if (byKnow.startsWith("<br>")) {
                    byKnow = byKnow.replaceFirst("<br>", "");
                } else {
                    break;
                }
            }
            byKnow = StringUtils.replaceHtml(byKnow);
        }
        if (ObjectUtil.isNotEmpty(setmeal)) {
            int k = 1;
            while (true) {
                if (setmeal.contains("&lt;")) {
                    setmeal = StringEscapeUtils.unescapeHtml3(setmeal);
                    setmeal = StringUtils.replaceHtml(setmeal);
                    k++;
                } else {
                    break;
                }
                if (k == 4) {
                    break;
                }
            }
        }


        productInfoBo.setItemPrice(supply_price);
        //折扣计算
        productInfoBo.setDiscount(divide.toString());
        productInfoBo.setItemId(goodsId);
        productInfoBo.setTitle(goods_name);
        productInfoBo.setActivityPriceCent(goods_price);
        productInfoBo.setOriginalPriceCent(line_price);
        productInfoBo.setItemBuyNote(byKnow);
        productInfoBo.setItemContentImage(image);
        productInfoBo.setItemContentGroup(setmeal);
        //有效时间
        String validityStartTime = goods.getString("validity_starttime");
        String ticketTimeRule = "";
        String[] split = validityStartTime.split("\"");
        if (split.length == 5) {
            String s = DateUtils.TimesToDateString(split[1]);
            String s1 = DateUtils.TimesToDateString(split[3]);
            ticketTimeRule = s+"至"+s1;
        }
        productInfoBo.setTicketTimeRule(ticketTimeRule);
//        productInfoBo.setUseNote(byKnow);
        productInfoBo.setStock(stock_num);


    }
    /**
     * 商户赋值
     *
     * @param data
     * @param commercialTenantBo
     */
    private void setCommercialTenant(JSONObject data, CommercialTenantBo commercialTenantBo) {
        String brandName = data.getString("name");
        commercialTenantBo.setCommercialTenantName(brandName);

    }
    /**
     * 商户商品赋值
     */
    private void setCommercialTenantProduct(Long commercialTenantId, Long productId) {
        CommercialTenantProductBo commercialTenantProductBo = new CommercialTenantProductBo();
        commercialTenantProductBo.setCommercialTenantId(commercialTenantId);
        commercialTenantProductBo.setProductId(productId);
        List<CommercialTenantProductVo> commercialTenantProductVos = commercialTenantProductService.queryList(commercialTenantProductBo);
        if (ObjectUtil.isEmpty(commercialTenantProductVos)) {
            commercialTenantProductService.insertByBo(commercialTenantProductBo);
        }
    }

    private Long setBrandAndCommercialTenant(JSONObject productData, Long productId) {
        JSONObject business = productData.getJSONObject("business");
        CommercialTenantVo commercialTenantVo = commercialTenantService.queryByCommercialTenantName(business.getString("name"));
        CommercialTenantBo commercialTenantBo = new CommercialTenantBo();
        if (ObjectUtil.isNotEmpty(commercialTenantVo)) {
            //商户存在 - 修改
            setCommercialTenant(business, commercialTenantBo);
            commercialTenantBo.setCommercialTenantId(commercialTenantVo.getCommercialTenantId());
            commercialTenantService.updateByBo(commercialTenantBo);
        } else {
            //商户不存在 - 新增
            setCommercialTenant(business, commercialTenantBo);
            commercialTenantBo.setSource("1");
            commercialTenantService.insertByBo(commercialTenantBo);
        }
        //关联商户商品
        setCommercialTenantProduct(commercialTenantBo.getCommercialTenantId(),productId);
        return commercialTenantBo.getCommercialTenantId();

    }

    /**
     * 商品门店赋值
     */
    private void setProductShop(Long productId, Long shopId) {
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setShopId(shopId);
        shopProduct.setProductId(productId);
        //因为门店是先删除后新增 所以这边也只有新增操作
        shopProductMapper.insert(shopProduct);
    }
    /**
     * 门店赋值
     */
    private void setShop(JSONObject data,Long productId,Long commercialTenantId) {
        JSONObject business = data.getJSONObject("business");
        JSONArray store = business.getJSONArray("store");
        for (int i = 0; i < store.size(); i++) {
            JSONObject storeJSONObject = store.getJSONObject(i);
            BigDecimal lng = storeJSONObject.getBigDecimal("lng");
            BigDecimal lat = storeJSONObject.getBigDecimal("lat");
            String poi_id = storeJSONObject.getString("poi_id");
            String address = storeJSONObject.getString("detailed_address");
            String name = storeJSONObject.getString("name");
            if (ObjectUtil.isNotEmpty(lat) && ObjectUtil.isNotEmpty(lng)) {
                ShopBo shopBo = new ShopBo();
                shopBo.setSupplierShopId(poi_id);
                shopBo.setShopName(name);
                shopBo.setLongitude(lng);
                shopBo.setLatitude(lat);
                shopBo.setAddress(address);
                shopBo.setCommercialTenantId(commercialTenantId);
                ShopVo shopVo = shopService.queryByNameAndSupplierId(name,poi_id);
                if (ObjectUtil.isEmpty(shopVo)) {
                    shopService.insertByBo(shopBo);
                } else {
                    shopBo.setShopId(shopVo.getShopId());
                    shopService.updateByBo(shopBo);
                }
                ShopProductVo shopProductVo = shopProductMapper.selectVoOne(new LambdaQueryWrapper<ShopProduct>().eq(ShopProduct::getProductId, productId).eq(ShopProduct::getShopId, shopBo.getShopId()));
                if (ObjectUtil.isEmpty(shopProductVo)){
                    setProductShop(productId,shopBo.getShopId());
                }

            }

        }



    }
    private void categoryProductLink(String categoryIds,Long productId){
        //此处自动关联商品栏目
        String[] categorySplit = categoryIds.split(",");
        for (int n = 0; n < categorySplit.length; n++) {
            Long l = Long.valueOf(categorySplit[n]);
            Long categoryProduct = categoryProductService.queryByCategoryAndProduct(l, productId);
            if (ObjectUtil.isNull(categoryProduct) || categoryProduct == 0) {
                CategoryProductBo categoryProductBo = new CategoryProductBo();
                categoryProductBo.setProductId(productId);
                categoryProductBo.setCategoryId(l);
                categoryProductService.insertByBo(categoryProductBo);
            }
        }
    }





}
