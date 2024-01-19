package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteMerchantService;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.properties.YsfMerchantProperties;
import com.ruoyi.zlyyh.utils.sdk.UnionPayMerchantUtil;
import com.ruoyi.zlyyhadmin.service.ICommercialTenantService;
import com.ruoyi.zlyyhadmin.service.IShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取银联品牌商户门店
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteMerchantServiceImpl implements RemoteMerchantService {
    private static final YsfMerchantProperties YSF_MERCHANT_PROPERTIES = SpringUtils.getBean(YsfMerchantProperties.class);

    @Autowired
    private ICommercialTenantService commercialTenantService;
    @Autowired
    private IShopService shopService;

    @Async
    @Override
    public void getBrandMerchantList(Long platformKey) {
        // 唯一标识（指定key + url + 消息头）
        String cacheRepeatKey = Constants.REPEAT_SUBMIT_KEY + platformKey;
        String key = RedisUtils.getCacheObject(cacheRepeatKey);
        if (key == null) {
            RedisUtils.setCacheObject(cacheRepeatKey, "", Duration.ofMillis(3000));
        } else {
            return;
        }

        Integer pageIndex = 1;
        Integer pageSize = 20;
        Integer errorCount = 0;
        String appId = YSF_MERCHANT_PROPERTIES.getAppId();
        String chnnlId = YSF_MERCHANT_PROPERTIES.getChnnlId();
        String signId = YSF_MERCHANT_PROPERTIES.getSignId();
        String token = YSF_MERCHANT_PROPERTIES.getToken();
        String openInsId = YSF_MERCHANT_PROPERTIES.getOpenInsId();
        String brandMethod = YSF_MERCHANT_PROPERTIES.getBrandMethod();
        String certPath = YSF_MERCHANT_PROPERTIES.getCertPath();
        String certPwd = YSF_MERCHANT_PROPERTIES.getCertPwd();
        String url = YSF_MERCHANT_PROPERTIES.getUrl();

        while (true) {
            if (errorCount > 3) {
                break;
            }
            //先查询品牌列表
            String brandList = UnionPayMerchantUtil.getBrandList(pageSize, pageIndex, appId, chnnlId, signId, token, brandMethod, openInsId, certPath, certPwd, url);
            if (ObjectUtil.isEmpty(brandList)) {
                errorCount += 1;
                continue;
            }
            JSONObject brandResult = JSONObject.parseObject(brandList);
            if (!"0000000000".equals(brandResult.getString("code"))) {
                // 请求失败
                log.error("获取银联商户失败，接口返回信息：{}", brandList);
                errorCount += 1;
                continue;
            }
            JSONObject data = brandResult.getJSONObject("data");
            if (ObjectUtil.isEmpty(data)) {
                break;
            }
            JSONObject data1 = data.getJSONObject("data");
            if (ObjectUtil.isEmpty(data1)) {
                break;
            }
            Integer total = data1.getInteger("count");
            JSONArray list = data1.getJSONArray("list");
            if (ObjectUtil.isEmpty(list)) {
                break;
            }
            for (int i = 0; i < list.size(); i++) {
                JSONObject brand = list.getJSONObject(i);
                String ylBrandId = brand.getString("brandId");
                if (null == ylBrandId) {
                    continue;
                }
                CommercialTenantVo commercialTenantVo = commercialTenantService.queryByYlBrandId(ylBrandId);
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
                    commercialTenantBo.setSource("1");
                    commercialTenantService.insertByBo(commercialTenantBo);
                }
                getShopSave(ylBrandId, platformKey);

            }
            if (pageIndex * pageSize >= total) {
                break;
            }
            pageIndex++;
        }
    }

    private void getShopSave(String brandId, Long platformKey) {
        String appId = YSF_MERCHANT_PROPERTIES.getAppId();
        String chnnlId = YSF_MERCHANT_PROPERTIES.getChnnlId();
        String signId = YSF_MERCHANT_PROPERTIES.getSignId();
        String token = YSF_MERCHANT_PROPERTIES.getToken();
        String openInsId = YSF_MERCHANT_PROPERTIES.getOpenInsId();
        String certPath = YSF_MERCHANT_PROPERTIES.getCertPath();
        String certPwd = YSF_MERCHANT_PROPERTIES.getCertPwd();
        String url = YSF_MERCHANT_PROPERTIES.getUrl();
        String storeMethod = YSF_MERCHANT_PROPERTIES.getStoreMethod();
        //添加商户后 先查询门店信息列表
        CommercialTenantVo commercialTenant = commercialTenantService.queryByYlBrandId(brandId);
        //添加门店之前先将门店与门店商品关联表删除 先查询门店id列表
        List<ShopVo> shopVos = shopService.queryByCommercialTenantId(commercialTenant.getCommercialTenantId());
        if (ObjectUtil.isNotEmpty(shopVos)) {
            List<Long> wechatIds = shopVos.stream().map(ShopVo::getShopId).collect(Collectors.toList());
            shopService.deleteWithValidByIds(wechatIds, true);
        }
        int pageIndexShop = 1;
        int pageSize = 20;
        int errorCount = 0;
        while (true) {
            if (errorCount > 20) {
                break;
            }
            String shopList = UnionPayMerchantUtil.getShopList(pageSize, pageIndexShop, brandId, appId, chnnlId, signId, token, storeMethod, openInsId, certPath, certPwd, url);
            if (ObjectUtil.isEmpty(shopList)) {
                break;
            }
            JSONObject jsonObjectShop = JSONObject.parseObject(shopList);
            String code = jsonObjectShop.getString("code");
            if (!"0000000000".equals(code)) {
                // 请求失败
                log.error("获取银联商户门店失败，接口返回信息：{}", shopList);
                errorCount += 1;
                continue;
            }
            JSONObject data = jsonObjectShop.getJSONObject("data");
            if (ObjectUtil.isEmpty(data)) {
                break;
            }
            JSONObject data1 = data.getJSONObject("data");
            if (ObjectUtil.isEmpty(data1)) {
                break;
            }
            Integer totalShop = data1.getInteger("count");
            JSONArray list = data1.getJSONArray("list");
            if (ObjectUtil.isEmpty(list)) {
                break;
            }
            for (int i1 = 0; i1 < list.size(); i1++) {
                JSONObject shop = list.getJSONObject(i1);
                if (ObjectUtil.isEmpty(shop)) {
                    continue;
                }
                setShop(shop, platformKey, commercialTenant.getCommercialTenantId());

            }
            if (pageIndexShop * pageSize >= totalShop) {
                break;
            }
            pageIndexShop++;
        }
    }

    /**
     * 商户赋值
     *
     * @param data
     * @param commercialTenantBo
     */
    private void setCommercialTenant(JSONObject data, CommercialTenantBo commercialTenantBo) {
        String brandId = data.getString("brandId");
        String brandName = data.getString("brandNm");
        commercialTenantBo.setYlBrandId(brandId);
        commercialTenantBo.setCommercialTenantName(brandName);
    }

    /**
     * 门店赋值
     */
    private Long setShop(JSONObject data, Long platformKey, Long commercialTenantId) {
        String shopName = data.getString("storeNm");
        String shopTel = data.getString("storeContsTel");
        String address = data.getString("storeAddr");
        String storeProvDivisNm = data.getString("storeProvDivisNm");
        String storeAreaDivisNm = data.getString("storeAreaDivisNm");
        String storeCityDivisNm = data.getString("storeCityDivisNm");
        //组合一下
        address = storeProvDivisNm + storeCityDivisNm + storeAreaDivisNm + address;
        String storeProvDivisCd = data.getString("storeProvDivisCd");
        String procode = storeProvDivisCd.substring(0, 2) + "0000";
        String citycode = storeProvDivisCd.substring(0, 4) + "00";
        BigDecimal longitude = data.getBigDecimal("storeLon");
        BigDecimal latitude = data.getBigDecimal("storeLat");

        if (ObjectUtil.isNotEmpty(latitude) && ObjectUtil.isNotEmpty(longitude)) {
            ShopBo shopBo = new ShopBo();
            shopBo.setPlatformKey(platformKey);
            shopBo.setAdcode(storeProvDivisCd);
            shopBo.setShopName(shopName);
            shopBo.setShopTel(shopTel);
            shopBo.setCity(storeCityDivisNm);
            shopBo.setLongitude(longitude);
            shopBo.setLatitude(latitude);
            shopBo.setAddress(address);
            shopBo.setProcode(procode);
            shopBo.setCitycode(citycode);

            shopBo.setCommercialTenantId(commercialTenantId);
            ShopVo shopVo = shopService.queryByNameAndCommercialTenantId(shopName, commercialTenantId);
            if (ObjectUtil.isEmpty(shopVo)) {
                shopService.insertByBo(shopBo);
            } else {
                shopBo.setShopId(shopVo.getShopId());
            }
            return shopBo.getShopId();
        }
        return null;
    }

}
