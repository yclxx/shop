package com.ruoyi.auth.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import com.ruoyi.auth.form.YsfLoginBody;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.enums.DeviceType;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.MessageUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.log.event.LogininforEvent;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.system.api.RemoteAppUserService;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.api.model.XcxLoginUser;
import com.ruoyi.system.api.model.YsfEntity;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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
        XcxLoginUser userInfo = remoteAppUserService.getUserInfoByMobile(null, mobile, getPlatformKey(request));
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            return "isNullUser";
        }
        userInfo.setCityName("杭州市");
        userInfo.setCityCode("330100");
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
    public String ysfLogin(YsfLoginBody ysfLoginBody, HttpServletRequest request) {
        // xcxCode 为 小程序openId
        YsfEntity ysfEntity = remoteAppUserService.getYsfEntity(ysfLoginBody.getXcxCode(), false, getPlatformKey(request));
        XcxLoginUser userInfo = remoteAppUserService.getUserInfoByOpenid(ysfEntity.getOpenId(), ysfEntity.getPlatformKey());
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            return "isNullUser:" + ysfEntity.getOpenId();
        }
        userInfo.setCityName(ysfLoginBody.getCityName());
        userInfo.setCityCode(ysfLoginBody.getCityCode());
        // 生成token
        return getToken(userInfo);
    }

    /**
     * 云闪付小程序 手机号登录
     *
     * @param ysfLoginBody 授权code
     * @param request      请求信息
     * @return token
     */
    public String ysfLoginByMobile(YsfLoginBody ysfLoginBody, HttpServletRequest request) {
        // xcxCode 为 小程序openId
        YsfEntity ysfEntity = remoteAppUserService.getYsfEntity(ysfLoginBody.getXcxCode(), true, getPlatformKey(request));
        XcxLoginUser userInfo = remoteAppUserService.getUserInfoByMobile(ysfEntity.getOpenId(), ysfEntity.getMobile(), ysfEntity.getPlatformKey());
        if (null == userInfo || "0".equals(userInfo.getReloadUser())) {
            // 新增用户
            userInfo = remoteAppUserService.register(ysfEntity, ysfLoginBody.getCityName(), ysfLoginBody.getCityCode());
            if (null == userInfo) {
                // 新增之后有可能马上查询不到，休眠一下
                ThreadUtil.sleep(50);
                userInfo = remoteAppUserService.getUserInfoByMobile(ysfEntity.getOpenId(), ysfEntity.getMobile(), ysfEntity.getPlatformKey());
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
            recordLoginDate(userInfo.getUserId(), userInfo.getCityName(), userInfo.getCityCode());
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
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            // 设置用户信息为需要授权
            remoteAppUserService.logout(loginUser.getUserId());
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
     * @param userId   用户Id
     * @param cityName 状态
     * @param cityCode 消息内容
     */
    public void recordLoginDate(Long userId, String cityName, String cityCode) {
        remoteAppUserService.recordLoginDate(userId, cityName, cityCode, ServletUtils.getClientIP());
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
}
