package com.ruoyi.zlyyh.utils;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.vo.DrawVo;
import com.ruoyi.zlyyh.enumd.DateType;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

/**
 * @author 25487
 */
public class DrawRedisCacheUtils {

    /**
     * 获取用户任务发放额度
     *
     * @param missionId 任务ID
     * @param dateType  时间类型
     * @return 发放数量
     */
    public static double getUserMissionQuotaCount(Long missionId, Long missionUserId, DateType dateType) {
        return RedisUtils.getAtomicDouble(getUserMissionQuotaRedisKey(missionId, missionUserId, dateType));
    }

    /**
     * 添加用户任务发放额度
     *
     * @param missionId     任务ID
     * @param missionUserId 任务ID
     * @param quota         需要添加的额度
     * @param dateType      时间类型
     */
    public static void addUserMissionTotalQuota(Long missionId, Long missionUserId, BigDecimal quota, DateType dateType) {
        if (null == quota) {
            return;
        }
        RedisUtils.addAtomicDouble(getUserMissionQuotaRedisKey(missionId, missionUserId, dateType), quota.doubleValue());
    }

    /**
     * 设置用户任务发放额度
     *
     * @param missionId     任务ID
     * @param missionUserId 任务ID
     * @param quota         需要设置的额度
     * @param dateType      时间类型
     */
    public static void setUserMissionTotalQuota(Long missionId, Long missionUserId, BigDecimal quota, DateType dateType, Duration duration) {
        if (null == quota) {
            return;
        }
        RedisUtils.setAtomicDouble(getUserMissionQuotaRedisKey(missionId, missionUserId, dateType), quota.doubleValue());
        duration = ZlyyhUtils.getDurationByDateTypeAndDefault(dateType, duration);
        RedisUtils.expire(getUserMissionQuotaRedisKey(missionId, missionUserId, dateType), duration);
    }

    /**
     * 获取用户任务发放额度缓存key
     *
     * @param missionId 奖品ID
     * @param dateType  时间类型
     * @return redis缓存key
     */
    private static String getUserMissionQuotaRedisKey(Long missionId, Long missionUserId, DateType dateType) {
        return "userMissionQuotaCache:" + missionId + ":" + missionUserId + ":" + ZlyyhUtils.getDateCacheKey(dateType);
    }

    /**
     * 获取任务发放额度
     *
     * @param missionId 任务ID
     * @param dateType  时间类型
     * @return 发放数量
     */
    public static double getMissionQuotaCount(Long missionId, DateType dateType) {
        return RedisUtils.getAtomicDouble(getMissionQuotaRedisKey(missionId, dateType));
    }

    /**
     * 添加任务发放额度
     *
     * @param missionId 任务ID
     * @param quota     需要添加的额度
     * @param dateType  时间类型
     */
    public static void addMissionTotalQuota(Long missionId, BigDecimal quota, DateType dateType) {
        if (null == quota) {
            return;
        }
        RedisUtils.addAtomicDouble(getMissionQuotaRedisKey(missionId, dateType), quota.doubleValue());
    }

    /**
     * 设置任务发放额度
     *
     * @param missionId 任务ID
     * @param quota     需要设置的额度
     * @param dateType  时间类型
     */
    public static void setMissionTotalQuota(Long missionId, BigDecimal quota, DateType dateType, Duration duration) {
        if (null == quota) {
            return;
        }
        RedisUtils.setAtomicDouble(getMissionQuotaRedisKey(missionId, dateType), quota.doubleValue());
        duration = ZlyyhUtils.getDurationByDateTypeAndDefault(dateType, duration);
        RedisUtils.expire(getMissionQuotaRedisKey(missionId, dateType), duration);
    }

    /**
     * 获取任务发放额度缓存key
     *
     * @param missionId 奖品ID
     * @param dateType  时间类型
     * @return redis缓存key
     */
    private static String getMissionQuotaRedisKey(Long missionId, DateType dateType) {
        return "missionQuotaCache:" + missionId + ":" + ZlyyhUtils.getDateCacheKey(dateType);
    }

    /**
     * 获取奖品发放数量缓存
     *
     * @param drawId   奖品ID
     * @param dateType 时间类型
     * @return 发放数量
     */
    public static long getDrawCount(Long drawId, DateType dateType) {
        return RedisUtils.getAtomicValue(getDrawCountRedisKey(drawId, dateType));
    }

    /**
     * 增加奖品发放数量缓存
     *
     * @param drawId   奖品ID
     * @param dateType 时间类型
     */
    public static void addDrawCount(Long drawId, DateType dateType) {
        RedisUtils.incrAtomicValue(getDrawCountRedisKey(drawId, dateType));
    }

    /**
     * 设置奖品发放数量缓存
     *
     * @param drawId   奖品ID
     * @param dateType 时间类型
     */
    public static void setDrawCount(Long drawId, DateType dateType, Long value, Duration duration) {
        RedisUtils.setAtomicValue(getDrawCountRedisKey(drawId, dateType), value);
        duration = ZlyyhUtils.getDurationByDateTypeAndDefault(dateType, duration);
        RedisUtils.expire(getDrawCountRedisKey(drawId, dateType), duration);
    }

    /**
     * 获取奖品发放次数缓存key
     *
     * @param drawId   奖品ID
     * @param dateType 时间类型
     * @return redis缓存key
     */
    private static String getDrawCountRedisKey(Long drawId, DateType dateType) {
        return "drawLimitCache:" + drawId + ":" + ZlyyhUtils.getDateCacheKey(dateType);
    }

    /**
     * 获取奖品发放数量缓存
     *
     * @param drawId        奖品ID
     * @param missionUserId 活动用户ID
     * @param dateType      时间类型
     * @return 发放数量
     */
    public static long getUserDrawCount(Long drawId, Long missionUserId, DateType dateType) {
        return RedisUtils.getAtomicValue(getUserDrawCountRedisKey(drawId, missionUserId, dateType));
    }

    /**
     * 添加奖品发放数量缓存
     *
     * @param drawId        奖品ID
     * @param missionUserId 活动用户ID
     * @param dateType      时间类型
     */
    public static void addUserDrawCount(Long drawId, Long missionUserId, DateType dateType) {
        RedisUtils.incrAtomicValue(getUserDrawCountRedisKey(drawId, missionUserId, dateType));
    }

    /**
     * 设置用户奖品发放数量缓存
     *
     * @param drawId   奖品ID
     * @param dateType 时间类型
     */
    public static void setUserDrawCount(Long drawId, Long missionUserId, DateType dateType, Long value, Duration duration) {
        RedisUtils.setAtomicValue(getUserDrawCountRedisKey(drawId, missionUserId, dateType), value);
        duration = ZlyyhUtils.getDurationByDateTypeAndDefault(dateType, duration);
        RedisUtils.expire(getUserDrawCountRedisKey(drawId, missionUserId, dateType), duration);
    }

    /**
     * 获取奖品发放次数缓存key
     *
     * @param drawId   奖品ID
     * @param dateType 时间类型
     * @return redis缓存key
     */
    private static String getUserDrawCountRedisKey(Long drawId, Long missionUserId, DateType dateType) {
        return "drawUserLimitCache:" + drawId + ":" + missionUserId + ":" + ZlyyhUtils.getDateCacheKey(dateType);
    }

    /**
     * 获取奖品列表当日缓存
     *
     * @param platformKey    平台
     * @param missionGroupId 任务组
     * @return 结果
     */
    public static List<DrawVo> getDrawList(Long platformKey, Long missionGroupId) {
        return RedisUtils.getCacheObject(getDrawListCacheKey(platformKey, missionGroupId));
    }

    /**
     * 缓存奖品列表当日缓存
     *
     * @param platformKey    平台
     * @param missionGroupId 任务组
     * @param drawVos        奖品列表
     */
    public static void setDrawList(Long platformKey, Long missionGroupId, List<DrawVo> drawVos) {
        RedisUtils.setCacheObject(getDrawListCacheKey(platformKey, missionGroupId), drawVos, Duration.ofDays(3));
    }

    /**
     * 删除奖品列表当日缓存
     *
     * @param platformKey    平台
     * @param missionGroupId 任务组
     */
    public static void delDrawList(Long platformKey, Long missionGroupId) {
        RedisUtils.deleteObject(getDrawListCacheKey(platformKey, missionGroupId));
    }

    private static String getDrawListCacheKey(Long platformKey, Long missionGroupId) {
        return "drawListCacheKey:" + platformKey + ":" + missionGroupId + ":" + DateUtil.today();
    }
}
