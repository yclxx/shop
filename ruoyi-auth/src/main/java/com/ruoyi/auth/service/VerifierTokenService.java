package com.ruoyi.auth.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.NumberUtil;
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
import com.ruoyi.system.api.RemoteVerifierUserService;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.api.model.WxEntity;
import com.ruoyi.system.api.model.XcxLoginUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class VerifierTokenService {
    @DubboReference(retries = 0)
    private RemoteVerifierUserService remoteVerifierUserService;

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
        WxEntity wxEntity = (WxEntity) remoteVerifierUserService.getEntity(loginBody.getXcxCode(), false, platformKey, channel);
        // 缓存基本信息
        CacheUtils.put(CacheNames.M_WX_ENTITY, wxEntity.getOpenid(), wxEntity);
        return loginByOpenId(wxEntity.getOpenid(), platformKey, loginBody.getCityName(), loginBody.getCityCode(), channel);
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
        XcxLoginUser userInfo = remoteVerifierUserService.getUserInfoByOpenId(openId, platformKey, platformType);
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            return "isNullUser:" + openId;
        }
        userInfo.setOpenid(openId);
        userInfo.setCityName(cityName);
        userInfo.setCityCode(cityCode);
        // 生成token
        return getToken(userInfo);
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
        String mobile = remoteVerifierUserService.getWxMobile(wxLoginBody.getXcxCode(), platformKey);
        WxEntity wxEntity = CacheUtils.get(CacheNames.M_WX_ENTITY, wxLoginBody.getOpenId());
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
        XcxLoginUser userInfo = remoteVerifierUserService.getUserInfoByMobile(wxEntity.getOpenid(), mobile, platformKey, channel);
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            // 新增用户
            userInfo = remoteVerifierUserService.register(wxEntity, wxLoginBody.getCityName(), wxLoginBody.getCityCode(), channel);
            if (null == userInfo) {
                throw new ServiceException("你非商户管理员");
                //userInfo = remoteVerifierUserService.getUserInfoByMobile(wxEntity.getOpenid(), mobile, platformKey, channel);
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
     * 获取token
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
            recordLogininfor(userInfo.getUsername(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
            recordLoginDate(userInfo.getUserId(), userInfo.getUserChannelId());
            token = StpUtil.getTokenValue();
            if (StringUtils.isNotBlank(token)) {
                CacheUtils.put(CacheNames.loginUserTokens, userInfo.getUserId(), token);
            }
        }
        return token;
    }

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

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(String username, String status, String message) {
        LogininforEvent logininfor = new LogininforEvent();
        logininfor.setUserName(username);
        logininfor.setIpaddr(ServletUtils.getClientIP());
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
     */
    public void recordLoginDate(Long userId, Long userChannelId) {
        remoteVerifierUserService.recordLoginDate(userId, userChannelId, ServletUtils.getClientIP());
    }

    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            // 清除缓存的token
            CacheUtils.evict(CacheNames.loginUserTokens, loginUser.getUserId());
            remoteVerifierUserService.logout(loginUser.getUserId());
            StpUtil.logout();
            recordLogininfor(loginUser.getUsername(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
        } catch (NotLoginException ignored) {
        }
    }
}
