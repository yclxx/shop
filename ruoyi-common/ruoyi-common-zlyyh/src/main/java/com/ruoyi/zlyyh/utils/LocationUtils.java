package com.ruoyi.zlyyh.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LocationUtils {

    private static final String MAP_LOCATION_URL = "https://restapi.amap.com/v3/geocode/regeo";
    private static final String GAO_DE_MAP_KEY = "ed8652067f054abb156de40a110a08ea";

    public static Map<String, String> getLocationCity(String location) {
        // 采用Redis缓存，相同ip无需多次查询
        if (ObjectUtil.isEmpty(location)){
            throw new ServiceException("未获取到您的位置信息，请打开位置授权");
        }
        String key = "locationCityKeys:" + location;
        // 查询缓存是否存在
        Map<String, String> result = null;
        try {
            result = RedisUtils.getCacheObject(key);
        } catch (Exception e) {
            log.error("获取城市行政编码redis读取异常", e);
        }
        if (ObjectUtil.isNotEmpty(result)) {
            return result;
        }
        result = new HashMap<>(4);
        //通过经纬度查询详细地址  经度在前
        String query = "key=" + GAO_DE_MAP_KEY + "&location=" + location;
        String s = HttpUtil.createGet(MAP_LOCATION_URL).body(query).execute().body();
        Dict dict = null;
        try {
            dict = JsonUtils.parseMap(s);
        } catch (Exception e) {
            log.error("获取经纬度异常,返回参数" + s, e);
        }
        if (null != dict) {
            // 成功
            if ("1".equals(dict.getStr("status"))) {
                Map<String, Object> regeocode = dict.get("regeocode", null);
                Dict reg = new Dict(regeocode);
                Map<String, Object> ac = reg.get("addressComponent", null);
                Dict addressComponent = new Dict(ac);
                String adcode = addressComponent.getStr("adcode");
                if (!"[]".equals(adcode) && !"100000".equals(adcode)) {

                    result.put("districtAdcode", adcode);
                    result.put("city", addressComponent.getStr("city"));
                    result.put("district", addressComponent.getStr("district"));
                    result.put("province", addressComponent.getStr("province"));
                    // 缓存
                    try {
                        RedisUtils.setCacheObject(key, result, Duration.ofMinutes(RandomUtil.randomInt(600, 1800)));
                    } catch (Exception e) {
                        log.error("获取城市行政编码redis缓存异常", e);
                    }
                    return result;
                }
            }
        } else {
            throw new ServiceException("获取经纬度失败");

        }
        return null;
    }


}
