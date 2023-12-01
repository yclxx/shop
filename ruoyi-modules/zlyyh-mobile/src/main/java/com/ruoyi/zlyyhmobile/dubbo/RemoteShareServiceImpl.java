package com.ruoyi.zlyyhmobile.dubbo;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.ruoyi.system.api.RemoteShareService;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyhmobile.event.ShareOrderEvent;
import com.ruoyi.zlyyhmobile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteShareServiceImpl implements RemoteShareService {
    private final IShareUserService shareUserService;
    private final IOrderService orderService;
    private final IProductService productService;
    private final IPlatformService platformService;
    private final IShareUserRecordService shareUserRecordService;
    @Autowired
    private LockTemplate lockTemplate;

    @Override
    public void shareOrderEvent(Long shareUserId, Long number) {
        ShareOrderEvent event = new ShareOrderEvent(shareUserId, number);
        log.info("分销订单事件，接收参数：{}", event);
        if (null == event.getNumber()) {
            log.error("分销订单事件，接收参数为空,event:{}", event);
            return;
        }
        // 休眠1秒再处理
        ThreadUtil.sleep(RandomUtil.randomInt(1000, 1500));
        OrderVo orderVo = orderService.queryById(event.getNumber());
        if (null == orderVo) {
            log.error("分销订单事件，订单不存在，event={}", event);
            return;
        }
        if (null != event.getShareUserId() && orderVo.getUserId().equals(event.getShareUserId())) {
            log.error("分销订单事件，不能分销给自己，event={}，orderUserId={}", event, orderVo.getUserId());
            return;
        }
        PlatformVo platformVo = platformService.queryById(orderVo.getPlatformKey(), orderVo.getSupportChannel());
        if (null == platformVo) {
            log.error("分销订单事件，平台不存在，event={}", event);
            return;
        }
        if ("0".equals(platformVo.getSharePermission())) {
            log.error("分销订单事件，平台未开通分销功能，event={}", event);
            return;
        }
        ProductVo productVo = productService.queryById(orderVo.getProductId());
        if (null == productVo) {
            log.error("分销订单事件，商品不存在，event={}", event);
            return;
        }
        if ("0".equals(productVo.getSharePermission())) {
            log.error("分销订单事件，商品未开通分销功能，event={}，productId:{}", event, productVo.getProductId());
            return;
        }
        String inviteeStatus = "0";
        if ("2".equals(orderVo.getStatus())) {
            // 支付成功
            inviteeStatus = "1";
            if ("1".equals(orderVo.getVerificationStatus())) {
                // 已核销
                inviteeStatus = "2";
            } else if ("2".equals(orderVo.getVerificationStatus())) {
                // 已失效
                inviteeStatus = "5";
            }
        } else if ("3".equals(orderVo.getStatus())) {
            // 订单关闭
            inviteeStatus = "3";
        } else if ("4".equals(orderVo.getStatus()) || "5".equals(orderVo.getStatus()) || "6".equals(orderVo.getStatus())) {
            // 订单退款
            inviteeStatus = "4";
        }
        String key = "shareOrder:" + event.getNumber();
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        // 获取锁成功，处理业务
        try {
            List<ShareUserRecordVo> shareUserRecordVos = shareUserRecordService.queryByNumber(event.getNumber());
            if (ObjectUtil.isEmpty(shareUserRecordVos)) {
                // 新增记录
                if (null == event.getShareUserId()) {
                    log.error("分销订单事件，缺少分销用户ID，{}", event);
                    return;
                }
                ShareUserVo shareUserVo = shareUserService.queryById(event.getShareUserId());
                if (null == shareUserVo) {
                    log.error("分销订单事件，分销用户不存在{}", event);
                    return;
                }
                if (!"1".equals(shareUserVo.getAuditStatus())) {
                    log.error("分销订单事件，分销用户未审核通过，shareUser={},event={}", shareUserVo, event);
                    return;
                }
                // 查询是否存在记录
                BigDecimal shareOneAmount = productVo.getShareOneAmount();
                if (shareOneAmount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal awardAmount = shareOneAmount;
                    if ("1".equals(productVo.getShareAmountType())) {
                        awardAmount = productVo.getSellAmount().multiply(shareOneAmount);
                    }
                    awardAmount = awardAmount.multiply(new BigDecimal(orderVo.getCount()));
                    addShareUserRecord(shareUserVo.getParentId(), event.getNumber(), awardAmount, orderVo.getUserId(), inviteeStatus, orderVo.getProductName(), orderVo.getPlatformKey());
                }
                BigDecimal shareTwoAmount = productVo.getShareTwoAmount();
                if (shareTwoAmount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal awardAmount = shareTwoAmount;
                    if ("1".equals(productVo.getShareAmountType())) {
                        awardAmount = productVo.getSellAmount().multiply(shareTwoAmount);
                    }
                    awardAmount = awardAmount.multiply(new BigDecimal(orderVo.getCount()));
                    addShareUserRecord(shareUserVo.getUserId(), event.getNumber(), awardAmount, orderVo.getUserId(), inviteeStatus, orderVo.getProductName(), orderVo.getPlatformKey());
                }
            } else {
                for (ShareUserRecordVo shareUserRecordVo : shareUserRecordVos) {
                    if ("0".equals(shareUserRecordVo.getInviteeStatus()) || "1".equals(shareUserRecordVo.getInviteeStatus())) {
                        // 校验奖励金额是否达上限，超过上限，不再发放奖励
                        boolean sendFlag = false;
                        ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
                        shareUserRecordBo.setRecordId(shareUserRecordVo.getRecordId());
                        if ("2".equals(inviteeStatus)) {
                            shareUserRecordBo.setOrderUsedTime(new Date());
                            if (platformVo.getShareAwardMonthAmount().compareTo(BigDecimal.ZERO) > 0) {
                                BigDecimal bigDecimal = shareUserRecordService.sumAwardAmount(shareUserRecordVo.getUserId());
                                if (bigDecimal.compareTo(platformVo.getShareAwardMonthAmount()) >= 0) {
                                    sendFlag = true;
                                }
                            }
                        }
                        shareUserRecordBo.setInviteeStatus(inviteeStatus);
                        shareUserRecordService.updateByBo(shareUserRecordBo);
                        if ("2".equals(inviteeStatus)) {
                            if (sendFlag) {
                                ShareUserRecordBo sb = new ShareUserRecordBo();
                                sb.setRecordId(shareUserRecordVo.getRecordId());
                                sb.setAwardStatus("2");
                                sb.setAwardTime(new Date());
                                sb.setActualReleasAmount(new BigDecimal("0"));
                                sb.setRemake("本月获得奖励金额已达上限。");
                                shareUserRecordService.updateByBo(sb);
                            } else {
                                // 发放奖励
                                shareUserRecordService.sendAward(shareUserRecordVo.getRecordId());
                            }
                        }
                    } else {
                        if ("2".equals(shareUserRecordVo.getInviteeStatus()) && ("4".equals(inviteeStatus) || "1".equals(inviteeStatus))) {
                            ShareUserRecordBo sb = new ShareUserRecordBo();
                            sb.setRecordId(shareUserRecordVo.getRecordId());
                            sb.setInviteeStatus(inviteeStatus);
                            shareUserRecordService.updateByBo(sb);
                            log.info("分销记录核销后再次变更：recordVo={},inviteeStatus={}", shareUserRecordVo, inviteeStatus);
                        } else {
                            log.info("分销记录已是最终状态：{}", shareUserRecordVo);
                        }
                    }
                }
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    private void addShareUserRecord(Long shareUserId, Long number, BigDecimal awardAmount, Long inviteeUserId, String inviteeStatus, String productName, Long platformKey) {
        if (null == shareUserId) {
            return;
        }
        ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
        shareUserRecordBo.setUserId(shareUserId);
        shareUserRecordBo.setInviteeUserId(inviteeUserId);
        shareUserRecordBo.setNumber(number);
        shareUserRecordBo.setProductName(productName);
        shareUserRecordBo.setAwardAmount(awardAmount);
        shareUserRecordBo.setInviteeStatus(inviteeStatus);
        shareUserRecordService.insertByBo(shareUserRecordBo, platformKey);
    }
}
