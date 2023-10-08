package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteFoodService;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.properties.YsfFoodProperties;
import com.ruoyi.zlyyh.utils.YsfFoodUtils;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteFoodServicelmpl implements RemoteFoodService {
    private static final YsfFoodProperties YSF_FOOD_PROPERTIES = SpringUtils.getBean(YsfFoodProperties.class);

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

    @Async
    @Override
    public void getFoodList(Long platformKey) {
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
        String appId = YSF_FOOD_PROPERTIES.getAppId();
        String rsaPrivateKey = YSF_FOOD_PROPERTIES.getRsaPrivateKey();
        String brandListUrl = YSF_FOOD_PROPERTIES.getBrandListUrl();

        while (true) {
            //先查询品牌列表
            String brandList = YsfFoodUtils.getBrandList(pageSize, pageIndex, appId, rsaPrivateKey, brandListUrl);
            if (ObjectUtil.isEmpty(brandList)) {
                break;
            }
            JSONObject jsonObject = JSONObject.parseObject(brandList);
            Integer total = jsonObject.getInteger("total");
            JSONArray data = jsonObject.getJSONArray("data");
            if (ObjectUtil.isEmpty(data)) {
                break;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject brand = data.getJSONObject(i);
                Long brandId = brand.getLong("brandId");
                if (null == brandId) {
                    continue;
                }
                CommercialTenantVo commercialTenantVo = commercialTenantService.queryByBrandId(brandId);
                CommercialTenantBo commercialTenantBo = new CommercialTenantBo();
                if (ObjectUtil.isNotEmpty(commercialTenantVo)) {
                    //商户存在 - 修改
                    setCommercialTenant(brand, commercialTenantBo);
                    commercialTenantBo.setPlatformKey(platformKey);
                    commercialTenantBo.setCommercialTenantId(commercialTenantVo.getCommercialTenantId());
                    commercialTenantService.updateByBo(commercialTenantBo);
                } else {
                    //商户不存在 - 新增
                    commercialTenantBo.setPlatformKey(platformKey);
                    setCommercialTenant(brand, commercialTenantBo);
                    commercialTenantService.insertByBo(commercialTenantBo);
                }
                getShopSave(brandId, platformKey);
            }
            if (pageIndex * pageSize >= total) {
                break;
            }
            pageIndex++;

        }

    }

    private void getShopSave(Long brandId, Long platformKey) {
        String appId = YSF_FOOD_PROPERTIES.getAppId();
        String rsaPrivateKey = YSF_FOOD_PROPERTIES.getRsaPrivateKey();
        String productListUrl = YSF_FOOD_PROPERTIES.getProductListUrl();
        String productInfoUrl = YSF_FOOD_PROPERTIES.getProductInfoUrl();
        String shopInfoUrl = YSF_FOOD_PROPERTIES.getShopInfoUrl();
        //添加商户后 先查询门店信息列表
        CommercialTenantVo commercialTenant = commercialTenantService.queryByBrandId(brandId);
        //添加门店之前先将门店与门店商品关联表删除 先查询门店id列表
        List<ShopVo> shopVos = shopService.queryByCommercialTenantId(commercialTenant.getCommercialTenantId());
        if (ObjectUtil.isNotEmpty(shopVos)) {
            List<Long> wechatIds = shopVos.stream().map(ShopVo::getShopId).collect(Collectors.toList());
            shopService.deleteWithValidByIds(wechatIds, true);
        }
        int pageIndexProduct = 1;
        int pageSize = 20;
        while (true) {
            String shopAndCommodityList = YsfFoodUtils.getShopAndCommodityByBrandId(pageSize, pageIndexProduct, appId, brandId, rsaPrivateKey, productListUrl);
            if (ObjectUtil.isEmpty(shopAndCommodityList)) {
                break;
            }
            JSONObject jsonObjectProduct = JSONObject.parseObject(shopAndCommodityList);
            Integer totalProduct = jsonObjectProduct.getInteger("total");
            JSONObject data1 = jsonObjectProduct.getJSONObject("data");
            if (ObjectUtil.isEmpty(data1)) {
                break;
            }
            JSONArray shopList = data1.getJSONArray("shopList");
            if (ObjectUtil.isEmpty(shopList)) {
                break;
            }
            for (int i1 = 0; i1 < shopList.size(); i1++) {
                JSONObject shop = shopList.getJSONObject(i1);
                Long brandShopId = shop.getLong("brandShopId");
                //请求门店详情接口获取门店详情信息
                String shopInfo = YsfFoodUtils.getShopInfo(appId, brandShopId, rsaPrivateKey, shopInfoUrl);
                if (ObjectUtil.isEmpty(shopInfo)) {
                    continue;
                }
                JSONObject shopInfoJson = JSONObject.parseObject(shopInfo);
                if (ObjectUtil.isEmpty(shopInfoJson)) {
                    continue;
                }
                Long shopId = setShop(shopInfoJson, platformKey, commercialTenant.getCommercialTenantId());
                if (null == shopId) {
                    continue;
                }
                //新增完门店后 查询门店的商品 并将商品门店 商品品牌 相互关联
                JSONArray commodityVoList = shop.getJSONArray("commodityVoList");
                if (ObjectUtil.isEmpty(commodityVoList)) {
                    continue;
                }
                for (int i2 = 0; i2 < commodityVoList.size(); i2++) {
                    JSONObject commodity = commodityVoList.getJSONObject(i2);
                    //请求商品详情接口
                    String commodityId = commodity.getString("commodityId");

                    //在这里先判断这个商品有没有存在map里面
                    Long productId = productHashMap.get(commodityId);
                    if (productId == null) {
                        //如果这个id没有存在过请求接口
                        String productInfo = YsfFoodUtils.getProductInfo(appId, commodityId, rsaPrivateKey, productInfoUrl);
                        if (ObjectUtil.isEmpty(productInfo)) {
                            continue;
                        }
                        JSONObject productJson = JSONObject.parseObject(productInfo);
                        //查询商品是否存在
                        ProductVo productVo = productService.queryByExternalProductId(commodityId);
                        ProductBo productBo = new ProductBo();
                        ProductInfoBo productInfoBo = new ProductInfoBo();
                        setProduct(productJson, productBo);
                        setProductInfo(productJson, productInfoBo);
                        if (ObjectUtil.isNotEmpty(productVo)) {
                            //商品不为空 修改商品
                            productBo.setPlatformKey(platformKey);
                            //商品修改为可以在用户侧退款
                            productBo.setCusRefund("1");
                            productBo.setProductId(productVo.getProductId());
                            productBo.setExternalProductId(commodityId);
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
                            productBo.setPlatformKey(platformKey);
                            //商品修改为可以在用户侧退款
                            productBo.setCusRefund("1");
                            productBo.setExternalProductId(commodityId);
                            productService.insertByBo(productBo);
                            productInfoBo.setProductId(productBo.getProductId());
                            productInfoService.insertByBo(productInfoBo);
                        }
                        productId = productBo.getProductId();
                        productHashMap.put(commodityId, productBo.getProductId());
                        //关联商品商户
                        setCommercialTenantProduct(commercialTenant.getCommercialTenantId(), productBo.getProductId());
                        //关联门店商户
                        setProductShop(productBo.getProductId(), shopId);
                    } else {
                        //关联商品商户
                        setCommercialTenantProduct(commercialTenant.getCommercialTenantId(), productId);
                        //关联门店商户
                        setProductShop(productId, shopId);
                    }
                    productService.setProductCity(productId);
                }
            }
            if (pageIndexProduct * pageSize >= totalProduct) {
                break;
            }
            pageIndexProduct++;
        }
    }

    /**
     * 商户赋值
     *
     * @param data
     * @param commercialTenantBo
     */
    private void setCommercialTenant(JSONObject data, CommercialTenantBo commercialTenantBo) {
        Long brandId = data.getLong("brandId");
        String brandName = data.getString("brandName");
        String brandLogo = data.getString("brandLogo");
        commercialTenantBo.setBrandId(brandId);
        commercialTenantBo.setCommercialTenantName(brandName);
        commercialTenantBo.setCommercialTenantImg(brandLogo);
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
        String commodityName = data.getString("commodityName");
        String commodityImage = data.getString("commodityImage");
        String commoditySubhead = data.getString("commoditySubhead");
        BigDecimal officialPrice = data.getBigDecimal("officialPrice");
        BigDecimal sellingPrice = data.getBigDecimal("sellingPrice");
        Long repertory = data.getLong("repertory");
        String sjDate = data.getString("sjDate");
        String xjDate = data.getString("xjDate");
        productBo.setProductName(commodityName);
        productBo.setProductImg(commodityImage);
        //productBo.setProductAbbreviation(commoditySubhead);
        productBo.setProductSubhead(commoditySubhead);
        productBo.setOriginalAmount(officialPrice);
        productBo.setSellAmount(sellingPrice);
        productBo.setTotalCount(repertory);
        //接口进来类型为美食套餐
        productBo.setProductType("5");
        productBo.setPickupMethod("1");
        productBo.setShowOriginalAmount("1");
        productBo.setProductAffiliation("0");
        productBo.setShowIndex("1");
        productBo.setShowStartDate(DateUtils.parseDate(sjDate));
        productBo.setShowEndDate(DateUtils.parseDate(xjDate));
        productBo.setSellStartDate(DateUtils.parseDate(sjDate));
        productBo.setSellEndDate(DateUtils.parseDate(xjDate));

    }

    /**
     * 商品详情赋值
     */
    private void setProductInfo(JSONObject data, ProductInfoBo productInfoBo) {
        JSONObject commodityKbProduct = data.getJSONObject("commodityKbProduct");
        String title = commodityKbProduct.getString("title");
        String itemId = commodityKbProduct.getString("itemId");
        String mainPicture = commodityKbProduct.getString("mainPicture");
        String saleStartTime = commodityKbProduct.getString("saleStartTime");
        String saleEndTime = commodityKbProduct.getString("saleEndTime");
        String discount = commodityKbProduct.getString("discount");
        Long stock = commodityKbProduct.getLong("stock");
        Long totalSales = commodityKbProduct.getLong("totalSales");
        Long applyShopCount = commodityKbProduct.getLong("applyShopCount");
        Long useTimes = commodityKbProduct.getLong("useTimes");
        String commissionRate = commodityKbProduct.getString("commissionRate");
        BigDecimal activityPriceCent = commodityKbProduct.getBigDecimal("activityPriceCent");
        BigDecimal originalPriceCent = commodityKbProduct.getBigDecimal("originalPriceCent");
        String itemContentGroup = commodityKbProduct.getString("itemContentGroup");
        String itemBuyNote = commodityKbProduct.getString("itemBuyNote");
        String itemContentImage = commodityKbProduct.getString("itemContentImage");
        String shopInfo = commodityKbProduct.getString("shopInfo");
        String useNote = commodityKbProduct.getString("useNote");
        String ticketTimeRule = commodityKbProduct.getString("ticketTimeRule");
        Long buyLimit = commodityKbProduct.getLong("buyLimit");
        String period = commodityKbProduct.getString("period");
        String reserveDesc = commodityKbProduct.getString("reserveDesc");
        String brandName = commodityKbProduct.getString("brandName");
        productInfoBo.setItemId(itemId);
        productInfoBo.setTitle(title);
        productInfoBo.setMainPicture(mainPicture);
        productInfoBo.setActivityPriceCent(activityPriceCent);
        productInfoBo.setOriginalPriceCent(originalPriceCent);
        productInfoBo.setItemBuyNote(itemBuyNote);
        productInfoBo.setItemContentImage(itemContentImage);
        productInfoBo.setItemContentGroup(itemContentGroup);
        productInfoBo.setBrandName(brandName);
        productInfoBo.setPeriod(period);
        productInfoBo.setBuyLimit(buyLimit);
        productInfoBo.setTicketTimeRule(ticketTimeRule);
        productInfoBo.setUseNote(useNote);
        productInfoBo.setShopInfo(shopInfo);
        productInfoBo.setReserveDesc(reserveDesc);
        productInfoBo.setSaleStartTime(saleStartTime);
        productInfoBo.setSaleEndTime(saleEndTime);
        productInfoBo.setDiscount(discount);
        productInfoBo.setUseTimes(useTimes);
        productInfoBo.setCommissionRate(commissionRate);
        productInfoBo.setStock(stock);
        productInfoBo.setTotalSales(totalSales);
        productInfoBo.setApplyShopCount(applyShopCount);

    }

    /**
     * 门店赋值
     */
    private Long setShop(JSONObject data, Long platformKey, Long commercialTenantId) {
        String shopName = data.getString("shopName");
        String shopTel = data.getString("shopTel");
        String businessHours = data.getString("businessHours");
        String address = data.getString("address");
        String formattedAddress = data.getString("formattedAddress");
        String province = data.getString("province");
        String city = data.getString("city");
        //String citycode = data.getString("citycode");
        String district = data.getString("district");
        String adcode = data.getString("adcode");
        String procode = adcode.substring(0, 2) + "0000";
        String citycode = adcode.substring(0, 4) + "00";
        BigDecimal longitude = data.getBigDecimal("longitude");
        BigDecimal latitude = data.getBigDecimal("latitude");
        String state = data.getString("state");
        if ("0".equals(state) && ObjectUtil.isNotEmpty(latitude) && ObjectUtil.isNotEmpty(longitude)) {
            ShopBo shopBo = new ShopBo();
            shopBo.setPlatformKey(platformKey);
            shopBo.setAdcode(adcode);
            shopBo.setShopName(shopName);
            shopBo.setShopTel(shopTel);
            shopBo.setBusinessHours(businessHours);
            shopBo.setCity(city);
            shopBo.setLongitude(longitude);
            shopBo.setLatitude(latitude);
            shopBo.setAddress(address);
            shopBo.setFormattedAddress(formattedAddress);
            shopBo.setProcode(procode);
            shopBo.setCitycode(citycode);
            shopBo.setDistrict(district);
            shopBo.setProvince(province);
            shopBo.setCommercialTenantId(commercialTenantId);
            ShopVo shopVo = shopService.queryByNameAndCommercialTenantId(shopName, commercialTenantId);
            if (ObjectUtil.isEmpty(shopVo)) {
                shopService.insertShop(shopBo);
            } else {
                shopBo.setShopId(shopVo.getShopId());
            }
            return shopBo.getShopId();
        }
        return null;
    }

}
