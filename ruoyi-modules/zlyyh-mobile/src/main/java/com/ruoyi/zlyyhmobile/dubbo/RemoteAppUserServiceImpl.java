package com.ruoyi.zlyyhmobile.dubbo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
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
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteAppUserService;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.system.api.model.LoginEntity;
import com.ruoyi.system.api.model.WxEntity;
import com.ruoyi.system.api.model.XcxLoginUser;
import com.ruoyi.system.api.model.YsfEntity;
import com.ruoyi.zlyyh.domain.RecordLog;
import com.ruoyi.zlyyh.domain.UserChannel;
import com.ruoyi.zlyyh.domain.vo.BackendTokenEntity;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.domain.vo.UserChannelVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.mapper.RecordLogMapper;
import com.ruoyi.zlyyh.mapper.UserChannelMapper;
import com.ruoyi.zlyyh.mapper.UserMapper;
import com.ruoyi.zlyyh.properties.WxProperties;
import com.ruoyi.zlyyh.properties.utils.YsfPropertiesUtils;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyh.utils.WxUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.UserRecordLog;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteAppUserServiceImpl implements RemoteAppUserService {

    private final UserMapper userMapper;
    private final UserChannelMapper userChannelMapper;
    private final RecordLogMapper recordLogMapper;
    private final IPlatformService platformService;
    private final IOrderService orderService;
    private final WxProperties wxProperties;
    @Autowired
    private LockTemplate lockTemplate;

    /**
     * 获取微信用户手机号
     */
    @Override
    public String getWxMobile(String code, Long platformKey) {
        PlatformVo platformVo = platformService.queryById(platformKey, PlatformEnumd.MP_WX);
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
     * 获取用户授权信息
     *
     * @return openId，令牌等相关信息
     */
    @Override
    public LoginEntity getEntity(String code, boolean getMobile, Long platformKey, String platformChannel) {
        PlatformEnumd platformType = PlatformEnumd.getPlatformEnumd(platformChannel);
        PlatformVo platformVo = platformService.queryById(platformKey, platformType);
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
        UserVo userVo = BeanCopyUtils.copy(userMapper.selectOneIncludeMobile(new LambdaQueryWrapper<User>().eq(User::getPlatformKey, platformKey), new User(mobile)), UserVo.class);
        if (ObjectUtil.isNull(userVo)) {
            return null;
        }
        if (UserStatus.DISABLE.getCode().equals(userVo.getStatus())) {
            throw new UserException("user.blocked", mobile);
        }
        String channel = ZlyyhUtils.getPlatformChannel(platformType);
        UserChannelVo userChannelVo = this.queryByUserId(channel, userVo.getUserId(), platformKey);
        if (null == userChannelVo) {
            return null;
        }
        if (StringUtils.isNotBlank(openId) && !openId.equals(userChannelVo.getOpenId())) {
            UserChannelVo openIdUserChannelVo = this.queryByOpenId(channel, openId, platformKey);
            if (null != openIdUserChannelVo) {
                // 修改原來的用戶openId
                UserChannel userChannel = new UserChannel();
                userChannel.setId(openIdUserChannelVo.getId());
                userChannel.setOpenId(openIdUserChannelVo.getOpenId() + "_del" + RandomUtil.randomNumbers(2));
                userChannelMapper.updateById(userChannel);
            }
            userChannelVo.setOpenId(openId);
            // 修改openId
            UserChannel userChannel = new UserChannel();
            userChannel.setId(userChannelVo.getId());
            userChannel.setOpenId(openId);
            userChannelMapper.updateById(userChannel);
        }
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        return buildLoginUser(userVo, userChannelVo);
    }

    @Override
    public XcxLoginUser getUserInfoByOpenid(String openId, Long platformKey, String platformType) throws UserException {
        String channel = ZlyyhUtils.getPlatformChannel(platformType);
        UserChannelVo userChannelVo = this.queryByOpenId(channel, openId, platformKey);
        UserVo userVo;
        if (null == userChannelVo) {
            if (!"0".equals(channel)) {
                return null;
            }
            userVo = userMapper.selectVoOne(new LambdaQueryWrapper<User>().eq(User::getPlatformKey, platformKey).eq(User::getOpenId, openId));
            if (null != userVo) {
                insertUserChannel(userVo, openId, channel);
                userChannelVo = this.queryByOpenId(channel, openId, platformKey);
            }
        } else {
            userVo = userMapper.selectVoById(userChannelVo.getUserId());
        }
        if (ObjectUtil.isNull(userVo)) {
            return null;
        }
        if (UserStatus.DISABLE.getCode().equals(userVo.getStatus())) {
            // 用户已被停用
            throw new UserException("user.blocked", userVo.getMobile());
        }
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        return buildLoginUser(userVo, userChannelVo);
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
            XcxLoginUser userInfo = getUserInfoByOpenid(loginEntity.getOpenId(), loginEntity.getPlatformKey(), platformType);
            User user = new User();
            user.setReloadUser("1");
            if (null == userInfo) {
                userInfo = getUserInfoByMobile(loginEntity.getOpenId(), loginEntity.getMobile(), loginEntity.getPlatformKey(), platformType);
                if (null == userInfo) {
                    // 新增用户
                    user.setMobile(loginEntity.getMobile());
                    user.setOpenId(loginEntity.getOpenId());
                    user.setPlatformKey(loginEntity.getPlatformKey());
                    user.setRegisterCityName(cityName);
                    user.setRegisterCityCode(cityCode);
                    PermissionUtils.setPlatformDeptIdAndUserId(user, user.getPlatformKey(), true, false);
                    // 新增用户
                    userMapper.insert(user);

                } else {
                    // 修改openId
                    user.setUserId(userInfo.getUserId());
                    user.setOpenId(loginEntity.getOpenId());
                    userMapper.updateById(user);
                }
            } else {
                // 修改用户手机号
                user.setUserId(userInfo.getUserId());
                user.setMobile(loginEntity.getMobile());
                userMapper.updateById(user);
            }
            String channel = ZlyyhUtils.getPlatformChannel(platformType);
            // 查询渠道用户是否存在
            UserChannelVo userChannelVo = this.queryByOpenId(channel, loginEntity.getOpenId(), loginEntity.getPlatformKey());
            if (null == userChannelVo) {
                // 根据openId查询
                userChannelVo = this.queryByUserId(channel, user.getUserId(), loginEntity.getPlatformKey());
                if (null == userChannelVo) {
                    insertUserChannel(user.getUserId(), loginEntity.getOpenId(), cityName, cityCode, loginEntity.getPlatformKey(), channel);
                } else {
                    updateUserChannel(null, loginEntity.getOpenId(), userChannelVo.getId());
                }
            } else {
                updateUserChannel(user.getUserId(), null, userChannelVo.getId());
            }
            // 再次查询用户
            return getUserInfoByMobile(loginEntity.getOpenId(), loginEntity.getMobile(), loginEntity.getPlatformKey(), platformType);
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 新增渠道用户
     *
     * @param userId   用户ID
     * @param openId   openId
     * @param cityName 城市
     * @param cityCode 城市行政区号
     */
    private void insertUserChannel(Long userId, String openId, String cityName, String cityCode, Long platformKey, String channel) {
        // 新增渠道用户
        UserChannel userChannel = new UserChannel();
        userChannel.setUserId(userId);
        userChannel.setOpenId(openId);
        userChannel.setReloadUser("1");
        userChannel.setChannel(channel);
        userChannel.setRegisterCityName(cityName);
        userChannel.setRegisterCityCode(cityCode);
        userChannel.setPlatformKey(platformKey);

        userChannelMapper.insert(userChannel);
    }

    /**
     * 新增渠道用户
     *
     * @param userVo  用户
     * @param openId  openId
     * @param channel 渠道
     */
    private void insertUserChannel(UserVo userVo, String openId, String channel) {
        // 新增渠道用户
        UserChannel userChannel = new UserChannel();
        userChannel.setUserId(userVo.getUserId());
        userChannel.setOpenId(openId);
        userChannel.setReloadUser(userVo.getReloadUser());
        userChannel.setRegisterCityCode(userVo.getRegisterCityCode());
        userChannel.setRegisterCityName(userVo.getRegisterCityName());
        userChannel.setFollowStatus(userVo.getFollowStatus());
        userChannel.setChannel(channel);
        userChannel.setCreateTime(userVo.getCreateTime());
        userChannel.setPlatformKey(userVo.getPlatformKey());

        userChannelMapper.insert(userChannel);
    }

    /**
     * 修改渠道用户
     *
     * @param userId    用户ID
     * @param openId    openId
     * @param channelId 渠道用户
     */
    private void updateUserChannel(Long userId, String openId, Long channelId) {
        // 新增渠道用户
        UserChannel userChannel = new UserChannel();
        userChannel.setId(channelId);
        if (StringUtils.isNotBlank(openId)) {
            userChannel.setOpenId(openId);
        }
        if (null != userId) {
            userChannel.setUserId(userId);
        }
        userChannel.setReloadUser("1");

        userChannelMapper.updateById(userChannel);
    }

    /**
     * 小程序端退出登录
     */
    @Override
    public void logout(Long userId, String platformType) {
        if (null == userId) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (null == user) {
            return;
        }
        String channel = ZlyyhUtils.getPlatformChannel(platformType);
        UserChannelVo userChannelVo = this.queryByUserId(channel, user.getUserId(), user.getPlatformKey());
        if (null == userChannelVo) {
            return;
        }
        UserChannel userChannel = new UserChannel();
        userChannel.setId(userChannelVo.getId());
        userChannel.setReloadUser("0");

        userChannelMapper.updateById(userChannel);
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
            if (ObjectUtil.isEmpty(mobileMap)) {
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
    private XcxLoginUser buildLoginUser(UserVo userVo, UserChannelVo userChannelVo) {
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        XcxLoginUser loginUser = new XcxLoginUser();
        if (null != userVo.getUserId()) {
            loginUser.setUserId(userVo.getUserId());
            loginUser.setUsername(userVo.getUserId().toString());
        }
        loginUser.setUserType(UserType.APP_USER.getUserType());
        loginUser.setOpenid(userChannelVo.getOpenId());
        loginUser.setReloadUser(userChannelVo.getReloadUser());
        loginUser.setCreateTime(userChannelVo.getCreateTime());
        loginUser.setUserChannelId(userChannelVo.getId());
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
    public void userLog() throws InterruptedException {
        Date yesterday = DateUtil.yesterday();
        String yesterdays = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, yesterday);
        //Date toDay = DateUtil.date();
        //String nowDate = DateUtils.getDate();
        int end = 9;
        List<Long> users = new ArrayList<>();
        Set<String> keys = RedisUtils.getCacheSet(yesterdays + ":userLogs");
        Long orderBuyNumber = 0L;
        Long orderBuyUser = 0L;
        for (String key : keys) {
            String keyUserId = key + ":userId";
            int keySize = RedisUtils.getCacheListSize(key);
            int start = 0;
            do {
                // 从内部获取
                List<UserRecordLog> logs = RedisUtils.getCacheList(key, start, end);
                if (ObjectUtil.isEmpty(logs)) {
                    break;
                }
                start += end;
                // 处理用户记录数量
                long userNumber = logs.stream().filter(item -> item.getUserId() != null || item.getOpenId() != null).count();
                users.add(userNumber);
                // 获取用户登录后的userId，并缓存
                List<Long> userIds = logs.stream().map(UserRecordLog::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
                if (!userIds.isEmpty()) {
                    HashSet<Long> set = new HashSet<>(userIds);
                    RedisUtils.setCacheSet(keyUserId, set);
                }
                Thread.sleep(10);
            } while (start <= keySize);
            // 处理用户订单数据
            Set<Long> userSet = RedisUtils.getCacheSet(keyUserId);
            if (!userSet.isEmpty()) {
                List<Long> userIds = new ArrayList<>(userSet);
                // 计算订单购买数量
                orderBuyNumber = orderService.queryNumberByUserId(userIds, "2", 1, yesterday);
                // 计算订单购买人数
                orderBuyUser = orderService.queryNumberByUserId(userIds, "2", 2, yesterday);
                RedisUtils.deleteObject(keyUserId);
            }

            String[] oldKey = key.split(":");
            RecordLog log = new RecordLog();
            log.setUserNumber((long) keySize);
            log.setUserPeople(users.stream().reduce(Long::sum).orElse(0L));
            if (oldKey.length > 0) {
                log.setSource(oldKey[1]);
            }
            if (oldKey.length > 1) {
                log.setPlatformKey(Long.valueOf(oldKey[2]));
            }
            log.setOrderBuyNumber(orderBuyNumber);
            log.setOrderBuyUser(orderBuyUser);
            log.setRecordDate(yesterdays);
            recordLogMapper.insert(log);
        }
    }

    @Async
    @Override
    public void recordLoginDate(Long userId, Long userChannelId, String cityName, String cityCode, String ip) {
        User user = userMapper.selectById(userId);
        if (null == user) {
            return;
        }
        user.setLastLoginDate(user.getLoginDate());
        user.setLoginIp(ip);
        user.setLoginDate(new Date());
        user.setLoginCityName(cityName);
        user.setLoginCityCode(cityCode);
        userMapper.updateById(user);

        UserChannelVo userChannelVo = userChannelMapper.selectVoById(userChannelId);
        if (null == userChannelVo) {
            return;
        }
        UserChannel userChannel = new UserChannel();
        userChannel.setId(userChannelVo.getId());
        userChannel.setLastLoginDate(userChannelVo.getLoginDate());
        userChannel.setLoginIp(ip);
        userChannel.setLoginDate(new Date());
        userChannel.setLoginCityName(cityName);
        userChannel.setLoginCityCode(cityCode);
        userChannelMapper.updateById(userChannel);
    }

    private UserChannelVo queryByOpenId(String channel, String openId, Long platformKey) {
        return userChannelMapper.selectVoOne(new LambdaQueryWrapper<UserChannel>().eq(UserChannel::getChannel, channel).eq(UserChannel::getOpenId, openId).eq(UserChannel::getPlatformKey, platformKey));
    }

    private UserChannelVo queryByUserId(String channel, Long userId, Long platformKey) {
        return userChannelMapper.selectVoOne(new LambdaQueryWrapper<UserChannel>().eq(UserChannel::getChannel, channel).eq(UserChannel::getUserId, userId).eq(UserChannel::getPlatformKey, platformKey));
    }
}
