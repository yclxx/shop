package com.ruoyi.auth.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.auth.form.AppLoginBody;
import com.ruoyi.auth.form.WxMobileLoginBody;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.enums.DeviceType;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.*;
import com.ruoyi.common.log.event.LogininforEvent;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.system.api.RemoteAppUserService;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.api.model.WxEntity;
import com.ruoyi.system.api.model.XcxLoginUser;
import com.ruoyi.system.api.model.YsfEntity;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Service
public class AppLoginService {

    @DubboReference(retries = 0)
    private RemoteAppUserService remoteAppUserService;

    /**
     * 云闪付小程序 测试登录
     *
     * @param mobile 用户Id
     * @return token
     */
    public String ysfTestLogin(String mobile, HttpServletRequest request) {
        if (!"17767132971".equals(mobile) && !"17817392639".equals(mobile)) {
            return "isNullUser";
        }
        XcxLoginUser userInfo = remoteAppUserService.getUserInfoByMobile(null, mobile, getPlatformKey(request), getPlatformChannel(request));
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            return "isNullUser";
        }
        userInfo.setCityName("杭州市");
        userInfo.setCityCode("330100");
        // 生成token
        return getToken(userInfo);
    }

    /**
     * 微信小程序 静默登录
     *
     * @param loginBody 授权code
     * @param request   请求信息
     * @return token
     */
    public String wxLogin(AppLoginBody loginBody, HttpServletRequest request) {
        Long platformKey = getPlatformKey(request);
        String channel = getPlatformChannel(request);
        // xcxCode 为 小程序openId
        WxEntity wxEntity = (WxEntity) remoteAppUserService.getEntity(loginBody.getXcxCode(), false, platformKey, channel);
        // 缓存基本信息
        CacheUtils.put(CacheNames.WX_ENTITY, wxEntity.getOpenid(), wxEntity);
        return loginByOpenId(wxEntity.getOpenid(), platformKey, loginBody.getCityName(), loginBody.getCityCode(), channel);
    }

    /**
     * 跳转小程序页面
     */
    public String jumpWxGroup(String pages){
        String accessToken = remoteAppUserService.getAccessToken("wxe7c323382a74e41d", "40eb4ef26612ddae48b98081fcd5d55b");
        if(StringUtils.isEmpty(accessToken)){
            return null;
        }
        try{
            String url = "https://api.weixin.qq.com/wxa/generatescheme?access_token=" + accessToken ;
            HttpPost httpPost = new HttpPost(url);
            Map<String,Object> map = new HashMap<>();
            map.put("path",pages);
            Map<String,Object> map1 = new HashMap<>();
            map1.put("jump_wxa",map);
            JSONObject jsonObjects = new JSONObject(map1);
            StringEntity entities = new StringEntity(jsonObjects.toJSONString(), "UTF-8");
            httpPost.setEntity(entities);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = null;
            if (entity != null) {
                //{"errcode":0,"openlink":"weixin://dl/business/?t=BBh8j0jvFLa","errmsg":"ok"}
                String result = EntityUtils.toString(entity, "UTF-8");
                jsonObject = JSON.parseObject(result);
                return jsonObject.getString("openlink");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 微信小程序 手机号登录
     *
     * @param wxLoginBody 授权code
     * @param request     请求信息
     * @return token
     */
    public String wxLoginByMobile(WxMobileLoginBody wxLoginBody, HttpServletRequest request) {
        Long platformKey = getPlatformKey(request);
        String channel = getPlatformChannel(request);
        if (StringUtils.isBlank(wxLoginBody.getOpenId())) {
            if (StringUtils.isBlank(wxLoginBody.getToken())) {
                throw new ServiceException("系统繁忙，请退出应用重试![token]");
            }
            LoginUser loginUser = LoginHelper.getLoginUser(wxLoginBody.getToken());
            if (null == loginUser) {
                throw new ServiceException("登录超时，请退出应用重试!");
            }
            if (loginUser instanceof XcxLoginUser) {
                XcxLoginUser xcxLoginUser = (XcxLoginUser) loginUser;
                wxLoginBody.setOpenId(xcxLoginUser.getOpenid());
            }
        }
        if (StringUtils.isBlank(wxLoginBody.getOpenId())) {
            throw new ServiceException("系统繁忙，请退出应用重试![openId]");
        }
        String mobile = remoteAppUserService.getWxMobile(wxLoginBody.getXcxCode(), platformKey);
        WxEntity wxEntity = CacheUtils.get(CacheNames.WX_ENTITY, wxLoginBody.getOpenId());
        if (null == wxEntity) {
            throw new ServiceException("授权超时，请退出应用重试!");
        }
        if (StringUtils.isBlank(mobile)) {
            if (StringUtils.isBlank(wxEntity.getSession_key())) {
                throw new ServiceException("授权超时，请退出应用重试!");
            }
            Map<String, Object> map = AESUtils.wxDecrypt(wxLoginBody.getEncryptedData(), wxEntity.getSession_key(), wxLoginBody.getIv());
            Object phoneNumber = map.get("phoneNumber");
            if (null == phoneNumber) {
                throw new ServiceException("登录失败，请退出应用重试!");
            }
            mobile = (String) phoneNumber;
        }
        wxEntity.setMobile(mobile);
        wxEntity.setPlatformKey(platformKey);
        // 手机号快速验证接口
        XcxLoginUser userInfo = remoteAppUserService.getUserInfoByMobile(wxEntity.getOpenid(), mobile, platformKey, channel);
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            // 新增用户
            userInfo = remoteAppUserService.register(wxEntity, wxLoginBody.getCityName(), wxLoginBody.getCityCode(), channel);
            if (null == userInfo) {
                userInfo = remoteAppUserService.getUserInfoByMobile(wxEntity.getOpenid(), mobile, platformKey, channel);
            }
        }
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            throw new ServiceException("授权失败，请稍后重试");
        }
        userInfo.setCityName(wxLoginBody.getCityName());
        userInfo.setCityCode(wxLoginBody.getCityCode());
        // 生成token
        return getToken(userInfo);
    }

    /**
     * 云闪付小程序 静默登录
     *
     * @param ysfLoginBody 授权code
     * @param request      请求信息
     * @return token
     */
    public String ysfLogin(AppLoginBody ysfLoginBody, HttpServletRequest request) {
        Long platformKey = getPlatformKey(request);
        String channel = getPlatformChannel(request);
        // xcxCode 为 小程序openId
        YsfEntity ysfEntity = (YsfEntity) remoteAppUserService.getEntity(ysfLoginBody.getXcxCode(), false, platformKey, channel);
        return loginByOpenId(ysfEntity.getOpenId(), platformKey, ysfLoginBody.getCityName(), ysfLoginBody.getCityCode(), channel);
    }

    /**
     * 云闪付小程序 手机号登录
     *
     * @param ysfLoginBody 授权code
     * @param request      请求信息
     * @return token
     */
    public String ysfLoginByMobile(AppLoginBody ysfLoginBody, HttpServletRequest request) {
        Long platformKey = getPlatformKey(request);
        // xcxCode 为 小程序openId
        String channel = getPlatformChannel(request);
        YsfEntity ysfEntity = (YsfEntity) remoteAppUserService.getEntity(ysfLoginBody.getXcxCode(), true, platformKey, channel);
        XcxLoginUser userInfo = remoteAppUserService.getUserInfoByMobile(ysfEntity.getOpenId(), ysfEntity.getMobile(), platformKey, channel);
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            // 新增用户
            userInfo = remoteAppUserService.register(ysfEntity, ysfLoginBody.getCityName(), ysfLoginBody.getCityCode(), channel);
            if (null == userInfo) {
                // 新增之后有可能马上查询不到，休眠一下
                ThreadUtil.sleep(50);
                userInfo = remoteAppUserService.getUserInfoByMobile(ysfEntity.getOpenId(), ysfEntity.getMobile(), platformKey, channel);
            }
        }
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            throw new ServiceException("授权失败，请稍后重试");
        }
        userInfo.setCityName(ysfLoginBody.getCityName());
        userInfo.setCityCode(ysfLoginBody.getCityCode());
        // 生成token
        return getToken(userInfo);
    }

    /**
     * 登录，openId
     *
     * @param openId      openId
     * @param platformKey 平台
     * @param cityName    城市
     * @param cityCode    城市行政区号
     * @return token
     */
    private String loginByOpenId(String openId, Long platformKey, String cityName, String cityCode, String platformType) {
        XcxLoginUser userInfo = remoteAppUserService.getUserInfoByOpenid(openId, platformKey, platformType);
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            return "isNullUser:" + openId;
        }
        userInfo.setCityName(cityName);
        userInfo.setCityCode(cityCode);
        // 生成token
        return getToken(userInfo);
    }

    /**
     * 获取token
     *
     * @param userInfo 登录用户信息
     * @return token
     */
    private String getToken(XcxLoginUser userInfo) {
        String token = null;
        try {
            token = CacheUtils.get(CacheNames.loginUserTokens, userInfo.getUserId());
        } catch (Exception ignored) {
        }
        if (StringUtils.isBlank(token)) {
            // 生成token
            LoginHelper.loginByDevice(userInfo, DeviceType.XCX);
            recordLogininfor(userInfo.getUsername(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"), userInfo.getCityName(), userInfo.getCityCode());
            recordLoginDate(userInfo.getUserId(), userInfo.getUserChannelId(), userInfo.getCityName(), userInfo.getCityCode());
            token = StpUtil.getTokenValue();
            if (StringUtils.isNotBlank(token)) {
                CacheUtils.put(CacheNames.loginUserTokens, userInfo.getUserId(), token);
            }
        }
        return token;
    }

    /**
     * 退出登录
     */
    public void logout(HttpServletRequest request) {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            // 设置用户信息为需要授权
            remoteAppUserService.logout(loginUser.getUserId(), getPlatformChannel(request));
            CacheUtils.evict(CacheNames.loginUserTokens, loginUser.getUserId());
            StpUtil.logout();
            recordLogininfor(loginUser.getUsername(), Constants.LOGOUT, MessageUtils.message("user.logout.success"), null, null);
        } catch (NotLoginException ignored) {
        }
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(String username, String status, String message, String cityName, String cityCode) {
        LogininforEvent logininfor = new LogininforEvent();
        logininfor.setUserName(username);
        logininfor.setIpaddr(ServletUtils.getClientIP());
        if (StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(cityCode)) {
            message = message + " " + cityName + "(" + cityCode + ")";
        }
        logininfor.setMsg(message);
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            logininfor.setStatus(Constants.LOGIN_SUCCESS_STATUS);
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            logininfor.setStatus(Constants.LOGIN_FAIL_STATUS);
        }
        SpringUtils.context().publishEvent(logininfor);
    }

    /**
     * 记录登录时间信息
     *
     * @param userId        用户Id
     * @param userChannelId 渠道用户Id
     * @param cityName      状态
     * @param cityCode      消息内容
     */
    public void recordLoginDate(Long userId, Long userChannelId, String cityName, String cityCode) {
        remoteAppUserService.recordLoginDate(userId, userChannelId, cityName, cityCode, ServletUtils.getClientIP());
    }

    /**
     * 获取平台key
     *
     * @param request 请求信息
     * @return 平台key
     */
    private Long getPlatformKey(HttpServletRequest request) {
        String platformKeyHeader = ServletUtils.getHeader(request, Constants.PLATFORM_KEY);
        if (!NumberUtil.isLong(platformKeyHeader)) {
            throw new ServiceException("请求错误，请退出重试！[platform is null]");
        }
        return Long.parseLong(platformKeyHeader);
    }

    private String getPlatformChannel(HttpServletRequest request) {
        return ServletUtils.getHeader(request, Constants.PLATFORM_TYPE);
    }
}
