package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteCtripFoodService;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.zlyyh.domain.bo.ProductInfoBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.bo.ShopProductBo;
import com.ruoyi.zlyyh.domain.vo.ProductInfoVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.domain.vo.ShopProductVo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.properties.CtripConfig;
import com.ruoyi.zlyyh.utils.CtripUtils;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 携程接口
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteCtripFoodServiceImpl implements RemoteCtripFoodService {
    private static final CtripConfig CtripConfig = SpringUtils.getBean(CtripConfig.class);

    @Autowired
    private IProductService productService;
    @Autowired
    private IProductInfoService productInfoService;
    @Autowired
    private ICommercialTenantService commercialTenantService;
    @Autowired
    private ICommercialTenantProductService commercialTenantProductService;
    @Autowired
    private IShopService shopService;
    @Autowired
    private IShopProductService shopProductService;
    private Map<String, Long> productHashMap;

    private final String REFRESHTOKEN = "ctripRefreshToken";

    private final String ACCESSTOKEN = "ctripAccessToken";

    @Async
    @Override
    public void getCtripFoodList(Long platformKey) {
        // 唯一标识（指定key + url + 消息头）
        String cacheRepeatKey = Constants.REPEAT_SUBMIT_KEY + platformKey;
        String key = RedisUtils.getCacheObject(cacheRepeatKey);
        if (key == null) {
            RedisUtils.setCacheObject(cacheRepeatKey, "", Duration.ofMillis(3000));
        } else {
            return;
        }
        productHashMap = new HashMap<>();
        Integer pageIndex = 1;
        Integer pageSize = 20;
        while (true) {
            String accessToken = RedisUtils.getCacheObject(ACCESSTOKEN);
            if (ObjectUtil.isEmpty(accessToken)) {
                accessToken = getAccessToken();
            }
            String shopListUrl = CtripConfig.getUrl() + "?AID=" + CtripConfig.getAid() + "&SID=" + CtripConfig.getSid() +
                "&ICODE=" + CtripConfig.getShopCode() + "&Token=" + accessToken;
            String shopProductListUrl = CtripConfig.getUrl() + "?AID=" + CtripConfig.getAid() + "&SID=" + CtripConfig.getSid() +
                "&ICODE=" + CtripConfig.getShopProductCode() + "&Token=" + accessToken;
            String productInfoUrl = CtripConfig.getUrl() + "?AID=" + CtripConfig.getAid() + "&SID=" + CtripConfig.getSid() +
                "&ICODE=" + CtripConfig.getProductInfoCode() + "&Token=" + accessToken;
            //先查询门店列表 根据门店查询商品套餐
            JSONObject shopListObject = CtripUtils.getShopList(pageSize, pageIndex, CtripConfig.getPartnerType(), shopListUrl);
            if (ObjectUtil.isEmpty(shopListObject)){
                break;
            }
            JSONArray restaurants = shopListObject.getJSONArray("restaurants");
            if (ObjectUtil.isEmpty(restaurants)){
                break;
            }
            //在这里取数据
            //对方可能中途下架商品 无通知 所以这里做下架处理
            for (int i = 0; i < restaurants.size(); i++) {
                JSONObject restaurantObject = restaurants.getJSONObject(i);
                Long shopId = setShop(restaurantObject, platformKey);
                List<ShopProductVo> shopProductVos = shopProductService.queryByShopId(shopId);
                //查询出之前与门店关联的商品id 然后先将这些商品下架
                List<Long> productIds = shopProductVos.stream().map(ShopProductVo::getProductId).collect(Collectors.toList());
                productService.updateProducts(productIds,"15");
                //完成下架后 删除商品门店的关联
                shopProductService.deleteWithValidByShopId(shopId);
                //查询门店商品接口 获取商品
                JSONObject shopProductList = CtripUtils.getShopProductList(CtripConfig.getPartnerType(), restaurantObject.getString("poiId"), shopProductListUrl);
                //请求完成后休眠1秒
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JSONArray products = shopProductList.getJSONArray("products");
                //非空判断
                if (ObjectUtil.isEmpty(products)){
                    continue;
                }
                for (int j = 0; j < products.size(); j++) {
                    JSONObject productJSONObject = products.getJSONObject(j);
                    String xcProductId = productJSONObject.getString("productId");
                    Long productId = productHashMap.get(xcProductId);
                    if (productId == null){
                        //如果这个id没有存在过请求接口
                        JSONObject productInfo = CtripUtils.getProductInfo(CtripConfig.getPartnerType(), xcProductId, productInfoUrl);
                        if (ObjectUtil.isEmpty(productInfo)){
                            continue;
                        }
                        JSONObject product = productInfo.getJSONObject("product");

                        //查询商品是否存在
                        ProductVo productVo = productService.queryByExternalProductId(xcProductId);
                        ProductBo productBo = new ProductBo();
                        ProductInfoBo productInfoBo = new ProductInfoBo();
                        try {
                            setProduct(product, productBo);
                            setProductInfo(product, productInfoBo);
                        }catch (Exception e){
                            continue;
                        }
                        if (ObjectUtil.isNotEmpty(productVo)) {
                            //商品不为空 修改商品
                            //商品修改为可以在用户侧退款
                            productBo.setCusRefund("1");
                            productBo.setProductId(productVo.getProductId());
                            productBo.setExternalProductId(xcProductId);
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
                            productBo.setExternalProductId(xcProductId);
                            productService.insertByBo(productBo);
                            productInfoBo.setProductId(productBo.getProductId());
                            productInfoService.insertByBo(productInfoBo);
                        }
                        productId = productBo.getProductId();
                        productHashMap.put(xcProductId, productBo.getProductId());
                        //关联门店商品
                        setProductShop(productBo.getProductId(), shopId);
                    }else {
                        //关联商品门店
                        setProductShop(productId, shopId);
                    }
                    productService.setProductCity(productId);
                }
            }

            //判断是否还有下一页
            Boolean hasNext = shopListObject.getBoolean("hasNext");
            if (!hasNext) {
                log.info("携程商品获取完成，共获取：" + pageIndex * pageSize + "条记录！");
                break;
            }
            pageIndex++;
        }



    }

    /**
     * 商品门店赋值
     */
    private void setProductShop(Long productId, Long shopId) {
        ShopProductBo shopProductBo = new ShopProductBo();
        shopProductBo.setShopId(shopId);
        shopProductBo.setProductId(productId);
        //因为门店是先删除后新增 所以这边也只有新增操作
        shopProductService.insertByBo(shopProductBo);
    }

    /**
     * 商品赋值
     */
    private void setProduct(JSONObject data, ProductBo productBo) {
        String xcProductId = data.getString("productId");
        String productName = data.getString("productName");
        JSONArray pictures = data.getJSONArray("pictures");
        if (ObjectUtil.isNotEmpty(pictures)){
            JSONObject jsonObject = pictures.getJSONObject(0);
            String picUrl = jsonObject.getString("url");
            productBo.setProductImg(picUrl);
        }
        String commoditySubhead = data.getString("subtitle");
        //价格处理 需要*1.05的系数
        JSONObject priceJson = data.getJSONObject("price");
        BigDecimal vipPrice = data.getBigDecimal("distributorSettleAmount"); //结算价
        BigDecimal originPrice = new BigDecimal("0.00"); // 原价
        BigDecimal salePrice = new BigDecimal("0.00"); //原售价
        BigDecimal newSalePrice = null; //新的售价
        if(ObjectUtil.isNotEmpty(priceJson)){
            JSONObject originPriceJson = priceJson.getJSONObject("originPrice");
            if(ObjectUtil.isNotEmpty(originPriceJson)){
                originPrice = originPriceJson.getBigDecimal("price");
            }
            JSONObject salePriceJson = priceJson.getJSONObject("salePrice");
            if(ObjectUtil.isNotEmpty(salePriceJson)){
                salePrice = salePriceJson.getBigDecimal("price");
            }
        }
        //1、如果售价小于10元直接略过
        if(salePrice.compareTo(new BigDecimal("10.00")) < 0){
            throw new ServiceException("售价小于10元");
        }
        //2、售价乘1.05取整
        newSalePrice = salePrice.multiply(new BigDecimal("1.05")).setScale(0,BigDecimal.ROUND_UP);
        //3、如果加价后大于原价直接略过
        if(newSalePrice.compareTo(originPrice) > -1){
            throw new ServiceException("加价后大于原价");
        }
        //4.新售价减结算价低于0.5也直接跳过
        if (newSalePrice.subtract(vipPrice).compareTo(new BigDecimal("0.5")) == -1){
            throw new ServiceException("新售价结算价低于0.5");
        }
        Long repertory = data.getLong("totalStock");
        JSONObject buyRuleJson = data.getJSONObject("buyRule");
        if(ObjectUtil.isNotEmpty(buyRuleJson)){
            JSONObject saleDateRangeJson = buyRuleJson.getJSONObject("saleDateRange");
            if(ObjectUtil.isNotEmpty(saleDateRangeJson)){
                String startDate = saleDateRangeJson.getString("startDate");
                if(ObjectUtil.isNotEmpty(startDate)){
                    productBo.setShowStartDate(DateUtils.TimesToDate(startDate));
                    productBo.setSellStartDate(DateUtils.TimesToDate(startDate));
                }
                String endDate = saleDateRangeJson.getString("endDate");
                if(ObjectUtil.isNotEmpty(endDate)){
                    productBo.setShowEndDate(DateUtils.TimesToDate(endDate));
                    productBo.setSellEndDate(DateUtils.TimesToDate(endDate));
                }
            }
        }
        productBo.setExternalProductId(xcProductId);
        productBo.setProductName(productName);
        productBo.setProductAbbreviation(commoditySubhead);
        productBo.setProductSubhead(commoditySubhead);
        productBo.setOriginalAmount(originPrice);
        productBo.setSellAmount(newSalePrice);
        productBo.setTotalCount(repertory);
        //接口进来类型为携程美食套餐
        productBo.setProductType("15");
        productBo.setPickupMethod("1");
        productBo.setShowOriginalAmount("1");
        productBo.setProductAffiliation("0");
        //重新调整为上架状态
        productBo.setStatus("0");
        productBo.setLineUpperLimit(1L);
        productBo.setSupplier("1711999541405609985");
    }

    /**
     * 商品详情赋值
     */
    /**
     * 商品详情赋值
     */
    private void setProductInfo(JSONObject data, ProductInfoBo productInfoBo) {
        String title = data.getString("productName");
        String itemId = data.getString("productId");
        JSONArray pictures = data.getJSONArray("pictures");
        if (ObjectUtil.isNotEmpty(pictures)){
            JSONObject jsonObject = pictures.getJSONObject(0);
            String picUrl = jsonObject.getString("url");
            productInfoBo.setMainPicture(picUrl);
        }
        JSONObject buyRuleJson = data.getJSONObject("buyRule");
        if(ObjectUtil.isNotEmpty(buyRuleJson)){
            JSONObject saleDateRangeJson = buyRuleJson.getJSONObject("saleDateRange");
            if(ObjectUtil.isNotEmpty(saleDateRangeJson)){
                String startDate = saleDateRangeJson.getString("startDate");
                if(ObjectUtil.isNotEmpty(startDate)){
                    productInfoBo.setSaleStartTime(DateUtils.dateTime(DateUtils.TimesToDate(startDate)));
                }
                String endDate = saleDateRangeJson.getString("endDate");
                if(ObjectUtil.isNotEmpty(endDate)){
                    productInfoBo.setSaleEndTime(DateUtils.dateTime(DateUtils.TimesToDate(endDate)));
                }
            }
            String reserveDesc = buyRuleJson.getString("reserveDesc");
            productInfoBo.setReserveDesc(reserveDesc);
            Long limitCount = buyRuleJson.getLong("limitCount");
            productInfoBo.setBuyLimit(limitCount);
        }
        Long stock = data.getLong("totalStock");
        Long totalSales = data.getLong("salesVolume");
        String itemContentImage = data.getString("imageShow");
        String itemContentGroup = data.getString("packages");
        String itemBuyNote = data.getString("buyRule");
        String useNote = data.getString("useRule");
        String ticketTimeRule = data.getString("tag");
        //价格处理 需要*1.05的系数
        JSONObject priceJson = data.getJSONObject("price");
        BigDecimal vipPrice = data.getBigDecimal("distributorSettleAmount"); //结算价
        BigDecimal originPrice = new BigDecimal("0.00"); // 原价
        BigDecimal salePrice = new BigDecimal("0.00"); //原售价
        BigDecimal newSalePrice = null; //新的售价
        if(ObjectUtil.isNotEmpty(priceJson)){
            JSONObject originPriceJson = priceJson.getJSONObject("originPrice");
            if(ObjectUtil.isNotEmpty(originPriceJson)){
                originPrice = originPriceJson.getBigDecimal("price");
            }
            JSONObject salePriceJson = priceJson.getJSONObject("salePrice");
            if(ObjectUtil.isNotEmpty(salePriceJson)){
                salePrice = salePriceJson.getBigDecimal("price");
            }
        }
        //1、如果售价小于10元直接略过
        if(salePrice.compareTo(new BigDecimal("10.00")) < 0){
            throw new ServiceException("售价小于10元");
        }
        //2、售价乘1.05取整
        newSalePrice = salePrice.multiply(new BigDecimal("1.05")).setScale(0,BigDecimal.ROUND_UP);
        //3、如果加价后大于原价直接略过
        if(newSalePrice.compareTo(originPrice) > -1){
            throw new ServiceException("加价后大于原价");
        }
        //4.新售价减结算价低于0.5也直接跳过
        if (newSalePrice.subtract(vipPrice).compareTo(new BigDecimal("0.5")) == -1){
            throw new ServiceException("新售价结算价低于0.5");
        }
        BigDecimal divide = newSalePrice.divide(originPrice, 2, RoundingMode.HALF_UP);

        productInfoBo.setDiscount(divide.toString());
        productInfoBo.setItemId(itemId);
        productInfoBo.setTitle(title);

        productInfoBo.setActivityPriceCent(newSalePrice);
        productInfoBo.setOriginalPriceCent(originPrice);
        productInfoBo.setItemBuyNote(itemBuyNote);
        productInfoBo.setItemContentImage(itemContentImage);
        productInfoBo.setItemContentGroup(itemContentGroup);
        productInfoBo.setTicketTimeRule(ticketTimeRule);
        productInfoBo.setUseNote(useNote);
        productInfoBo.setStock(stock);
        productInfoBo.setTotalSales(totalSales);


    }

    /**
     * 门店赋值
     */
    private Long setShop(JSONObject data, Long platformKey) {
        String shopName = data.getString("name");
        String address = data.getString("address");
        String poiId = data.getString("poiId");
        //获取经纬度字段
        JSONObject gaodeCoord = data.getJSONObject("gaodeCoord");
        BigDecimal lon = gaodeCoord.getBigDecimal("lon");
        BigDecimal lat = gaodeCoord.getBigDecimal("lat");

        if (ObjectUtil.isNotEmpty(lat) && ObjectUtil.isNotEmpty(lon)) {
            ShopBo shopBo = new ShopBo();
            shopBo.setSupplierShopId(poiId);
            shopBo.setShopName(shopName);
            shopBo.setLongitude(lon);
            shopBo.setLatitude(lat);
            shopBo.setAddress(address);
            //供应商商户默认为已审核商户
            shopBo.setExamineVerifier("1");
            ShopVo shopVo = shopService.queryByNameAndSupplierId(shopName,poiId);
            if (ObjectUtil.isEmpty(shopVo)) {
                shopService.insertByBo(shopBo);
            } else {
                shopBo.setShopId(shopVo.getShopId());
                shopService.updateByBo(shopBo);

            }
            return shopBo.getShopId();
        }
        return null;
    }




    /**
     * 初始化Access token
     */
    public synchronized String getAccessToken() {
        String url = CtripConfig.getGetTokenUrl();
        String param = "AID=" + CtripConfig.getAid() + "&SID=" + CtripConfig.getSid() + "&KEY=" + CtripConfig.getKey();
        String result = HttpUtil.createGet(url).body(param).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject);
        Integer expiresIn = jsonObject.getInteger("Expires_In");
        if (null == expiresIn || expiresIn <= 0) { // 没时间，默认五分钟
            expiresIn = 5 * 60; // 单位秒
        } else { // 有时间，去七成
            expiresIn = new Double(expiresIn * 0.7).intValue();
        }
        RedisUtils.setCacheObject(ACCESSTOKEN, jsonObject.getString("Access_Token"), Duration.ofSeconds(expiresIn));
        RedisUtils.setCacheObject(REFRESHTOKEN, jsonObject.getString("Refresh_Token"), Duration.ofSeconds(12 * 60));
        return jsonObject.getString("Access_Token");
    }
}

