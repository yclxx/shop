package com.ruoyi.zlyyh.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.enumd.DateType;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 25487
 */
@Slf4j
public class ZlyyhUtils {

    /**
     * 校验是否在活动城市
     *
     * @param showCity 活动城市
     */
    public static void checkCity(String showCity, PlatformVo platformVo) {
        String cityCode = ZlyyhUtils.getCityCode();
        if (StringUtils.isBlank(cityCode)) {
            throw new ServiceException("未获取到您的位置信息,请确认是否开启定位服务");
        }
        if (null != platformVo && StringUtils.isNotBlank(platformVo.getPlatformCity()) && !"ALL".equalsIgnoreCase(platformVo.getPlatformCity()) && !platformVo.getPlatformCity().contains(cityCode)) {
            throw new ServiceException("您当前所在位置不在活动参与范围!");
        }
        if (StringUtils.isNotBlank(showCity) && !"ALL".equalsIgnoreCase(showCity) && !showCity.contains(cityCode)) {
            throw new ServiceException("您当前所在位置不在活动参与范围!");
        }
    }

    /**
     * 校验是否在活动城市
     *
     * @param showCity 活动城市
     */
    public static void checkCity(String showCity) {
        checkCity(showCity, null);
    }

    /**
     * 获取日期key
     *
     * @param dateType 日期类型
     * @return 日期key
     */
    public static String getDateCacheKey(DateType dateType) {
        if (dateType == DateType.DAY) {
            return "day:" + DateUtil.today();
        } else if (dateType == DateType.WEEK) {
            return "week:" + DateUtils.getThisWeekDate("周一");
        } else if (dateType == DateType.MONTH) {
            return "month:" + DateUtils.getMonth();
        } else if (dateType == DateType.TOTAL) {
            return "total";
        }
        throw new ServiceException("dateType error");
    }

    /**
     * 获取今日已抢完缓存key
     *
     * @return 缓存key
     */
    public static String getProductDayCount() {
        return "productDayCount:" + DateUtil.today() + ":";
    }

    /**
     * 用户真实所在城市 精确到市 例如 330100
     */
    public static String getCityCode() {
        String adCode = getAdCode();
        if (StringUtils.isBlank(adCode)) {
            return adCode;
        }
        return adCode.substring(0, 4) + "00";
    }

    /**
     * 用户真实所在城市 精确到区 例如 330105
     */
    public static String getAdCode() {
        return ServletUtils.getHeader(ZlyyhConstants.AD_CODE);
    }

    /**
     * 平台渠道
     */
    public static PlatformEnumd getPlatformType() {
        String header = ServletUtils.getHeader(Constants.PLATFORM_TYPE);
        if (StringUtils.isBlank(header)) {
            return PlatformEnumd.MP_YSF;
        }
        return PlatformEnumd.getPlatformEnumd(header);
    }

    /**
     * 用户渠道
     */
    public static String getPlatformChannel() {
        return getPlatformType().getChannel();
    }

    /**
     * 用户渠道
     */
    public static String getPlatformChannel(String platformType) {
        return PlatformEnumd.getPlatformEnumd(platformType).getChannel();
    }

    /**
     * 用户真实所在城市 精确到区 例如 330105
     */
    public static String getCityName() {
        String header = ServletUtils.getHeader(ZlyyhConstants.CITY_NAME);
        if (StringUtils.isNotBlank(header)) {
            try {
                header = URLDecoder.decode(header, "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return header;
    }

    /**
     * 查询平台信息 从请求头中获取平台ID
     *
     * @return 平台ID
     */
    public static Long getPlatformId() {
        HttpServletRequest request = ServletUtils.getRequest();
        if (null == request) {
            throw new ServiceException("请求错误，请退出重试！[request is null]");
        }
        String platformKeyHeader = ServletUtils.getHeader(request, Constants.PLATFORM_KEY);
        if (!NumberUtil.isLong(platformKeyHeader)) {
            throw new ServiceException("请求错误，请退出重试！[platform is null]");
        }
        return Long.parseLong(platformKeyHeader);
    }

    public static void main(String[] args) {
//        String appId = "5d6630e7212a456e82f8d6a495faaec7";
        String appId = "d27c0217490d4e35a901abb2e874f383";
        String url = "https://open.95516.com/open/access/1.0/mission.progress.query";
//        String url = "https://open.95516.com/open/access/1.0/membership.message.push";
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("backendToken", "08d9e1f82006003d1YRkYuPW");
//        params.put("backendToken", "071f9e8e2004003a1P0p3ZyL");
        params.put("openId", "Yt476r36uzge3OFTr/yxUNLUnPa4Fjc5u1ZOO9WQbWLCsU7bvF5PX8elM0Dzid+8");
//        params.put("bizTp","9998");
//        params.put("subBizTp","33");
//        params.put("vid","2023051602");
//        params.put("mobile","13675827473");
//        DESede desede = SecureUtil.desede(HexUtil.decodeHex("2fcb048abc239831bac47fe3b32a2c012fcb048abc239831"));
//        params.put("mobile", desede.encryptBase64(params.get("mobile")));
        params.put("missionId", "JYRW2023101800509");
        //原交易时间
//        params.put("missionGroupId", "1023051601");
        String s = HttpUtil.post(url, JSONObject.toJSONString(params));
        log.info("请求url：{}，请求参数：{}，返回结果：{}", url, params, s);

//        String appId = "fa83ef42f3874697a05f5670c62999c5";
//        String url = "https://open.95516.com/open/access/1.0/mission.progress.query";
//        Map<String, String> params = new HashMap<>();
//        params.put("appId", appId);
//        params.put("backendToken", YsfUtils.getBackendTokenTest(appId, "664a7c84048f4559a0a159b0733dee61", "https://open.95516.com/open/access/1.0/backendToken"));
//        params.put("openId", "AndbYx5raRG7h5LJ4/tyABOu/LDk98N/bnrZQHYsuJz+A91T4lBnR9bMQg6JRT68");
//        params.put("missionId", "2023051605");
//        //原交易时间
////        params.put("missionGroupId", "1023051601");
//        String s = HttpUtil.post(url, JSONObject.toJSONString(params));
//        log.info("请求url：{}，请求参数：{}，返回结果：{}", url, params, s);
    }
}
