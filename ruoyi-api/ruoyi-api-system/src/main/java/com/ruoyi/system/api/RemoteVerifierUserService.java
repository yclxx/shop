package com.ruoyi.system.api;

import com.ruoyi.common.core.exception.user.UserException;
import com.ruoyi.system.api.model.LoginEntity;
import com.ruoyi.system.api.model.XcxLoginUser;

/**
 * 用户服务
 */
public interface RemoteVerifierUserService {

    /**
     * 获取微信用户手机号
     */
    String getWxMobile(String code, Long platformKey);

    /**
     * 获取微信登录信息
     */
    //WxEntity wxLoginEntity(Long platformKey, String channel, String code);
    /**
     * 获取用户授权信息
     *
     * @param code        授权code
     * @param platformKey 平台key
     * @param getMobile   true 获取手机号 ，false 不获取手机号
     * @return openId，令牌等相关信息
     */
    LoginEntity getEntity(String code, boolean getMobile, Long platformKey, String platformChannel);

    /**
     * 通过手机号查询用户信息
     *
     * @param openId      用户openId，如不一致则修改为最新的openId
     * @param mobile      手机号
     * @param platformKey 平台key
     * @return 结果
     * @throws UserException 用户异常
     */
    XcxLoginUser getUserInfoByMobile(String openId, String mobile, Long platformKey, String platformType) throws UserException;

    /**
     * 通过openid查询用户信息
     *
     * @param openId      用户openId
     * @param platformKey 平台key
     * @return 结果
     * @throws UserException 用户异常
     */
    XcxLoginUser getUserInfoByOpenId(String openId, Long platformKey, String platformType) throws UserException;

    /**
     * 注册用户
     *
     * @param ysfEntity 用户相关信息
     * @param cityName  城市
     * @param cityCode  城市行政区号
     * @return 结果
     */
    XcxLoginUser register(LoginEntity ysfEntity, String cityName, String cityCode, String platformType);

    /**
     * 记录用户登录时间
     *
     * @param userId   用户ID
     */
    void recordLoginDate(Long userId, Long userChannelId, String ip);
    /**
     * 小程序端退出登录
     */
     void logout(Long userId);
}
