package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.constant.YsfUpConstants;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.MissionGroupProduct;
import com.ruoyi.zlyyh.domain.MissionUserRecord;
import com.ruoyi.zlyyh.domain.MissionUserRecordLog;
import com.ruoyi.zlyyh.domain.bo.DrawBo;
import com.ruoyi.zlyyh.domain.bo.MissionBo;
import com.ruoyi.zlyyh.domain.bo.MissionGroupProductBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.enumd.DateType;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.mapper.MissionGroupProductMapper;
import com.ruoyi.zlyyh.mapper.MissionUserRecordLogMapper;
import com.ruoyi.zlyyh.mapper.MissionUserRecordMapper;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.*;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.domain.vo.UserProductCount;
import com.ruoyi.zlyyhmobile.event.CacheMissionRecordEvent;
import com.ruoyi.zlyyhmobile.event.MissionUserRecordEvent;
import com.ruoyi.zlyyhmobile.service.*;
import com.ruoyi.zlyyhmobile.utils.AliasMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动记录Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MissionUserRecordServiceImpl implements IMissionUserRecordService {

    private final MissionUserRecordMapper baseMapper;
    private final IMissionUserService iMissionUserService;
    private final IDrawService drawService;
    private final IMissionGroupService missionGroupService;
    private final IMissionService missionService;
    private final IUserService userService;
    private final MissionUserRecordLogMapper missionUserRecordLogMapper;
    private final MissionGroupProductMapper missionGroupProductMapper;
    private final IProductService productService;
    private final IOrderService orderService;
    private final IPlatformService platformService;
    private final AsyncSecondService asyncSecondService;
    private final YsfConfigService ysfConfigService;
    @Autowired
    private LockTemplate lockTemplate;

    /**
     * 查询奖品列表
     *
     * @param missionGroupId 任务组ID
     * @return
     */
    @Override
    public List<MissionUserRecordVo> getRecordList(Long missionGroupId) {
        String key = CacheNames.recordList + ":" + missionGroupId;
        List<MissionUserRecordVo> recordVos = RedisUtils.getCacheList(key);
        if (ObjectUtil.isNotEmpty(recordVos)) {
            return recordVos;
        }
        final LockInfo lockInfo = lockTemplate.lock("lock" + key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return new ArrayList<>();
        }
        // 获取锁成功，处理业务
        try {
            MissionGroupVo missionGroupVo = missionGroupService.queryById(missionGroupId);
            if (null == missionGroupVo) {
                return new ArrayList<>();
            }
            LambdaQueryWrapper<MissionUserRecord> lqw = Wrappers.lambdaQuery();
            lqw.eq(MissionUserRecord::getMissionGroupId, missionGroupId);
            lqw.eq(MissionUserRecord::getStatus, "1");
            lqw.ne(MissionUserRecord::getDrawType, "9");
            lqw.last("order by draw_time desc limit 50");
            recordVos = baseMapper.selectVoList(lqw);
            // 先删除，防止重复
            RedisUtils.deleteObject(key);
            RedisUtils.setCacheList(key, recordVos);
            RedisUtils.expire(key, Duration.ofMinutes(20));
            return recordVos;
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 查询奖品列表
     *
     * @param missionGroupId 任务组ID
     * @return
     */
    @Override
    public List<String> getRecordStringList(Long missionGroupId) {
        String key = CacheNames.recordStringList + ":" + missionGroupId;
        List<String> recordVosString = RedisUtils.getCacheList(key);
        if (ObjectUtil.isNotEmpty(recordVosString)) {
            return recordVosString;
        }
        final LockInfo lockInfo = lockTemplate.lock("lock" + key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return new ArrayList<>();
        }
        // 获取锁成功，处理业务
        try {
            MissionGroupVo missionGroupVo = missionGroupService.queryById(missionGroupId);
            if (null == missionGroupVo) {
                return new ArrayList<>();
            }
            LambdaQueryWrapper<MissionUserRecord> lqw = Wrappers.lambdaQuery();
            lqw.eq(MissionUserRecord::getMissionGroupId, missionGroupId);
            lqw.eq(MissionUserRecord::getStatus, "1");
            lqw.ne(MissionUserRecord::getDrawType, "9");
            lqw.last("order by draw_time desc limit 50");
            List<MissionUserRecordVo> recordVos = baseMapper.selectVoList(lqw);
            for (MissionUserRecordVo userDrawVo : recordVos) {
                if (StringUtils.isBlank(userDrawVo.getSendAccount())) {
                    continue;
                }
                String str = DesensitizedUtil.mobilePhone(userDrawVo.getSendAccount()) + "获得" + userDrawVo.getDrawName();
                recordVosString.add(str);
                // 暂时提示真实内容
            }
            RedisUtils.setCacheList(key, recordVosString);
            RedisUtils.expire(key, Duration.ofMinutes(20));

            return recordVosString;
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 查询用户奖品列表
     *
     * @param missionGroupId 任务组ID
     * @param pageQuery      分页信息
     * @return
     */
    @Override
    public TableDataInfo<MissionUserRecordVo> getUserRecordPageList(Long missionGroupId, PageQuery pageQuery) {
        MissionGroupVo missionGroupVo = missionGroupService.queryById(missionGroupId);
        if (null == missionGroupVo) {
            return TableDataInfo.build(new ArrayList<>());
        }
        UserVo userVo = userService.queryById(LoginHelper.getUserId(), ZlyyhUtils.getPlatformChannel());
        if (null == userVo) {
            return TableDataInfo.build(new ArrayList<>());
        }
        LambdaQueryWrapper<MissionUserRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(MissionUserRecord::getMissionUserId, userVo.getUserId());
        lqw.eq(MissionUserRecord::getMissionGroupId, missionGroupId);
        lqw.eq(MissionUserRecord::getStatus, "1");
        Page<MissionUserRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 抽奖
     *
     * @param missionGroupId 任务组ID
     * @param userId         用户ID
     * @param platformKey    平台标识
     * @return 剩余抽奖次数
     */
    @Transactional
    @Override
    public MissionUserRecord getDraw(Long missionGroupId, Long userId, Long platformKey, String channel) {
        TimeInterval timer = DateUtil.timer();
        UserVo userVo = userService.queryById(userId, channel);
        if (null == userVo || "0".equals(userVo.getReloadUser()) || StringUtils.isBlank(userVo.getMobile())) {
            throw new ServiceException("登录超时，请退出重试[user]", HttpStatus.HTTP_UNAUTHORIZED);
        }
        MissionGroupVo missionGroupVo = missionGroupService.queryById(missionGroupId);
        if (null == missionGroupVo) {
            throw new ServiceException("未找到活动信息");
        }
        if (!"0".equals(missionGroupVo.getStatus())) {
            throw new ServiceException("活动已结束");
        }
        if (null != missionGroupVo.getEndDate() && DateUtils.compare(missionGroupVo.getEndDate()) < 0) {
            throw new ServiceException("活动已结束");
        }
        ZlyyhUtils.checkCity(missionGroupVo.getShowCity());
        // 上锁
        String lockKey = "lockKey:" + userId;
        final LockInfo lockInfo = lockTemplate.lock(lockKey, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            throw new ServiceException("不能频繁操作，请稍后重试！");
        }
        // 获取锁成功，处理业务
        try {
            MissionUserRecord missionUserRecord = this.getMissionUserRecord(userVo.getUserId(), missionGroupId, missionGroupVo.getPlatformKey());
            if (null == missionUserRecord) {
                throw new ServiceException("暂无抽奖机会~");
            }
            MissionUserRecordEvent missionUserRecordEvent = new MissionUserRecordEvent();
            List<DrawVo> drawVos = this.getDrawListByCache(userVo.getUserId(), missionGroupId, platformKey, missionUserRecordEvent);
            if (ObjectUtil.isEmpty(drawVos)) {
                throw new ServiceException("奖品已发完");
            }
            List<Double> drawProbability = drawVos.stream().map(item -> item.getDrawProbability().doubleValue()).collect(Collectors.toList());
            // 抽奖
            AliasMethod method = new AliasMethod(drawProbability);
            int index = method.next();
            // 奖品
            DrawVo drawVo = drawVos.get(index);
            // 用户奖品信息
            missionUserRecord.setStatus("1");
            missionUserRecord.setDrawId(drawVo.getDrawId());
            missionUserRecord.setDrawType(drawVo.getDrawType());
            missionUserRecord.setDrawName(drawVo.getDrawName());
            missionUserRecord.setDrawImg(drawVo.getDrawImg());
            missionUserRecord.setSendAccount(userVo.getMobile());
            missionUserRecord.setDrawTime(new Date());
            missionUserRecord.setDrawNo(drawVo.getDrawNo());
            missionUserRecord.setSendValue(drawVo.getSendValue());
            missionUserRecord.setDrawQuota(drawVo.getDrawQuota());
            missionUserRecord.setToType(drawVo.getToType());
            missionUserRecord.setAppId(drawVo.getAppId());
            missionUserRecord.setUrl(drawVo.getUrl());
            missionUserRecord.setOrderCityName(ZlyyhUtils.getCityName());
            missionUserRecord.setOrderCityCode(ZlyyhUtils.getAdCode());

            baseMapper.updateById(missionUserRecord);
            // 记录发放数量
            log.info("用户userId：{}，抽奖耗时：{}毫秒", userId, timer.interval());
            // 发布事件记录发放数量
            if (missionUserRecordEvent.isToEvent()) {
                missionUserRecordEvent.setMissionUserRecord(missionUserRecord);
                missionUserRecordEvent.setCacheTime(missionGroupVo.getEndDate());
                SpringUtils.context().publishEvent(missionUserRecordEvent);
            } else {
                saveDrawCount(missionUserRecord, missionGroupVo.getEndDate());
            }
            // 返回结果
            log.info("用户userId：{}，总耗时：{}毫秒", userId, timer.interval());
            SpringUtils.context().publishEvent(new CacheMissionRecordEvent(missionUserRecord.getMissionUserRecordId()));
            return missionUserRecord;
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 异步发奖
     *
     * @param missionUserRecordId 需要发放的抽奖记录
     */
    @Override
    public void sendDraw(Long missionUserRecordId) {
        if (null == missionUserRecordId) {
            log.error("发奖异常，待发奖记录ID为空");
            return;
        }
        String key = "sendDraw:" + missionUserRecordId;
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        // 获取锁成功，处理业务
        try {
            MissionUserRecord missionUserRecord = baseMapper.selectById(missionUserRecordId);
            if (null == missionUserRecord) {
                log.error("发奖异常，待发奖记录ID：{}，未找到对应信息", missionUserRecordId);
                return;
            }
            if (!"1".equals(missionUserRecord.getStatus())) {
                log.error("发奖异常，发奖记录状态不对，记录ID：{}", missionUserRecordId);
                return;
            }
            if (!"0".equals(missionUserRecord.getSendStatus()) && !"3".equals(missionUserRecord.getSendStatus())) {
                log.error("奖品发放中，或已发放成功：{}", missionUserRecordId);
                return;
            }
            MissionGroupVo missionGroupVo = missionGroupService.queryById(missionUserRecord.getMissionGroupId());
            if (null == missionGroupVo) {
                return;
            }
            // 新增发券记录
            MissionUserRecordLog missionUserRecordLog = new MissionUserRecordLog();
            missionUserRecordLog.setMissionUserRecordId(missionUserRecordId);
            missionUserRecordLog.setPushNumber(IdUtil.getSnowflakeNextIdStr());
            missionUserRecordLog.setExternalProductId(missionUserRecord.getDrawNo());
            missionUserRecordLog.setStatus("0");
            if ("9".equals(missionUserRecord.getDrawType())) {
                missionUserRecordLog.setStatus("1");
            }
            missionUserRecordLog.setSendValue(missionUserRecord.getSendValue());
            missionUserRecordLogMapper.insert(missionUserRecordLog);
            // 修改订单状态发券中
            missionUserRecord.setSendStatus("1");
            if ("9".equals(missionUserRecord.getDrawType())) {
                missionUserRecord.setSendStatus("2");
            }
            missionUserRecord.setSendOkTime(new Date());
            missionUserRecord.setPushNumber(missionUserRecordLog.getPushNumber());
            // 修改订单信息
            baseMapper.updateById(missionUserRecord);
            if ("9".equals(missionUserRecord.getDrawType())) {
                return;
            }
            // 再次查询，防止多次修改，多次加密问题
            missionUserRecord = baseMapper.selectById(missionUserRecord.getMissionUserRecordId());
            if (StringUtils.isBlank(missionUserRecord.getDrawNo())) {
                DrawVo drawVo = drawService.queryById(missionUserRecord.getDrawId());
                if (null == drawVo || StringUtils.isBlank(drawVo.getDrawNo())) {
                    log.error("奖品发放错误，缺少供应商产品ID：{}", missionUserRecordId);
                    sendResult(R.fail("缺少供应商产品ID"), missionUserRecord, missionUserRecordLog, false);
                    return;
                }
                missionUserRecord.setDrawNo(drawVo.getDrawNo());
            }
            if ("0".equals(missionUserRecord.getDrawType())) {
                // 0银联票券
                R<JSONObject> result = YsfUtils.sendCoupon(missionUserRecord.getMissionUserRecordId(), missionUserRecordLog.getPushNumber(), missionUserRecordLog.getExternalProductId(), missionUserRecord.getSendAccount(), 1L, "1", missionGroupVo.getPlatformKey());
                // 处理结果
                sendResult(result, missionUserRecord, missionUserRecordLog, true);
            } else if ("1".equals(missionUserRecord.getDrawType())) {
                // 1云闪付红包
                R<Void> result = YsfUtils.sendAcquire(missionUserRecord.getMissionUserRecordId(), missionUserRecordLog.getPushNumber(), missionUserRecord.getSendAccount(), "1", missionUserRecordLog.getExternalProductId(), missionUserRecordLog.getSendValue(), missionUserRecord.getDrawName(), missionUserRecord.getDrawId(), missionGroupVo.getPlatformKey());
                sendResult(result, missionUserRecord, missionUserRecordLog, false);
            } else if ("2".equals(missionUserRecord.getDrawType())) {
                // 2云闪付积点
                R<Void> result = YsfUtils.memberPointAcquire(missionUserRecord.getMissionUserRecordId(), missionUserRecordLog.getPushNumber(), missionUserRecordLog.getExternalProductId(), missionUserRecordLog.getSendValue().longValue(), "0", missionUserRecord.getDrawName(), missionUserRecord.getSendAccount(), "1", missionGroupVo.getPlatformKey());
                sendResult(result, missionUserRecord, missionUserRecordLog, false);
            } else if ("3".equals(missionUserRecord.getDrawType())) {
                R<Void> result = CloudRechargeUtils.doPostCreateOrder(missionUserRecord.getMissionUserRecordId(), missionUserRecordLog.getExternalProductId(), missionUserRecord.getSendAccount(), 1, missionUserRecordLog.getPushNumber(), "/zlyyh-mobile/missionUserRecord/ignore/orderCallback");
                sendResult(result, missionUserRecord, missionUserRecordLog, false);
            } else if ("4".equals(missionUserRecord.getDrawType())) {
                String chnlId = ysfConfigService.queryValueByKey(missionGroupVo.getPlatformKey(), YsfUpConstants.up_chnlId);
                String appId = ysfConfigService.queryValueByKey(missionGroupVo.getPlatformKey(), YsfUpConstants.up_appId);
                String sm4Key = ysfConfigService.queryValueByKey(missionGroupVo.getPlatformKey(), YsfUpConstants.up_sm4Key);
                String rsaPrivateKey = ysfConfigService.queryValueByKey(missionGroupVo.getPlatformKey(), YsfUpConstants.up_rsaPrivateKey);
                String entityTp = ysfConfigService.queryValueByKey(missionGroupVo.getPlatformKey(), YsfUpConstants.up_entityTp);
                // 银联开放平台发券
                R<JSONObject> result = YsfUtils.couponAcquire(missionUserRecordLog.getPushNumber(), missionUserRecordLog.getExternalProductId(), missionUserRecord.getSendAccount(), "1", entityTp, chnlId, appId, rsaPrivateKey, sm4Key);
                // 处理结果
                sendResult(result, missionUserRecord, missionUserRecordLog, true);
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 失败自动补发
     */
    @Override
    public void sendStatusOrder() {
        List<MissionUserRecordVo> missionUserRecordVos = baseMapper.selectVoList(new LambdaQueryWrapper<MissionUserRecord>().eq(MissionUserRecord::getStatus, "1").in(MissionUserRecord::getSendStatus, "0", "1", "3").gt(MissionUserRecord::getDrawTime, DateUtil.offsetDay(new Date(), -3).toJdkDate()).last("limit 1000"));
        if (ObjectUtil.isNotEmpty(missionUserRecordVos)) {
            for (MissionUserRecordVo missionUserRecordVo : missionUserRecordVos) {
                if ("0".equals(missionUserRecordVo.getSendStatus()) || "3".equals(missionUserRecordVo.getSendStatus())) {
                    String value = CacheUtils.get(CacheNames.reloadOrderNumbers, missionUserRecordVo.getMissionUserRecordId());
                    if (StringUtils.isBlank(value)) {
                        CacheUtils.put(CacheNames.reloadOrderNumbers, missionUserRecordVo.getMissionUserRecordId(), DateUtil.now());
                        //开始发券
                        sendDraw(missionUserRecordVo.getMissionUserRecordId());
                    }
                } else {
                    queryOrderSendStatus(missionUserRecordVo.getPushNumber());
                    MissionUserRecordVo recordVo = baseMapper.selectVoById(missionUserRecordVo.getMissionUserRecordId());
                    if (null != recordVo && "3".equals(recordVo.getSendStatus())) {
                        String value = CacheUtils.get(CacheNames.reloadOrderNumbers, missionUserRecordVo.getMissionUserRecordId());
                        if (StringUtils.isBlank(value)) {
                            CacheUtils.put(CacheNames.reloadOrderNumbers, missionUserRecordVo.getMissionUserRecordId(), DateUtil.now());
                            //开始发券
                            sendDraw(missionUserRecordVo.getMissionUserRecordId());
                        }
                    }
                }
            }
        }
    }

    private void sendResult(R result, MissionUserRecord missionUserRecord, MissionUserRecordLog missionUserRecordLog, boolean warnQueryCoupon) {
        if (R.isSuccess(result)) {
            if (missionUserRecord.getDrawType().equals("3")) {
                JSONObject data = (JSONObject) result.getData();
                rechargeResult(data, missionUserRecordLog, missionUserRecord);
            } else {
                // 发放成功
                missionUserRecordLog.setStatus("1");
                missionUserRecord.setSendStatus("2");
                if (null != result.getData()) {
                    if ("4".equals(missionUserRecord.getDrawType())) {
                        try {
                            JSONObject data = (JSONObject) result.getData();
                            missionUserRecordLog.setExternalOrderNumber(data.getString("couponCd"));
                        } catch (Exception ignored) {
                        }
                    }
                    missionUserRecordLog.setRemark(JSONObject.toJSONString(result.getData()));
                } else {
                    missionUserRecordLog.setRemark(result.getMsg());
                }
                if (StringUtils.isNotBlank(missionUserRecordLog.getRemark()) && missionUserRecordLog.getRemark().length() >= 5000) {
                    missionUserRecordLog.setRemark(missionUserRecordLog.getRemark().substring(0, 4900));
                }
            }
        } else {
            if (R.FAIL == result.getCode()) {
                // 发放失败
                missionUserRecordLog.setStatus("2");
                missionUserRecordLog.setRemark(result.getMsg());
                if (StringUtils.isNotBlank(missionUserRecordLog.getRemark()) && missionUserRecordLog.getRemark().length() >= 5000) {
                    missionUserRecordLog.setRemark(missionUserRecordLog.getRemark().substring(0, 4900));
                }

                missionUserRecord.setSendStatus("3");
                missionUserRecord.setFailReason(missionUserRecordLog.getRemark());
            } else {
                // 请求接口查询发放状态
                if (warnQueryCoupon) {
                    queryOrderSendStatus(missionUserRecordLog.getPushNumber());
                }
                return;
            }
        }
        missionUserRecordLogMapper.updateById(missionUserRecordLog);
        // 修改订单信息
        baseMapper.updateById(missionUserRecord);
    }

    public void queryOrderSendStatus(String pushNumber) {
        if (StringUtils.isBlank(pushNumber)) {
            return;
        }
        MissionUserRecordLog missionUserRecordLog = missionUserRecordLogMapper.selectOne(new LambdaQueryWrapper<MissionUserRecordLog>().eq(MissionUserRecordLog::getPushNumber, pushNumber));
        if (null == missionUserRecordLog) {
            log.error("发券订单号{}，不存在活动发券订单", pushNumber);
            return;
        }
        MissionUserRecord missionUserRecord = baseMapper.selectById(missionUserRecordLog.getMissionUserRecordId());
        if (null == missionUserRecord) {
            log.error("发券订单号{}，不存在活动订单", pushNumber);
            return;
        }
        if (!"0".equals(missionUserRecordLog.getStatus())) {
            log.error("发券订单号{}，订单已有最终状态，不做查询处理", pushNumber);
            return;
        }
        MissionVo missionVo = missionService.queryById(missionUserRecord.getMissionId());
        if (null == missionVo) {
            return;
        }
        if ("3".equals(missionUserRecord.getDrawType())) {
            R<JSONObject> result = CloudRechargeUtils.doPostQueryOrder(missionUserRecordLog.getMissionUserRecordId(), missionUserRecordLog.getPushNumber());
            sendResult(result, missionUserRecord, missionUserRecordLog, false);
        } else if ("0".equals(missionUserRecord.getDrawType())) {
            R<JSONObject> result = YsfUtils.queryCoupon(missionUserRecordLog.getMissionUserRecordId(), missionUserRecordLog.getPushNumber(), missionUserRecordLog.getCreateTime(), missionVo.getPlatformKey());
            // 处理结果
            sendResult(result, missionUserRecord, missionUserRecordLog, false);
        } else if ("4".equals(missionUserRecord.getDrawType())) {
            String chnlId = ysfConfigService.queryValueByKey(null, YsfUpConstants.up_chnlId);
            String appId = ysfConfigService.queryValueByKey(null, YsfUpConstants.up_appId);
            String rsaPrivateKey = ysfConfigService.queryValueByKey(null, YsfUpConstants.up_rsaPrivateKey);
            R<JSONObject> result = YsfUtils.couponAcqQuery(missionUserRecordLog.getPushNumber(), DateUtil.format(missionUserRecordLog.getCreateTime(), DatePattern.PURE_DATE_PATTERN), chnlId, appId, rsaPrivateKey);
            // 处理结果
            sendResult(result, missionUserRecord, missionUserRecordLog, false);
        }
    }

    /**
     * 查询用户剩余抽奖次数
     *
     * @param missionGroupId 任务组ID
     * @param userId         用户ID
     * @return 剩余抽奖次数
     */
    @Override
    public Long getUserDrawCount(Long missionGroupId, Long userId) {
        this.queryMissionAndToUserDraw(userId, missionGroupId);
        return getDrawCount(userId, missionGroupId);
    }

    @Override
    public void cloudRechargeCallback(CloudRechargeEntity huiguyunEntity) {
        CloudRechargeUtils.getData(huiguyunEntity);
        JSONObject data = JSONObject.parseObject(huiguyunEntity.getEncryptedData());
        // 查询订单是否存在
        MissionUserRecordLog missionUserRecordLog = missionUserRecordLogMapper.selectOne(new LambdaQueryWrapper<MissionUserRecordLog>().eq(MissionUserRecordLog::getPushNumber, data.getString("externalOrderNumber")));
        if (null == missionUserRecordLog) {
            throw new ServiceException("订单不存在");
        }
        MissionUserRecord missionUserRecord = baseMapper.selectById(missionUserRecordLog.getMissionUserRecordId());
        if (null == missionUserRecord) {
            throw new ServiceException("订单不存在");
        }
        rechargeResult(data, missionUserRecordLog, missionUserRecord);
        missionUserRecordLogMapper.updateById(missionUserRecordLog);
        // 修改订单信息
        baseMapper.updateById(missionUserRecord);
    }

    /**
     * 充值中心订单结果处理
     *
     * @param data                 订单结果
     * @param missionUserRecordLog 订单请求信息
     * @param missionUserRecord    订单信息
     */
    private void rechargeResult(JSONObject data, MissionUserRecordLog missionUserRecordLog, MissionUserRecord missionUserRecord) {
        String state = data.getString("state");
        missionUserRecordLog.setExternalOrderNumber(data.getString("number"));
        if ("4".equals(state)) {
            // 发放成功
            missionUserRecordLog.setStatus("1");
            missionUserRecord.setSendStatus("2");
        } else if ("5".equals(state)) {
            // 发放失败
            missionUserRecordLog.setStatus("2");
            missionUserRecordLog.setRemark(data.getString("remark"));
            if (StringUtils.isNotBlank(missionUserRecordLog.getRemark()) && missionUserRecordLog.getRemark().length() >= 5000) {
                missionUserRecordLog.setRemark(missionUserRecordLog.getRemark().substring(0, 4900));
            }
            missionUserRecord.setSendStatus("3");
            missionUserRecord.setFailReason(missionUserRecordLog.getRemark());
        }
    }

    /**
     * 查询任务完成进度
     */
    @Override
    public void queryMission() {
        Set<Long> missionGroupIds = missionService.queryMissionGroupIds();
        for (Long missionGroupId : missionGroupIds) {
            int pageNum = 1;
            while (true) {
                // 查询任务用户
                PageQuery pageQuery = new PageQuery();
                pageQuery.setPageNum(pageNum);
                pageQuery.setPageSize(500);
                TableDataInfo<MissionUserVo> missionUserVoTableDataInfo = iMissionUserService.queryPageList(missionGroupId, pageQuery);
                for (MissionUserVo row : missionUserVoTableDataInfo.getRows()) {
                    this.queryMissionAndToUserDraw(row.getUserId(), row.getMissionGroupId());
                }
                int count = pageQuery.getPageNum() * pageQuery.getPageSize();
                if (count >= missionUserVoTableDataInfo.getTotal()) {
                    break;
                }
                pageNum++;
            }
        }
    }

    /**
     * 查询用户购买次数
     *
     * @param missionGroupId 任务组ID
     * @param userId         用户ID
     * @return 结果
     */
    @Override
    public UserProductCount getUserProductPayCount(Long missionGroupId, Long missionId, Long userId, String cityCode) {
        MissionVo missionVo = null;
        if (null != missionId) {
            missionVo = missionService.queryById(missionId);
        }
        MissionGroupVo missionGroupVo = missionGroupService.queryById(missionGroupId);
        if (null == missionGroupVo) {
            if (null == missionVo) {
                return new UserProductCount();
            } else {
                missionGroupVo = missionGroupService.queryById(missionVo.getMissionGroupId());
            }
            if (null == missionGroupVo) {
                return new UserProductCount();
            }
        }
        List<ProductVo> productVos;
        if (null != missionVo) {
            productVos = missionGroupService.missionProduct(missionId, missionGroupVo.getPlatformKey(), cityCode);
//            if (ObjectUtil.isEmpty(productVos)) {
//                productVos = missionGroupService.missionProduct(missionGroupId, missionGroupVo.getPlatformKey(), cityCode);
//            }
        } else {
            productVos = missionGroupService.missionProduct(missionGroupId, missionGroupVo.getPlatformKey(), cityCode);
        }
        if (ObjectUtil.isEmpty(productVos)) {
            return new UserProductCount();
        }
        Long id = null == missionVo ? missionGroupId : missionId;
        String redisKey = "UserProductCount:" + id + ":" + userId + ":" + DateUtil.today();
        UserProductCount userProductCount = RedisUtils.getCacheObject(redisKey);
        if (null != userProductCount && !userProductCount.isDayPay()) {
            return userProductCount;
        }
        if (productVos.size() > 1) {
            // 查询用户参与次数
            OrderVo lastOrder = orderService.getLastOrder(productVos.stream().map(ProductVo::getProductId).collect(Collectors.toList()), userId);
            if (null == lastOrder) {
                userProductCount = new UserProductCount();
                userProductCount.setProductId(productVos.get(0).getProductId());
                return userProductCount;
            }
            int i = 0;
            for (int j = 0; j < productVos.size(); j++) {
                if (productVos.get(j).getProductId().equals(lastOrder.getProductId())) {
                    i = j;
                    break;
                }
            }
            i++;
            userProductCount = new UserProductCount();
            if (DateUtils.compare(lastOrder.getCreateTime(), DateUtil.beginOfDay(new Date())) >= 0) {
                userProductCount.setDayPay(false);
                userProductCount.setPayCount(i);
                if (i >= productVos.size()) {
                    userProductCount.setProductId(productVos.get(0).getProductId());
                } else {
                    userProductCount.setProductId(productVos.get(i).getProductId());
                }
                RedisUtils.setCacheObject(redisKey, userProductCount, Duration.ofDays(2));
            } else if (DateUtils.compare(lastOrder.getCreateTime(), DateUtil.beginOfDay(DateUtil.yesterday())) >= 0 && DateUtils.compare(lastOrder.getCreateTime(), DateUtil.beginOfDay(new Date())) < 0) {
                if (i >= productVos.size()) {
                    userProductCount.setPayCount(0);
                    userProductCount.setProductId(productVos.get(0).getProductId());
                } else {
                    userProductCount.setPayCount(i);
                    userProductCount.setProductId(productVos.get(i).getProductId());
                }
            } else {
                userProductCount.setProductId(productVos.get(0).getProductId());
            }
            return userProductCount;
        } else {
            ProductVo productVo = productVos.get(0);
            // 查询用户参与次数
            Long dayOrderCount = orderService.getDayOrderCount(productVo.getProductId(), userId);
            userProductCount = new UserProductCount();
            userProductCount.setProductId(productVo.getProductId());
            userProductCount.setPayCount(dayOrderCount.intValue());
            if (dayOrderCount >= productVo.getDayUserCount()) {
                userProductCount.setDayPay(false);
            }
            return userProductCount;
        }
    }

    /**
     * 购买商品
     */
    @Override
    public CreateOrderResult payMissionGroupProduct(Long missionId, Long userId, Long platformId, String channel, String cityName, String adCode) {
        MissionVo missionVo = missionService.queryById(missionId);
        if (null == missionVo) {
            log.error("任务不存在：{}", missionId);
            throw new ServiceException("任务不存在");
        }
        checkMissionStatus(missionVo.getStatus(), missionVo.getStartDate(), missionVo.getEndDate());
        MissionGroupVo missionGroupVo = missionGroupService.queryById(missionVo.getMissionGroupId());
        if (null == missionGroupVo) {
            log.error("任务不存在：{}", missionId);
            throw new ServiceException("任务不存在");
        }
        checkMissionStatus(missionGroupVo.getStatus(), missionGroupVo.getStartDate(), missionGroupVo.getEndDate());
        UserProductCount userProductPayCount = this.getUserProductPayCount(missionVo.getMissionGroupId(), missionId, userId, ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        if (null == userProductPayCount || null == userProductPayCount.getProductId()) {
            log.error("任务配置错误：{}", missionId);
            throw new ServiceException("任务配置错误");
        }
        if (!userProductPayCount.isDayPay()) {
            log.error("用户：{}，今日已达参与上限：{}", userId, missionId);
            throw new ServiceException("今日已达参与上限");
        }
        if ("1".equals(missionVo.getMissionAffiliation())) {
            // 查询是否是62会员 先查缓存
            MemberVipBalanceVo user62VipInfo = userService.getUser62VipInfo(true, userId);
            if (null == user62VipInfo) {
                throw new ServiceException("会员信息查询失败，请稍后重试");
            }
            if (!"01".equals(user62VipInfo.getStatus()) && !"03".equals(user62VipInfo.getStatus())) {
                // 不是会员，再次查询，防止用户开通之后，我方缓存未更新问题
                user62VipInfo = userService.getUser62VipInfo(false, userId);
                if (!"01".equals(user62VipInfo.getStatus()) && !"03".equals(user62VipInfo.getStatus())) {
                    throw new ServiceException("请先开通62会员");
                }
            }
        }
        asyncSecondService.sendInviteDraw(userId, platformId, missionVo.getMissionGroupId(), channel, cityName, adCode);
        CreateOrderBo createOrderBo = new CreateOrderBo();
        createOrderBo.setProductId(userProductPayCount.getProductId());
        createOrderBo.setUserId(userId);
        createOrderBo.setAdcode(adCode);
        createOrderBo.setCityName(cityName);
        createOrderBo.setPlatformKey(platformId);
        createOrderBo.setChannel(channel);
        return orderService.createOrder(createOrderBo, true);
    }

    private void checkMissionStatus(String status, Date startDate, Date endDate) {
        if ("1".equals(status)) {
            throw new ServiceException("任务已结束");
        }
        if (null != startDate && DateUtils.compare(startDate) > 0) {
            throw new ServiceException("开始时间:" + DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", startDate));
        }
        if (null != endDate && DateUtils.compare(endDate) < 0) {
            throw new ServiceException("任务已结束");
        }
    }

    /**
     * 请求接口赠送奖励
     *
     * @param userId 用户信息
     */
    private void queryMissionAndToUserDraw(Long userId, Long missionGroupId) {
        if (null == userId) {
            return;
        }
        MissionGroupVo missionGroupVo = missionGroupService.queryById(missionGroupId);
        if (null == missionGroupVo || !"0".equals(missionGroupVo.getStatus()) || StringUtils.isBlank(missionGroupVo.getMissionGroupUpid())) {
            return;
        }
        if (null != missionGroupVo.getEndDate() && DateUtils.compare(missionGroupVo.getEndDate()) < 0) {
            return;
        }
        UserVo userVo = userService.queryById(userId, PlatformEnumd.MP_YSF.getChannel());
        if (null == userVo || StringUtils.isBlank(userVo.getOpenId())) {
            return;
        }
        PlatformVo platformVo = platformService.queryById(missionGroupVo.getPlatformKey(), PlatformEnumd.MP_YSF.getChannel());
        if (null == platformVo) {
            return;
        }
        MissionBo missionBo = new MissionBo();
        missionBo.setPlatformKey(missionGroupVo.getPlatformKey());
        missionBo.setMissionGroupId(missionGroupVo.getMissionGroupId());
        List<MissionVo> missionVos = missionService.queryList(missionBo);
        for (MissionVo missionVo : missionVos) {
            if (StringUtils.isBlank(missionVo.getMissionUpid())) {
                continue;
            }
            // 请求接口查询用户完成任务进度
            R<Long> result = YsfUtils.queryMission(missionVo.getMissionUpid(), userVo.getOpenId(), platformVo.getAppId(), platformVo.getSecret(), platformVo.getPlatformKey());
            if (R.isError(result)) {
                continue;
            }
            if (null == result.getData() || result.getData() <= 0) {
                continue;
            }
            // 赠送奖励
//            missionUserDrawService.sendRecord(missionVo, userVo.getUserId(), result.getData());
        }
    }

    private Long getDrawCount(Long missionUserId, Long missionGroupId) {
        return baseMapper.selectCount(buildUserDrawCountQuery(missionUserId, missionGroupId));
    }

    private MissionUserRecord getMissionUserRecord(Long missionUserId, Long missionGroupId, Long platformKey) {
        LambdaQueryWrapper<MissionUserRecord> lqw = buildUserDrawCountQuery(missionUserId, missionGroupId);
        List<Long> missionIds = this.getMissionIds(missionGroupId, platformKey, missionUserId);
        if (ObjectUtil.isEmpty(missionIds)) {
            throw new ServiceException("抱歉，奖品已发完或已达规则上限");
        }
//        lqw.in(MissionUserRecord::getMissionId, missionIds);
        lqw.last("order by mission_user_record_id asc limit 1");
        return baseMapper.selectOne(lqw);
    }

    private List<Long> getMissionIds(Long missionGroupId, Long platformKey, Long missionUserId) {
        MissionBo missionBo = new MissionBo();
        missionBo.setPlatformKey(platformKey);
        missionBo.setMissionGroupId(missionGroupId);
        List<MissionVo> missionVos = missionService.queryList(missionBo);
        List<Long> missionIds = new ArrayList<>(missionVos.size());
        for (MissionVo missionVo : missionVos) {
            if (missionVo.getMissionTotalQuota().signum() > 0) {
                // 奖励总额度
                double missionQuotaCount = DrawRedisCacheUtils.getMissionQuotaCount(missionVo.getMissionId(), DateType.TOTAL);
                if (missionQuotaCount >= missionVo.getMissionTotalQuota().doubleValue()) {
                    // 剔除
                    log.info("任务ID：{}，任务：{}，已达总额度上限，剔除该任务抽奖机会", missionVo.getMissionId(), missionVo.getMissionName());
                    continue;
                }
            }
            DateType dateType = null;
            // 奖励额度
            if ("1".equals(missionVo.getPeriodType())) {
                dateType = DateType.DAY;
            } else if ("2".equals(missionVo.getPeriodType())) {
                dateType = DateType.WEEK;
            } else if ("3".equals(missionVo.getPeriodType())) {
                dateType = DateType.MONTH;
            }
            if (missionVo.getMissionPeriodQuota().signum() > 0 && null != dateType) {
                double missionQuotaCount = DrawRedisCacheUtils.getMissionQuotaCount(missionVo.getMissionId(), dateType);
                if (missionQuotaCount >= missionVo.getMissionPeriodQuota().doubleValue()) {
                    // 剔除
                    log.info("任务ID：{}，任务：{}，已达周期额度上限，剔除该任务抽奖机会", missionVo.getMissionId(), missionVo.getMissionName());
                    continue;
                }
            }
            if (missionVo.getUserTotalQuota().signum() > 0) {
                // 奖励总额度
                double missionQuotaCount = DrawRedisCacheUtils.getUserMissionQuotaCount(missionVo.getMissionId(), missionUserId, DateType.TOTAL);
                if (missionQuotaCount >= missionVo.getUserTotalQuota().doubleValue()) {
                    // 剔除
                    log.info("任务ID：{}，任务：{}，任务用户ID：{},用户已达总额度上限，剔除该任务抽奖机会", missionVo.getMissionId(), missionVo.getMissionName(), missionUserId);
                    continue;
                }
            }
            if (missionVo.getUserPeriodQuota().signum() > 0 && null != dateType) {
                double missionQuotaCount = DrawRedisCacheUtils.getUserMissionQuotaCount(missionVo.getMissionId(), missionUserId, dateType);
                if (missionQuotaCount >= missionVo.getUserPeriodQuota().doubleValue()) {
                    // 剔除
                    log.info("任务ID：{}，任务：{}，任务用户ID：{},用户已达周期额度上限，剔除该任务抽奖机会", missionVo.getMissionId(), missionVo.getMissionName(), missionUserId);
                    continue;
                }
            }
            missionIds.add(missionVo.getMissionId());
        }
        return missionIds;
    }

    private LambdaQueryWrapper<MissionUserRecord> buildUserDrawCountQuery(Long missionUserId, Long missionGroupId) {
        LambdaQueryWrapper<MissionUserRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(MissionUserRecord::getMissionUserId, missionUserId);
        lqw.eq(MissionUserRecord::getMissionGroupId, missionGroupId);
        lqw.eq(MissionUserRecord::getStatus, "0");
        lqw.and(lm -> {
            lm.isNull(MissionUserRecord::getExpiryTime).or().gt(MissionUserRecord::getExpiryTime, new Date());
        });
        return lqw;
    }

    /**
     * 获取当天缓存奖品列表
     *
     * @return 奖品列表
     */
    private List<DrawVo> getDrawListByCache(Long missionUserId, Long missionGroupId, Long platformKey, MissionUserRecordEvent missionUserRecordEvent) {
        List<DrawVo> drawVos = DrawRedisCacheUtils.getDrawList(platformKey, missionGroupId);
        if (ObjectUtil.isEmpty(drawVos)) {
            DrawBo bo = new DrawBo();
            bo.setMissionGroupId(missionGroupId);
            bo.setPlatformKey(platformKey);
            bo.setDrawWinning("0");
            drawVos = drawService.queryList(bo);
            if (ObjectUtil.isEmpty(drawVos)) {
                return new ArrayList<>();
            }
            // 缓存
            DrawRedisCacheUtils.setDrawList(platformKey, missionGroupId, drawVos);
        }
        List<DrawVo> newDrawVos = new ArrayList<>();
        // 筛除超限奖品
        boolean updateCache = false;
        Iterator<DrawVo> iterator = drawVos.iterator();
        while (iterator.hasNext()) {
            DrawVo drawVo = iterator.next();
            // 开始时间 结束时间校验
            if (null != drawVo.getSellStartDate() && DateUtils.compare(drawVo.getSellStartDate()) > 0) {
                continue;
            }
            if (null != drawVo.getSellEndDate() && DateUtils.compare(drawVo.getSellEndDate()) < 0) {
                updateCache = true;
                iterator.remove();
                continue;
            }
            if (drawVo.getDayCount() > 0) {
                missionUserRecordEvent.setToEvent(false);
                long drawCount = DrawRedisCacheUtils.getDrawCount(drawVo.getDrawId(), DateType.DAY);
                if (drawCount >= drawVo.getDayCount()) {
                    updateCache = true;
                    iterator.remove();
                    log.info("奖品Id：{}，名称：{}，今日已发完，从抽奖列表中剔除", drawVo.getDrawId(), drawVo.getDrawName());
                    continue;
                }
            }
            if (drawVo.getWeekCount() > 0) {
                missionUserRecordEvent.setToEvent(false);
                long drawCount = DrawRedisCacheUtils.getDrawCount(drawVo.getDrawId(), DateType.WEEK);
                if (drawCount >= drawVo.getWeekCount()) {
                    updateCache = true;
                    iterator.remove();
                    log.info("奖品Id：{}，名称：{}，本周已发完，从抽奖列表中剔除", drawVo.getDrawId(), drawVo.getDrawName());
                    continue;
                }
            }
            if (drawVo.getMonthCount() > 0) {
                missionUserRecordEvent.setToEvent(false);
                long drawCount = DrawRedisCacheUtils.getDrawCount(drawVo.getDrawId(), DateType.MONTH);
                if (drawCount >= drawVo.getMonthCount()) {
                    updateCache = true;
                    iterator.remove();
                    log.info("奖品Id：{}，名称：{}，本月已发完，从抽奖列表中剔除", drawVo.getDrawId(), drawVo.getDrawName());
                    continue;
                }
            }
            if (drawVo.getTotalCount() > 0) {
                missionUserRecordEvent.setToEvent(false);
                long drawCount = DrawRedisCacheUtils.getDrawCount(drawVo.getDrawId(), DateType.TOTAL);
                if (drawCount >= drawVo.getTotalCount()) {
                    updateCache = true;
                    iterator.remove();
                    log.info("奖品Id：{}，名称：{}，已发完，从抽奖列表中剔除", drawVo.getDrawId(), drawVo.getDrawName());
                    continue;
                }
            }
            // 去除单用户可中次数达标的奖项
            if (drawVo.getDayUserCount() > 0) {
                missionUserRecordEvent.setToEvent(false);
                long drawCount = DrawRedisCacheUtils.getUserDrawCount(drawVo.getDrawId(), missionUserId, DateType.DAY);
                if (drawCount >= drawVo.getDayUserCount()) {
                    log.info("奖品Id：{}，名称：{}，用户：{}，今日已达上限，从抽奖列表中剔除", drawVo.getDrawId(), drawVo.getDrawName(), missionUserId);
                    continue;
                }
            }
            if (drawVo.getWeekUserCount() > 0) {
                missionUserRecordEvent.setToEvent(false);
                long drawCount = DrawRedisCacheUtils.getUserDrawCount(drawVo.getDrawId(), missionUserId, DateType.WEEK);
                if (drawCount >= drawVo.getWeekUserCount()) {
                    log.info("奖品Id：{}，名称：{}，用户：{}，本周已达上限，从抽奖列表中剔除", drawVo.getDrawId(), drawVo.getDrawName(), missionUserId);
                    continue;
                }
            }
            if (drawVo.getMonthUserCount() > 0) {
                missionUserRecordEvent.setToEvent(false);
                long drawCount = DrawRedisCacheUtils.getUserDrawCount(drawVo.getDrawId(), missionUserId, DateType.MONTH);
                if (drawCount >= drawVo.getMonthUserCount()) {
                    log.info("奖品Id：{}，名称：{}，用户：{}，本月已达上限，从抽奖列表中剔除", drawVo.getDrawId(), drawVo.getDrawName(), missionUserId);
                    continue;
                }
            }
            if (drawVo.getTotalUserCount() > 0) {
                missionUserRecordEvent.setToEvent(false);
                long drawCount = DrawRedisCacheUtils.getUserDrawCount(drawVo.getDrawId(), missionUserId, DateType.TOTAL);
                if (drawCount >= drawVo.getTotalUserCount()) {
                    log.info("奖品Id：{}，名称：{}，用户：{}，已达上限，从抽奖列表中剔除", drawVo.getDrawId(), drawVo.getDrawName(), missionUserId);
                    continue;
                }
            }
            newDrawVos.add(drawVo);
        }
        if (updateCache) {
            // 缓存
            DrawRedisCacheUtils.setDrawList(platformKey, missionGroupId, drawVos);
        }
        return newDrawVos;
    }

    /**
     * 缓存已发次数
     *
     * @param missionUserRecord 抽奖记录
     */
    @Override
    public void saveDrawCount(MissionUserRecord missionUserRecord, Date cacheTime) {
        Duration duration = null;
        if (null != cacheTime) {
            long datePoorHour = DateUtils.getDatePoorDay(cacheTime, new Date());
            if (datePoorHour > 0) {
                duration = Duration.ofDays(datePoorHour + 30);
            }
        }
        DateType[] values = DateType.values();
        for (DateType value : values) {
            // 奖励发放次数
            this.changeDrawCount(missionUserRecord.getDrawId(), value, duration);
            DrawRedisCacheUtils.addDrawCount(missionUserRecord.getDrawId(), value);
            this.changeUserDrawCount(missionUserRecord.getDrawId(), missionUserRecord.getMissionUserId(), value, duration);
            DrawRedisCacheUtils.addUserDrawCount(missionUserRecord.getDrawId(), missionUserRecord.getMissionUserId(), value);
            // 刷新奖励发放额度
            this.changeMissionQuota(missionUserRecord.getMissionId(), value, duration);
            // 奖励发放额度
            DrawRedisCacheUtils.addMissionTotalQuota(missionUserRecord.getMissionId(), missionUserRecord.getDrawQuota(), value);
            // 刷新用户奖励发放额度
            this.changeUserMissionQuota(missionUserRecord.getMissionId(), missionUserRecord.getMissionUserId(), value, duration);
            // 用户奖励发放额度
            DrawRedisCacheUtils.addUserMissionTotalQuota(missionUserRecord.getMissionId(), missionUserRecord.getMissionUserId(), missionUserRecord.getDrawQuota(), value);
        }
    }

    @Override
    public Map<String, Double> getUserQuota(Long missionId) {
        Map<String, Double> result = new HashMap<>();
        result.put("userQuota", 0d);
        result.put("redPackageBalance", 0d);
        result.put("convertRedPackage", 0d);
        MissionVo missionVo = missionService.queryById(missionId);
        if (null == missionVo) {
            return result;
        }
        MissionGroupVo missionGroupVo = missionGroupService.queryById(missionVo.getMissionGroupId());
        if (null == missionGroupVo) {
            return result;
        }
        result.put("userQuota", getUserQuotaByGroupId(missionVo.getMissionGroupId()));
        List<Long> productIds = new ArrayList<>();
        List<MissionGroupProductVo> missionGroupProductVos = missionGroupProductMapper.selectVoList(new LambdaQueryWrapper<MissionGroupProduct>().eq(MissionGroupProduct::getMissionGroupId, missionVo.getMissionGroupId()));
        if (ObjectUtil.isNotEmpty(missionGroupProductVos)) {
            productIds = missionGroupProductVos.stream().map(MissionGroupProductVo::getProductId).collect(Collectors.toList());
        }
        result.put("convertRedPackage", orderService.sumSendValueByProductIds(productIds, LoginHelper.getUserId()).doubleValue());
        if (null != missionGroupVo.getConvertValue()) {
            result.put("redPackageBalance", missionGroupVo.getConvertValue().doubleValue() - result.get("convertRedPackage"));
        }
        return result;
    }

    private double getUserQuotaByGroupId(Long missionGroupId) {
        MissionGroupVo missionGroupVo = missionGroupService.queryById(missionGroupId);
        if (null == missionGroupVo) {
            return 0;
        }
        Long userId = LoginHelper.getUserId();
        MissionUserVo missionUserVo = iMissionUserService.queryByUserIdAndGroupId(missionGroupId, userId, missionGroupVo.getPlatformKey());
        if (null == missionUserVo) {
            return 0;
        }
        MissionBo missionBo = new MissionBo();
        missionBo.setPlatformKey(missionGroupVo.getPlatformKey());
        missionBo.setMissionGroupId(missionGroupVo.getMissionGroupId());
        List<MissionVo> missionVos = missionService.queryList(missionBo);
        if (ObjectUtil.isEmpty(missionVos)) {
            return 0;
        }
        double userMissionQuotaCount = 0;
        for (MissionVo missionVo : missionVos) {
            // 刷新用户奖励发放额度
            this.changeUserMissionQuota(missionVo.getMissionId(), missionUserVo.getMissionUserId(), DateType.TOTAL, null);
            userMissionQuotaCount += DrawRedisCacheUtils.getUserMissionQuotaCount(missionVo.getMissionId(), missionUserVo.getMissionUserId(), DateType.TOTAL);
        }
        // 减去已兑换积点数
        double userMissionConvertCount = 0;
        try {
            List<Long> productIds = new ArrayList<>();
            List<MissionGroupProductVo> missionGroupProductVos = missionGroupProductMapper.selectVoList(new LambdaQueryWrapper<MissionGroupProduct>().eq(MissionGroupProduct::getMissionGroupId, missionGroupId));
            if (ObjectUtil.isNotEmpty(missionGroupProductVos)) {
                productIds = missionGroupProductVos.stream().map(MissionGroupProductVo::getProductId).collect(Collectors.toList());
            }
            userMissionConvertCount = orderService.sumOutAmountByProductIds(productIds, userId).doubleValue();
        } catch (Exception e) {
            log.error("查询用户产品发放额度异常：", e);
        }
        double result = userMissionQuotaCount - userMissionConvertCount;
        return result > 0 ? result : 0;
    }

    /**
     * 兑换商品
     *
     * @param bo 商品ID + 任务组ID
     */
    @Override
    public CreateOrderResult convertProduct(MissionGroupProductBo bo) {
        MissionGroupVo missionGroupVo = missionGroupService.queryById(bo.getMissionGroupId());
        if (null == missionGroupVo) {
            throw new ServiceException("活动不存在");
        }
        LambdaQueryWrapper<MissionGroupProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getMissionGroupId() != null, MissionGroupProduct::getMissionGroupId, bo.getMissionGroupId());
        lqw.eq(bo.getProductId() != null, MissionGroupProduct::getProductId, bo.getProductId());
        lqw.last("limit 1");
        MissionGroupProductVo missionGroupProductVo = missionGroupProductMapper.selectVoOne(lqw);
        if (null == missionGroupProductVo) {
            throw new ServiceException("兑换商品不存在");
        }
        double userQuota = getUserQuotaByGroupId(missionGroupVo.getMissionGroupId());
        if (userQuota <= 0) {
            throw new ServiceException("活动获得积点剩余数量不足");
        }
        ProductVo productVo = productService.queryById(bo.getProductId());
        if (null == productVo) {
            throw new ServiceException("兑换商品不存在");
        }
        if (userQuota < productVo.getSellAmount().doubleValue()) {
            throw new ServiceException("活动获得积点剩余数量不足");
        }
        CreateOrderBo createOrderBo = new CreateOrderBo();
        createOrderBo.setProductId(productVo.getProductId());
        createOrderBo.setUserId(LoginHelper.getUserId());
        createOrderBo.setAdcode(ZlyyhUtils.getAdCode());
        createOrderBo.setCityName(ZlyyhUtils.getCityName());
        createOrderBo.setPlatformKey(ZlyyhUtils.getPlatformId());
        createOrderBo.setChannel(ZlyyhUtils.getPlatformChannel());
        return orderService.createOrder(createOrderBo, true);
    }

    /**
     * 同步数据库发放额度
     *
     * @param missionId 奖品ID
     * @param dateType  时间类型
     */
    private void changeMissionQuota(Long missionId, DateType dateType, Duration duration) {
        String key = "changeMissionQuotaCache:" + missionId + ":" + ZlyyhUtils.getDateCacheKey(dateType);
        String cache = RedisUtils.getCacheObject(key);
        if (StringUtils.isBlank(cache)) {
            return;
        }
        RedisUtils.setCacheObject(key, DateUtil.now(), Duration.ofHours(12));
        BigDecimal aLong = this.sumMissionQuota(missionId, null, dateType);
        DrawRedisCacheUtils.setMissionTotalQuota(missionId, aLong, dateType, duration);
    }

    /**
     * 同步数据库用户发放额度
     *
     * @param missionId 奖品ID
     * @param dateType  时间类型
     */
    private void changeUserMissionQuota(Long missionId, Long missionUserId, DateType dateType, Duration duration) {
        String key = "changeUserMissionQuotaCache:" + missionId + ":" + missionUserId + ":" + ZlyyhUtils.getDateCacheKey(dateType);
        String cache = RedisUtils.getCacheObject(key);
        if (StringUtils.isBlank(cache)) {
            return;
        }
        RedisUtils.setCacheObject(key, DateUtil.now(), Duration.ofHours(26));
        BigDecimal aLong = this.sumMissionQuota(missionId, missionUserId, dateType);
        DrawRedisCacheUtils.setUserMissionTotalQuota(missionId, missionUserId, aLong, dateType, duration);
    }

    /**
     * 统计任务发放额度
     *
     * @param missionId     任务ID
     * @param missionUserId 任务用户ID
     * @param dateType      时间类型
     * @return 发放额度
     */
    private BigDecimal sumMissionQuota(Long missionId, Long missionUserId, DateType dateType) {
        LambdaQueryWrapper<MissionUserRecord> lqw = buildQueryWrapper(missionUserId, dateType);
        lqw.eq(MissionUserRecord::getMissionId, missionId);
        return baseMapper.sumMissionQuota(lqw);
    }

    /**
     * 同步数据库发放数量
     *
     * @param drawId   奖品ID
     * @param dateType 时间类型
     */
    private void changeDrawCount(Long drawId, DateType dateType, Duration duration) {
        String key = "changeDrawCountCache:" + drawId + ":" + ZlyyhUtils.getDateCacheKey(dateType);
        String cache = RedisUtils.getCacheObject(key);
        if (StringUtils.isBlank(cache)) {
            return;
        }
        RedisUtils.setCacheObject(key, DateUtil.now(), Duration.ofHours(8));
        Long aLong = queryDrawCount(drawId, null, dateType);
        DrawRedisCacheUtils.setDrawCount(drawId, dateType, aLong, duration);
    }

    /**
     * 同步用户数据库发放数量
     *
     * @param drawId   奖品ID
     * @param dateType 时间类型
     */
    private void changeUserDrawCount(Long drawId, Long missionUserId, DateType dateType, Duration duration) {
        String key = "changeUserDrawCountCache:" + drawId + ":" + missionUserId + ":" + ZlyyhUtils.getDateCacheKey(dateType);
        String cache = RedisUtils.getCacheObject(key);
        if (StringUtils.isBlank(cache)) {
            return;
        }
        RedisUtils.setCacheObject(key, DateUtil.now(), Duration.ofHours(24));
        Long aLong = queryDrawCount(drawId, missionUserId, dateType);
        DrawRedisCacheUtils.setUserDrawCount(drawId, missionUserId, dateType, aLong, duration);
    }

    private Long queryDrawCount(Long drawId, Long missionUserId, DateType dateType) {
        LambdaQueryWrapper<MissionUserRecord> lqw = buildQueryWrapper(missionUserId, dateType);
        lqw.eq(MissionUserRecord::getDrawId, drawId);
        return baseMapper.selectCount(lqw);
    }

    private LambdaQueryWrapper<MissionUserRecord> buildQueryWrapper(Long missionUserId, DateType dateType) {
        LambdaQueryWrapper<MissionUserRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(MissionUserRecord::getStatus, "1");
        lqw.eq(null != missionUserId, MissionUserRecord::getMissionUserId, missionUserId);
        if (dateType == DateType.DAY) {
            lqw.apply("date(draw_time) = curdate()");
        } else if (dateType == DateType.WEEK) {
            lqw.apply("YEARWEEK(date_format(draw_time,'%Y-%m-%d'),1) = YEARWEEK(now(),1)");
        } else if (dateType == DateType.MONTH) {
            lqw.apply("DATE_FORMAT(draw_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m')");
        }
        return lqw;
    }
}
