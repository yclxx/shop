package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ShareUserAccount;
import com.ruoyi.zlyyh.domain.ShareUserRecord;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.mapper.ShareUserAccountMapper;
import com.ruoyi.zlyyh.mapper.ShareUserMapper;
import com.ruoyi.zlyyh.mapper.ShareUserRecordMapper;
import com.ruoyi.zlyyh.utils.CloudRechargeEntity;
import com.ruoyi.zlyyh.utils.CloudRechargeUtils;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import com.ruoyi.zlyyhmobile.service.IShareUserRecordService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 分销记录Service业务层处理
 *
 * @author yzg
 * @date 2023-11-09
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ShareUserRecordServiceImpl implements IShareUserRecordService {

    private final ShareUserRecordMapper baseMapper;
    private final IUserService userService;
    private final IPlatformService platformService;
    private final ShareUserAccountMapper shareUserAccountMapper;
    private final ShareUserMapper shareUserMapper;
    @Autowired
    private LockTemplate lockTemplate;

    /**
     * 查询分销总次数
     *
     * @param userId 用户ID
     * @return 分销次数
     */
    public Long shareCount(Long userId) {
        LambdaQueryWrapper<ShareUserRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShareUserRecord::getUserId, userId);
        return baseMapper.selectCount(lqw);
    }

    /**
     * 查询分销记录
     */
    @Override
    public ShareUserRecordVo queryById(Long recordId) {
        return baseMapper.selectVoById(recordId);
    }

    /**
     * 查询分销记录列表
     */
    @Override
    public TableDataInfo<ShareUserRecordVo> queryPageList(ShareUserRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShareUserRecord> lqw = buildQueryWrapper(bo);
        Page<ShareUserRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询分销记录列表
     */
    @Override
    public List<ShareUserRecordVo> queryList(ShareUserRecordBo bo) {
        LambdaQueryWrapper<ShareUserRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShareUserRecord> buildQueryWrapper(ShareUserRecordBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShareUserRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, ShareUserRecord::getUserId, bo.getUserId());
        lqw.eq(bo.getInviteeUserId() != null, ShareUserRecord::getInviteeUserId, bo.getInviteeUserId());
        lqw.eq(bo.getNumber() != null, ShareUserRecord::getNumber, bo.getNumber());
        lqw.eq(bo.getOrderUsedTime() != null, ShareUserRecord::getOrderUsedTime, bo.getOrderUsedTime());
        lqw.eq(bo.getAwardAmount() != null, ShareUserRecord::getAwardAmount, bo.getAwardAmount());
        if (StringUtils.isNotBlank(bo.getInviteeStatus())) {
            lqw.in(ShareUserRecord::getInviteeStatus, bo.getInviteeStatus().split(","));
        }
        if (StringUtils.isNotBlank(bo.getAwardStatus())) {
            if (bo.getAwardStatus().contains(",")) {
                lqw.in(ShareUserRecord::getAwardStatus, bo.getAwardStatus().split(","));
            } else {
                lqw.eq(ShareUserRecord::getAwardStatus, bo.getAwardStatus());
            }
        }
        lqw.eq(bo.getAwardTime() != null, ShareUserRecord::getAwardTime, bo.getAwardTime());
        lqw.eq(StringUtils.isNotBlank(bo.getAwardAccount()), ShareUserRecord::getAwardAccount, bo.getAwardAccount());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            ShareUserRecord::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增分销记录
     */
    @Override
    public Boolean insertByBo(ShareUserRecordBo bo) {
        ShareUserRecord add = BeanUtil.toBean(bo, ShareUserRecord.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setRecordId(add.getRecordId());
        }
        return flag;
    }

    /**
     * 修改分销记录
     */
    @Transactional
    @Override
    public Boolean updateByBo(ShareUserRecordBo bo) {
        ShareUserRecord update = BeanUtil.toBean(bo, ShareUserRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShareUserRecord entity) {
        if (StringUtils.isNotBlank(entity.getPushRemake()) && entity.getPushRemake().length() > 4900) {
            entity.setPushRemake(entity.getPushRemake().substring(0, 4900));
        }
        if (StringUtils.isBlank(entity.getInviteeStatus())) {
            return;
        }
        Long userId = entity.getUserId();
        if (null == userId && null == entity.getRecordId()) {
            throw new ServiceException("分销用户ID不能为空");
        }
        if (null == userId) {
            ShareUserRecordVo shareUserRecordVo = baseMapper.selectVoById(entity.getRecordId());
            if (null == shareUserRecordVo) {
                throw new ServiceException("分销记录不存在");
            }
            userId = shareUserRecordVo.getUserId();
        }
        ShareUserAccount shareUserAccount = shareUserAccountMapper.selectById(userId);
        if (null == shareUserAccount) {
            throw new ServiceException("分销用户账户不存在");
        }
        if (shareUserAccount.getStatus().equals("1")) {
            log.info("分销账户已禁止奖励：{}", shareUserAccount);
            return;
        }
        // 操作邀请人用户账户
        if (null == entity.getRecordId()) {
            if (entity.getInviteeStatus().equals("0") || entity.getInviteeStatus().equals("1")) {
                shareUserAccount.setFreezeBalance(shareUserAccount.getFreezeBalance().add(entity.getAwardAmount()));
            } else if ("2".equals(entity.getInviteeStatus())) {
                shareUserAccount.setWithdrawDeposit(shareUserAccount.getWithdrawDeposit().add(entity.getAwardAmount()));
            }
            shareUserAccountMapper.updateById(shareUserAccount);
        } else {
            ShareUserRecordVo shareUserRecordVo = baseMapper.selectVoById(entity.getRecordId());
            if (null == shareUserRecordVo) {
                log.info("分销记录不存在：{}", entity);
                return;
            }
            if (shareUserRecordVo.getInviteeStatus().equals(entity.getInviteeStatus())) {
                return;
            }
            if (entity.getInviteeStatus().equals("0") || entity.getInviteeStatus().equals("1")) {
                return;
            }
            if ("2".equals(entity.getInviteeStatus())) {
                shareUserAccount.setFreezeBalance(shareUserAccount.getFreezeBalance().subtract(shareUserRecordVo.getAwardAmount()));
                shareUserAccount.setWithdrawDeposit(shareUserAccount.getWithdrawDeposit().add(shareUserRecordVo.getAwardAmount()));
            } else if ("3".equals(entity.getInviteeStatus()) || "4".equals(entity.getInviteeStatus()) || "5".equals(entity.getInviteeStatus())) {
                shareUserAccount.setFreezeBalance(shareUserAccount.getFreezeBalance().subtract(shareUserRecordVo.getAwardAmount()));
            }
            shareUserAccountMapper.updateById(shareUserAccount);
        }
    }

    /**
     * 批量删除分销记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public List<ShareUserRecordVo> queryByNumber(Long number) {
        LambdaQueryWrapper<ShareUserRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShareUserRecord::getNumber, number);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 发送奖励
     *
     * @param recordId 分享记录ID
     */
    @Override
    public void sendAward(Long recordId) {
        // 加锁
        String key = "shareOrderSendAward:" + recordId;
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 1000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        // 获取锁成功，处理业务
        try {
            ShareUserRecordVo shareUserRecordVo = baseMapper.selectVoById(recordId);
            if (shareUserRecordVo == null) {
                log.error("分销奖励记录不存在");
                return;
            }
            if (!"0".equals(shareUserRecordVo.getAwardStatus()) && !"3".equals(shareUserRecordVo.getAwardStatus())) {
                log.error("已奖励或奖励中");
                return;
            }
            ShareUserVo shareUserVo = shareUserMapper.selectVoById(shareUserRecordVo.getUserId());
            if (shareUserVo == null) {
                log.error("分销用户不存在");
                return;
            }
            ShareUserAccount shareUserAccount = shareUserAccountMapper.selectById(shareUserRecordVo.getUserId());
            if (null == shareUserAccount) {
                throw new ServiceException("分销用户账户不存在");
            }
            if (shareUserAccount.getStatus().equals("1")) {
                log.info("分销账户已禁止奖励：{}", shareUserAccount);
                return;
            }
            PlatformVo platformVo = platformService.queryById(shareUserVo.getPlatformKey(), PlatformEnumd.MP_YSF.getChannel());
            if (platformVo == null) {
                log.error("平台信息不存在");
                return;
            }
            if (platformVo.getShareUsedDate() > 0) {
                // 校验时间是否符合
                if (shareUserRecordVo.getOrderUsedTime() == null ||
                    shareUserRecordVo.getOrderUsedTime().getTime() < System.currentTimeMillis() - platformVo.getShareUsedDate() * 24 * 60 * 60 * 1000) {
                    log.error("未到奖励时间，核销：{}天之后才能奖励,{}", platformVo.getShareUsedDate(), shareUserRecordVo);
                    return;
                }
            }
            String channel = PlatformEnumd.MP_YSF.getChannel();
            // 查询用户信息 后续需更具奖励类型去查询不同端的用户信息
            UserVo userVo = userService.queryById(shareUserRecordVo.getUserId(), channel);
            if (userVo == null) {
                ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
                shareUserRecordBo.setRecordId(recordId);
                shareUserRecordBo.setAwardStatus("2");
                shareUserRecordBo.setAwardTime(new Date());
                shareUserRecordBo.setPushRemake("一级分销员系统不发放");
                updateByBo(shareUserRecordBo);
                return;
            }
            ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
            shareUserRecordBo.setRecordId(recordId);
            shareUserRecordBo.setAwardStatus("1");
            shareUserRecordBo.setAwardTime(new Date());
            shareUserRecordBo.setAwardAccount(userVo.getMobile());
            shareUserRecordBo.setAwardPushNumber(IdUtil.getSnowflakeNextIdStr());

            updateByBo(shareUserRecordBo);
            // 请求接口发奖
            R<Void> result = CloudRechargeUtils.doPostCreateOrder(shareUserRecordBo.getRecordId(), platformVo.getShareAwardProductId(), shareUserRecordBo.getAwardAccount(), 1, shareUserRecordVo.getAwardAmount(), "订单分佣奖励", shareUserRecordBo.getAwardPushNumber(), "/zlyyh-mobile/shareUser/ignore/orderCallback");
            if (R.FAIL == result.getCode()) {
                fail(shareUserRecordBo.getRecordId(), result.getMsg());
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 发送奖励
     *
     * @param recordId 分享记录ID
     */
    @Override
    public void querySendAwardStatus(Long recordId) {
        ShareUserRecord shareUserRecord = baseMapper.selectById(recordId);
        if (shareUserRecord == null) {
            log.error("分销奖励记录不存在");
            return;
        }
        if (!"1".equals(shareUserRecord.getAwardStatus())) {
            return;
        }
        // 请求接口发奖
        R<JSONObject> result = CloudRechargeUtils.doPostQueryOrder(shareUserRecord.getRecordId(), shareUserRecord.getAwardPushNumber());
        if (R.isSuccess(result)) {
            rechargeResult(result.getData(), shareUserRecord);
        } else {
            if (R.FAIL == result.getCode()) {
                fail(recordId, result.getMsg());
            }
        }
    }

    private void fail(Long recordId, String msg) {
        // 发放失败
        ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
        shareUserRecordBo.setRecordId(recordId);
        shareUserRecordBo.setAwardStatus("3");
        shareUserRecordBo.setPushRemake(msg);
        updateByBo(shareUserRecordBo);
    }

    public BigDecimal sumAwardAmount(Long userId) {
        return baseMapper.sumAwardAmount(userId);
    }

    @Override
    public void cloudRechargeCallback(CloudRechargeEntity huiguyunEntity) {
        CloudRechargeUtils.getData(huiguyunEntity);
        JSONObject data = JSONObject.parseObject(huiguyunEntity.getEncryptedData());
        // 查询订单是否存在
        ShareUserRecord shareUserRecord = baseMapper.selectOne(new LambdaQueryWrapper<ShareUserRecord>().eq(ShareUserRecord::getAwardPushNumber, data.getString("externalOrderNumber")).last("limit 1"));
        if (null == shareUserRecord) {
            throw new ServiceException("订单不存在");
        }
        rechargeResult(data, shareUserRecord);
        // 修改订单信息
        ShareUserRecordBo shareUserRecordBo = BeanUtil.toBean(shareUserRecord, ShareUserRecordBo.class);
        updateByBo(shareUserRecordBo);
    }

    /**
     * 充值中心订单结果处理
     *
     * @param data            订单结果
     * @param shareUserRecord 请求信息
     */
    private void rechargeResult(JSONObject data, ShareUserRecord shareUserRecord) {
        String state = data.getString("state");
        if ("4".equals(state)) {
            // 发放成功
            shareUserRecord.setAwardStatus("2");
        } else if ("5".equals(state)) {
            // 发放失败
            shareUserRecord.setAwardStatus("3");
        }
        shareUserRecord.setPushRemake(data.toJSONString());
    }
}
