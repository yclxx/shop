package com.ruoyi.zlyyhmobile.event;

import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyhmobile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 异步调用
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ShareOrderEventListener {
    private final IShareUserService shareUserService;
    private final IOrderService orderService;
    private final IProductService productService;
    private final IPlatformService platformService;
    private final IShareUserRecordService shareUserRecordService;

    /**
     * 分销订单事件
     */
    @Async
    @EventListener
    public void cacheMissionRecord(ShareOrderEvent event) {
        log.info("分销订单事件，接收参数：{}", event);
        if (null == event.getShareUserId() || null == event.getNumber()) {
            log.error("分销订单事件，接收参数为空,event:{}", event);
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
        OrderVo orderVo = orderService.queryById(event.getNumber());
        if (null == orderVo) {
            log.error("分销订单事件，订单不存在，event={}", event);
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
            inviteeStatus = "1";
        } else if ("3".equals(orderVo.getStatus())) {
            inviteeStatus = "3";
        }
        BigDecimal shareOneAmount = productVo.getShareOneAmount();
        if (shareOneAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal awardAmount = shareOneAmount;
            if ("1".equals(productVo.getShareAmountType())) {
                awardAmount = productVo.getSellAmount().multiply(shareOneAmount);
            }
            addShareUserRecord(event.getShareUserId(), event.getNumber(), awardAmount, orderVo.getUserId(), inviteeStatus);
        }
        BigDecimal shareTwoAmount = productVo.getShareTwoAmount();
        if (shareTwoAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal awardAmount = shareTwoAmount;
            if ("1".equals(productVo.getShareAmountType())) {
                awardAmount = productVo.getSellAmount().multiply(shareTwoAmount);
            }
            addShareUserRecord(shareUserVo.getParentId(), event.getNumber(), awardAmount, orderVo.getUserId(), inviteeStatus);
        }
    }

    private void addShareUserRecord(Long shareUserId, Long number, BigDecimal awardAmount, Long inviteeUserId, String inviteeStatus) {
        if (null == shareUserId) {
            return;
        }
        // 查询是否存在记录
        ShareUserRecordVo shareUserRecordVo = shareUserRecordService.queryByShareUserIdAndNumber(shareUserId, number);
        if (null != shareUserRecordVo) {
            log.error("分销订单事件，订单已存在，shareUserRecordVo={}", shareUserRecordVo);
            return;
        }
        ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
        shareUserRecordBo.setUserId(shareUserId);
        shareUserRecordBo.setInviteeUserId(inviteeUserId);
        shareUserRecordBo.setNumber(number);
        shareUserRecordBo.setAwardAmount(awardAmount);
        shareUserRecordBo.setInviteeStatus(inviteeStatus);
        shareUserRecordService.insertByBo(shareUserRecordBo);
    }
}
