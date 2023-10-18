package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.vo.MemberVipBalanceVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.mapper.UserMapper;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.UserRecordLog;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

/**
 * 用户信息Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper baseMapper;
    private final IPlatformService platformService;

    /**
     * 查询单个用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserVo queryById(Long userId) {
        return baseMapper.selectVoById(userId);
    }

    /**
     * 查询用户是否62会员
     *
     * @param isCache 是否优先查询缓存，true是，false否
     * @return 结果
     */
    @Override
    public MemberVipBalanceVo getUser62VipInfo(boolean isCache, Long userId) {
        UserVo userVo = this.queryById(userId);
        if (null == userVo || StringUtils.isBlank(userVo.getMobile())) {
            return null;
        }
        String cacheKey = "user62Vips:" + userVo.getMobile();
        MemberVipBalanceVo memberVipBalanceVo;
        if (isCache) {
            log.info("查询62会员走缓存，userId={}", userId);
            memberVipBalanceVo = RedisUtils.getCacheObject(cacheKey);
            if (null != memberVipBalanceVo) {
                return memberVipBalanceVo;
            }
        } else {
            log.info("查询62会员不走缓存，userId={}", userId);
        }
        PlatformVo platformVo = platformService.queryById(userVo.getPlatformKey(), PlatformEnumd.MP_YSF);
        if (null == platformVo || "0".equals(platformVo.getUnionPayVip())) {
            return null;
        }
        memberVipBalanceVo = YsfUtils.queryMemberVipBalance(userVo.getMobile(), platformVo.getAppId(), platformVo.getSecret(), platformVo.getSymmetricKey(), platformVo.getPlatformKey());
        if (null != memberVipBalanceVo) {
            // 缓存
            if ("01".equals(memberVipBalanceVo.getStatus()) || "03".equals(memberVipBalanceVo.getStatus())) {
                Date date = DateUtil.parse(memberVipBalanceVo.getEndTime()).toJdkDate();
                long datePoorHour = DateUtils.getDatePoorHour(date, new Date());
                if (datePoorHour > 0) {
                    if (datePoorHour < 40) {
                        // 是62会员用户，缓存30-40个小时
                        RedisUtils.setCacheObject(cacheKey, memberVipBalanceVo, Duration.ofHours(datePoorHour));
                    } else {
                        // 是62会员用户，缓存30-40个小时
                        RedisUtils.setCacheObject(cacheKey, memberVipBalanceVo, Duration.ofHours(RandomUtil.randomInt(30, 40)));
                    }
                } else {
                    RedisUtils.setCacheObject(cacheKey, memberVipBalanceVo, Duration.ofMinutes(RandomUtil.randomInt(60, 120)));
                }
            } else {
                // 普通用户，缓存1-2小时
                RedisUtils.setCacheObject(cacheKey, memberVipBalanceVo, Duration.ofMinutes(RandomUtil.randomInt(60, 120)));
            }
        }
        return memberVipBalanceVo;
    }

    @Override
    public void userFollow(String code) {
        if ("00".equals(code) || "01".equals(code)) {
            User user = new User();
            user.setUserId(LoginHelper.getUserId());
            user.setFollowStatus("1");
            baseMapper.updateById(user);
        }
    }

    /**
     * 权益会员到期
     *
     * @param userId 用户ID
     */
    @Override
    public void vipExpiry(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setVipUser("2");
        baseMapper.updateById(user);
    }

    @Override
    public String getOpenIdByMobile(Long platformKey, String mobile) {
        User user = baseMapper.selectOneIncludeMobile(new LambdaQueryWrapper<User>().eq(User::getPlatformKey, platformKey), new User(mobile));
        if (null == user) {
            return null;
        }
        return user.getOpenId();
    }

    @Override
    public void userLog(UserRecordLog recordLog) {
        Long userId = LoginHelper.getUserId();
        Long platformKey = ZlyyhUtils.getPlatformId();
        String nowDate = DateUtils.getDate();
        String redisKey = nowDate + ":" + recordLog.getSource() + ":" + platformKey;
        if (userId != null) {
            recordLog.setUserId(userId);
        }
        recordLog.setPlatformKey(platformKey);

        boolean exists = RedisUtils.isExistsObject(redisKey);
        RedisUtils.setCacheList(redisKey, recordLog);
        if (!exists) {
            RedisUtils.expire(redisKey, Duration.ofHours(50));
        }

        boolean existsObject = RedisUtils.isExistsObject(nowDate + ":userLogs");
        RedisUtils.setCacheSet(nowDate + ":userLogs", redisKey);
        if (!existsObject) {
            RedisUtils.expire(nowDate + ":userLogs", Duration.ofHours(50));
        }
    }
}
