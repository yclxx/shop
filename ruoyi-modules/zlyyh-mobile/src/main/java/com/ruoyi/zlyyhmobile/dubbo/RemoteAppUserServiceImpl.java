package com.ruoyi.zlyyhmobile.dubbo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DESede;
import cn.hutool.http.HttpRequest;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.enums.UserStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.user.UserException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteAppUserService;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.system.api.model.XcxLoginUser;
import com.ruoyi.system.api.model.YsfEntity;
import com.ruoyi.zlyyh.domain.RecordLog;
import com.ruoyi.zlyyh.domain.vo.BackendTokenEntity;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.RecordLogMapper;
import com.ruoyi.zlyyh.mapper.UserMapper;
import com.ruoyi.zlyyh.properties.utils.YsfPropertiesUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyhmobile.domain.bo.UserRecordLog;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    private final RecordLogMapper recordLogMapper;
    private final IPlatformService platformService;
    private final IOrderService orderService;
    @Autowired
    private LockTemplate lockTemplate;

    /**
     * 获取用户授权信息
     *
     * @return openId，令牌等相关信息
     */
    @Override
    public YsfEntity getYsfEntity(String code, boolean getMobile, Long platformKey) {
        PlatformVo platformVo = platformService.queryById(platformKey);
        if (null == platformVo) {
            throw new ServiceException("请求错误，请退出重试！[platform is null]");
        }
        if (StringUtils.isBlank(platformVo.getAppId()) || StringUtils.isBlank(platformVo.getSecret())) {
            throw new ServiceException("请求错误，请退出重试！[platform settings error]");
        }
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
        ysfEntity.setPlatformKey(platformKey);
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

    @Override
    public XcxLoginUser getUserInfoByMobile(String openId, String mobile, Long platformKey) throws UserException {
        UserVo userVo = BeanCopyUtils.copy(userMapper.selectOneIncludeMobile(new LambdaQueryWrapper<User>().eq(User::getPlatformKey, platformKey), new User(mobile)), UserVo.class);
        if (ObjectUtil.isNull(userVo)) {
            return null;
        }
        if (UserStatus.DISABLE.getCode().equals(userVo.getStatus())) {
            throw new UserException("user.blocked", mobile);
        }
        if (StringUtils.isNotBlank(openId) && !openId.equals(userVo.getOpenId())) {
            UserVo openIdUser = userMapper.selectVoOne(new LambdaQueryWrapper<User>().eq(User::getPlatformKey, platformKey).eq(User::getOpenId, openId));
            if (ObjectUtil.isNotNull(openIdUser)) {
                // 修改原來的用戶openId
                User user = new User();
                user.setUserId(openIdUser.getUserId());
                user.setOpenId(openIdUser.getOpenId() + "_del" + RandomUtil.randomNumbers(2));
                userMapper.updateById(user);
            }
            userVo.setOpenId(openId);
            // 修改openId
            User user = new User();
            user.setUserId(userVo.getUserId());
            user.setOpenId(openId);
            userMapper.updateById(user);
        }
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        return buildLoginUser(userVo);
    }

    @Override
    public XcxLoginUser getUserInfoByOpenid(String openId, Long platformKey) throws UserException {
        UserVo userVo = userMapper.selectVoOne(new LambdaQueryWrapper<User>().eq(User::getPlatformKey, platformKey).eq(User::getOpenId, openId));
        if (ObjectUtil.isNull(userVo)) {
            return null;
        }
        if (UserStatus.DISABLE.getCode().equals(userVo.getStatus())) {
            // 用户已被停用
            throw new UserException("user.blocked", userVo.getMobile());
        }
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        return buildLoginUser(userVo);
    }

    /**
     * 查询用户
     *
     * @param userId 用户相关信息
     * @return 结果
     */
    @Override
    public XcxLoginUser queryByUserId(Long userId, Long platformKey) {
        UserVo userVo = userMapper.selectVoById(userId);
        if (ObjectUtil.isNull(userVo) || !userVo.getPlatformKey().equals(platformKey)) {
            return null;
        }
        if (UserStatus.DISABLE.getCode().equals(userVo.getStatus())) {
            // 用户已被停用
            throw new UserException("user.blocked", userVo.getMobile());
        }
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        return buildLoginUser(userVo);
    }

    /**
     * 注册用户
     *
     * @param ysfEntity 用户相关信息
     */
    @Override
    public XcxLoginUser register(YsfEntity ysfEntity, String cityName, String cityCode) {
        // 加锁
        String lockKey = ysfEntity.getPlatformKey() + "_" + ysfEntity.getMobile();
        final LockInfo lockInfo = lockTemplate.lock(lockKey, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            throw new ServiceException("操作频繁");
        }
        // 获取锁成功，处理业务
        try {
            XcxLoginUser userInfo = getUserInfoByOpenid(ysfEntity.getOpenId(), ysfEntity.getPlatformKey());
            User user = new User();
            user.setReloadUser("1");
            if (null == userInfo) {
                userInfo = getUserInfoByMobile(ysfEntity.getOpenId(), ysfEntity.getMobile(), ysfEntity.getPlatformKey());
                if (null == userInfo) {
                    // 新增用户
                    user.setMobile(ysfEntity.getMobile());
                    user.setOpenId(ysfEntity.getOpenId());
                    user.setPlatformKey(ysfEntity.getPlatformKey());
                    user.setRegisterCityName(cityName);
                    user.setRegisterCityCode(cityCode);
                    // 新增用户
                    userMapper.insert(user);
                } else {
                    // 修改openId
                    user.setUserId(userInfo.getUserId());
                    user.setOpenId(ysfEntity.getOpenId());
                    userMapper.updateById(user);
                }
            } else {
                // 修改用户手机号
                user.setUserId(userInfo.getUserId());
                user.setMobile(ysfEntity.getMobile());
                userMapper.updateById(user);
            }
            // 再次查询用户
            return getUserInfoByMobile(ysfEntity.getOpenId(), ysfEntity.getMobile(), ysfEntity.getPlatformKey());
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 小程序端退出登录
     */
    @Override
    public void logout(Long userId) {
        if (null == userId) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (null == user) {
            return;
        }
        user.setReloadUser("0");
        userMapper.updateById(user);
    }

    /**
     * 构建登录用户
     */
    private XcxLoginUser buildLoginUser(UserVo userVo) {
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        XcxLoginUser loginUser = new XcxLoginUser();
        if (null != userVo.getUserId()) {
            loginUser.setUserId(userVo.getUserId());
            loginUser.setUsername(userVo.getUserId().toString());
        }
        loginUser.setUserType("app_user");
        loginUser.setOpenid(userVo.getOpenId());
        loginUser.setReloadUser(userVo.getReloadUser());
        loginUser.setCreateTime(userVo.getCreateTime());
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
    public void recordLoginDate(Long userId, String cityName, String cityCode, String ip) {
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
    }
}
