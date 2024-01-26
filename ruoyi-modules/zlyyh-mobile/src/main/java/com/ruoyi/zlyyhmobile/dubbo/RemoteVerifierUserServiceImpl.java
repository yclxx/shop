package com.ruoyi.zlyyhmobile.dubbo;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DESede;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.enums.UserStatus;
import com.ruoyi.common.core.enums.UserType;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.user.UserException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteVerifierUserService;
import com.ruoyi.system.api.model.LoginEntity;
import com.ruoyi.system.api.model.WxEntity;
import com.ruoyi.system.api.model.XcxLoginUser;
import com.ruoyi.system.api.model.YsfEntity;
import com.ruoyi.zlyyh.domain.CommercialTenant;
import com.ruoyi.zlyyh.domain.Verifier;
import com.ruoyi.zlyyh.domain.vo.BackendTokenEntity;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.mapper.CommercialTenantMapper;
import com.ruoyi.zlyyh.mapper.VerifierMapper;
import com.ruoyi.zlyyh.properties.WxProperties;
import com.ruoyi.zlyyh.properties.utils.YsfPropertiesUtils;
import com.ruoyi.zlyyh.utils.WxUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteVerifierUserServiceImpl implements RemoteVerifierUserService {
    private final VerifierMapper verifierMapper;
    private final IPlatformService platformService;
    private final WxProperties wxProperties;
    private final CommercialTenantMapper commercialTenantMapper;
    @Autowired
    private LockTemplate lockTemplate;

    /**
     * 获取微信用户手机号
     */
    @Override
    public String getWxMobile(String code, Long platformKey) {
        PlatformVo platformVo = platformService.queryById(platformKey, PlatformEnumd.MP_WX.getChannel());
        if (null == platformVo) {
            throw new ServiceException("平台配置错误");
        }
        String appId = platformVo.getAppId();
        String secret = platformVo.getSecret();
        String accessToken = WxUtils.getAccessToken(appId, secret, wxProperties.getAccessTokenUrl());
        if (StringUtils.isBlank(accessToken)) {
            // 休眠300毫秒再次获取
            ThreadUtil.sleep(300);
            accessToken = WxUtils.getAccessToken(appId, secret, wxProperties.getAccessTokenUrl());
            if (StringUtils.isBlank(accessToken)) {
                log.error("微信基础访问令牌没有获取到");
                throw new ServiceException("系统繁忙，请稍后重试!");
            }
        }
        String url = wxProperties.getUserPhoneNumberUrl() + "?access_token=" + accessToken;
        Map<String, String> params = new HashMap<>(1);
        params.put("code", code);
        String result;
        try {
            result = HttpUtil.post(url, JsonUtils.toJsonString(params));
        } catch (Exception e) {
            log.error("微信获取用户手机号异常，异常信息：", e);
            throw new ServiceException("系统繁忙，请稍后重试！");
        }
        log.info("微信小程序获取用户手机号,请求地址：{},请求参数：{},返回结果：{}", url, params, result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("0".equals(jsonObject.getString("errcode"))) {
            JSONObject phoneInfo = jsonObject.getJSONObject("phone_info");
            return phoneInfo.getString("phoneNumber");
        }
        // 记录失败次数
        if (jsonObject.getInteger("errcode") == 40001) {
            String key = "access_token_fail";
            Integer fail = RedisUtils.getCacheObject(key);
            if (ObjectUtil.isEmpty(fail)) {
                fail = 1;
            } else {
                fail++;
            }
            if (fail >= 10) {
                fail = 0;
                // 删除access_token
                RedisUtils.deleteObject("accessToken:" + appId);
            }
            RedisUtils.setCacheObject(key, fail, Duration.ofMinutes(30));
        }
        return null;
    }

    /**
     * 微信获取openId
     */
    //public WxEntity wxLoginEntity(Long platformKey, String channel, String code) {
    //    PlatformEnumd platformEnumd = PlatformEnumd.getPlatformEnumd(channel);
    //    PlatformVo platformVo = platformService.queryById(platformKey, platformEnumd.getChannel());
    //    String url = wxProperties.getCodeSessionUrl() + "?appid=" + platformVo.getAppId() + "&secret=" + platformVo.getSecret() + "&js_code=" + code + "&grant_type=authorization_code";
    //    String result;
    //    try {
    //        result = HttpRequest.get(url).execute().body();
    //    } catch (Exception e) {
    //        log.error("平台【{}】请求获取微信用户openId异常，异常信息：", platformVo.getPlatformKey(), e);
    //        throw new ServiceException("系统繁忙，请稍后重试！");
    //    }
    //    WxEntity wxEntity = JsonUtils.parseObject(result, WxEntity.class);
    //    if (null == wxEntity || StringUtils.isEmpty(wxEntity.getOpenid())) {
    //        log.info("平台【{}】请求获取微信用户openId失败，返回信息：{}", platformVo.getPlatformKey(), result);
    //        throw new ServiceException("系统繁忙，请稍后重试！");
    //    }
    //    wxEntity.setOpenId(wxEntity.getOpenid());
    //    return wxEntity;
    //}


    /**
     * 获取用户授权信息
     *
     * @return openId，令牌等相关信息
     */
    @Override
    public LoginEntity getEntity(String code, boolean getMobile, Long platformKey, String platformChannel) {
        PlatformEnumd platformType = PlatformEnumd.getPlatformEnumd(platformChannel);
        PlatformVo platformVo = platformService.queryById(platformKey, platformType.getChannel());
        if (null == platformVo) {
            throw new ServiceException("请求错误，请退出重试！[platform is null]");
        }
        if (StringUtils.isBlank(platformVo.getAppId()) || StringUtils.isBlank(platformVo.getSecret())) {
            throw new ServiceException("请求错误，请退出重试！[platform settings error]");
        }
        if (PlatformEnumd.MP_YSF == platformType) {
            return ysfLoginEntity(platformVo, code, getMobile);
        } else if (PlatformEnumd.MP_WX == platformType) {
            return wxLoginEntity(platformVo, code);
        } else {
            throw new ServiceException("平台错误，请联系客服处理!");
        }
    }

    @Override
    public XcxLoginUser getUserInfoByMobile(String openId, String mobile, Long platformKey, String platformType) throws UserException {
        VerifierVo userVo = BeanCopyUtils.copy(verifierMapper.selectOneIncludeMobile(new LambdaQueryWrapper<Verifier>().eq(Verifier::getPlatformKey, platformKey), new Verifier(mobile)), VerifierVo.class);
        if (ObjectUtil.isNull(userVo)) {
            return null;
        }
        if (UserStatus.DISABLE.getCode().equals(userVo.getStatus())) {
            throw new UserException("user.blocked", mobile);
        }

        if (userVo.getIsAdmin() && !userVo.getIsBd()){
            CommercialTenantVo commercialTenantVo = commercialTenantMapper.selectVoOne(new LambdaQueryWrapper<CommercialTenant>().eq(CommercialTenant::getAdminMobile, userVo.getMobile()).eq(CommercialTenant::getStatus, "0"));
            //如果是商户登录查询是否已经创建商户如果没有联系bd或者管理员添加
            if (ObjectUtil.isEmpty(commercialTenantVo)){
                return null;
//                throw new ServiceException("请联系BD或管理员添加商户");
            }
        }

        // 此处可根据登录用户的数据不同 自行创建 loginUser
        return buildLoginUser(userVo);
    }

    @Override
    public XcxLoginUser getUserInfoByOpenId(String openId, Long platformKey, String platformType) throws UserException {
        VerifierVo userVo = verifierMapper.selectVoOne(new LambdaQueryWrapper<Verifier>().eq(Verifier::getPlatformKey, platformKey).eq(Verifier::getOpenId, openId));
        if (ObjectUtil.isNull(userVo)) {
            return null;
        }
        if (UserStatus.DISABLE.getCode().equals(userVo.getStatus())) {
            // 用户已被停用
            throw new UserException("user.blocked", userVo.getMobile());
        }
        if (userVo.getIsAdmin() && !userVo.getIsBd()){
            CommercialTenantVo commercialTenantVo = commercialTenantMapper.selectVoOne(new LambdaQueryWrapper<CommercialTenant>().eq(CommercialTenant::getAdminMobile, userVo.getMobile()).eq(CommercialTenant::getStatus, "0"));
            //如果是商户登录查询是否已经创建商户如果没有联系bd或者管理员添加
            if (ObjectUtil.isEmpty(commercialTenantVo)){
                return null;
//                throw new ServiceException("请联系BD或管理员添加商户");
            }
        }
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        return buildLoginUser(userVo);
    }

    /**
     * 注册用户
     *
     * @param loginEntity 用户相关信息
     */
    @Transactional
    @Override
    public XcxLoginUser register(LoginEntity loginEntity, String cityName, String cityCode, String platformType) {
        // 加锁
        String lockKey = loginEntity.getPlatformKey() + "_" + loginEntity.getMobile();
        final LockInfo lockInfo = lockTemplate.lock(lockKey, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            throw new ServiceException("操作频繁");
        }
        // 获取锁成功，处理业务
        try {
            XcxLoginUser userInfo = getUserInfoByOpenId(loginEntity.getOpenId(), loginEntity.getPlatformKey(), platformType);
            Verifier user = new Verifier();
            if (null == userInfo) {
                userInfo = getUserInfoByMobile(loginEntity.getOpenId(), loginEntity.getMobile(), loginEntity.getPlatformKey(), platformType);
                if (null == userInfo) {
                    return null;
                } else {
                    // 修改openId
                    //user.setUserId(userInfo.getUserId());
                    user.setId(userInfo.getUserId());
                    user.setReloadUser("1");
                    user.setOpenId(loginEntity.getOpenId());
                    verifierMapper.updateById(user);
                }
            } else {
                // 修改用户手机号
                user.setId(userInfo.getUserId());
                user.setReloadUser("1");
                user.setMobile(loginEntity.getMobile());
                verifierMapper.updateById(user);
            }
            // 再次查询用户
            return getUserInfoByMobile(loginEntity.getOpenId(), loginEntity.getMobile(), loginEntity.getPlatformKey(), platformType);
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 微信获取openId
     *
     * @param platformVo 平台
     * @param code       授权码
     * @return 登录信息
     */
    private WxEntity wxLoginEntity(PlatformVo platformVo, String code) {
        String url = wxProperties.getCodeSessionUrl() + "?appid=" + platformVo.getAppId() + "&secret=" + platformVo.getSecret() + "&js_code=" + code + "&grant_type=authorization_code";
        String result;
        try {
            result = HttpRequest.get(url).execute().body();
        } catch (Exception e) {
            log.error("平台【{}】请求获取微信用户openId异常，异常信息：", platformVo.getPlatformKey(), e);
            throw new ServiceException("系统繁忙，请稍后重试！");
        }
        WxEntity wxEntity = JsonUtils.parseObject(result, WxEntity.class);
        if (null == wxEntity || StringUtils.isEmpty(wxEntity.getOpenid())) {
            log.info("平台【{}】请求获取微信用户openId失败，返回信息：{}", platformVo.getPlatformKey(), result);
            throw new ServiceException("系统繁忙，请稍后重试！");
        }
        wxEntity.setOpenId(wxEntity.getOpenid());
        return wxEntity;
    }

    /**
     * 云闪付获取openId 手机号
     *
     * @param platformVo 平台
     * @param code       授权码
     * @param getMobile  是否获取手机号
     * @return 登录信息
     */
    private YsfEntity ysfLoginEntity(PlatformVo platformVo, String code, boolean getMobile) {
        // 获取基础服务令牌 backendToken
        String backendToken = YsfUtils.getBackendToken(platformVo.getAppId(), platformVo.getSecret(), false, platformVo.getPlatformKey());
        if (StringUtils.isEmpty(backendToken)) {
            throw new ServiceException("系统繁忙，请稍后重试！[backendToken is null]");
        }
        // 获取accessToken和openId
        BackendTokenEntity jsonObject = getTokenObject(backendToken, code, platformVo.getAppId(), platformVo.getPlatformKey());
        if (null == jsonObject) {
            throw new ServiceException("系统繁忙，请稍后重试！[token is null]");
        }
        if ("a10".equals(jsonObject.getResp())) {
            // 如果不合法的backend_token，或已过期[a10]  重新获取
            backendToken = YsfUtils.getBackendToken(platformVo.getAppId(), platformVo.getSecret(), true, platformVo.getPlatformKey());
            if (StringUtils.isEmpty(backendToken)) {
                throw new ServiceException("系统繁忙，请稍后重试！[backendToken is null]");
            }
            // 再次获取
            jsonObject = getTokenObject(backendToken, code, platformVo.getAppId(), platformVo.getPlatformKey());
            if (null == jsonObject) {
                throw new ServiceException("系统繁忙，请稍后重试！[token is null]");
            }
        }
        if (!"00".equals(jsonObject.getResp())) {
            throw new ServiceException(jsonObject.getMsg());
        }
        YsfEntity ysfEntity = new YsfEntity();
        ysfEntity.setBackendToken(backendToken);
        ysfEntity.setPlatformKey(platformVo.getPlatformKey());
        Map<String, String> params = jsonObject.getParams();
        ysfEntity.setOpenId(params.get("openId"));
        ysfEntity.setAccessToken(params.get("accessToken"));
        if (getMobile) {
            Map<String, String> mobileMap = getMobile(ysfEntity.getAccessToken(), ysfEntity.getOpenId(), ysfEntity.getBackendToken(), platformVo.getAppId(), platformVo.getPlatformKey());
            if (null == mobileMap) {
                throw new ServiceException("系统繁忙，请稍后重试！");
            }
            // 获取手机号
            String mobile;
            try {
                DESede desede = SecureUtil.desede(HexUtil.decodeHex(platformVo.getSymmetricKey()));
                mobile = desede.decryptStr(mobileMap.get("mobile"));
                ysfEntity.setMobile(mobile);
            } catch (Exception e) {
                log.error("云闪付解密联登手机号异常，密文：" + mobileMap.get("mobile") + "，异常信息：", e);
                throw new ServiceException("登录异常，请退出应用重试!");
            }
        }
        return ysfEntity;
    }

    /**
     * 构建登录用户
     */
    private XcxLoginUser buildLoginUser(VerifierVo userVo) {
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        XcxLoginUser loginUser = new XcxLoginUser();
        if (null != userVo.getId()) {
            loginUser.setUserId(userVo.getId());
            loginUser.setUsername(userVo.getId().toString());
            loginUser.setReloadUser(userVo.getReloadUser());
        }
        loginUser.setUserType(UserType.APP_USER.getUserType());
        return loginUser;
    }

    /**
     * 获取token的JSONObject
     *
     * @param backendToken 接口访问令牌
     * @param code         code
     * @return 结果
     */
    private BackendTokenEntity getTokenObject(String backendToken, String code, String appid, Long platformKey) {
        // 获取请求地址
        HashMap<String, String> param = new HashMap<>();
        param.put("appId", appid);
        param.put("backendToken", backendToken);
        param.put("code", code);
        param.put("grantType", "authorization_code");
        String result;
        try {
            result = HttpRequest.post(YsfPropertiesUtils.getTokenUrl(platformKey)).header("Content-Type", "application/json;charset=UTF-8").header("Accept", "application/json").body(JsonUtils.toJsonString(param), "application/json").execute().body();
        } catch (Exception e) {
            log.info("云闪付请求获取accessToken和openId异常，异常信息：", e);
            return null;
        }
        log.info("云闪付获取accessToken和openId返回结果：{}", result);
        return JsonUtils.parseObject(result, BackendTokenEntity.class);
    }

    /**
     * 获取用户手机号
     *
     * @param accessToken  获取手机号访问令牌
     * @param openId       openId
     * @param backendToken 接口访问令牌
     * @return 结果
     */
    private Map<String, String> getMobile(String accessToken, String openId, String backendToken, String appid, Long platformKey) {
        // 获取参数
        HashMap<String, String> param = new HashMap<>();
        param.put("appId", appid);
        param.put("backendToken", backendToken);
        param.put("openId", openId);
        param.put("accessToken", accessToken);
        String result;
        try {
            result = HttpRequest.post(YsfPropertiesUtils.getUserMobileUrl(platformKey)).header("Content-Type", "application/json;charset=UTF-8").header("Accept", "application/json").body(JsonUtils.toJsonString(param), "application/json").execute().body();
        } catch (Exception e) {
            log.error("云闪付请求获取用户手机号异常，异常信息：", e);
            return null;
        }
        log.info("云闪付获取手机号返回结果：{}", result);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        BackendTokenEntity jsonObject = JsonUtils.parseObject(result, BackendTokenEntity.class);
        if (null == jsonObject || !"00".equals(jsonObject.getResp())) {
            return null;
        }
        return jsonObject.getParams();
    }

    @Async
    @Override
    public void recordLoginDate(Long userId, Long userChannelId, String ip) {
        Verifier user = verifierMapper.selectById(userId);
        if (ObjectUtil.isEmpty(user)) {
            return;
        }
        user.setLastLoginDate(user.getLoginDate());
        user.setLoginIp(ip);
        user.setLoginDate(new Date());
        verifierMapper.updateById(user);
    }

    /**
     * 小程序端退出登录
     */
    @Override
    public void logout(Long userId) {
        if (null == userId) {
            return;
        }
        Verifier verifier = verifierMapper.selectById(userId);
        if (null == verifier) {
            return;
        }
        verifier.setReloadUser("0");
        verifierMapper.updateById(verifier);
    }
}
