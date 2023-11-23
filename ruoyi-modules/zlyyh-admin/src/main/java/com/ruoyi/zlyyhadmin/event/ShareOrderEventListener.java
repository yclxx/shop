package com.ruoyi.zlyyhadmin.event;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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
    @Autowired
    private LockTemplate lockTemplate;

    /**
     * 分销订单事件 退款
     */
    @Async
    @EventListener
    public void cacheMissionRecord(ShareOrderEvent event) {
        log.info("分销订单事件，接收参数：{}", event);
        if (null == event.getNumber()) {
            log.error("分销订单事件，接收参数为空,event:{}", event);
            return;
        }
        // 订单退款
        String inviteeStatus = "4";
        String key = "shareOrder:" + event.getNumber();
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        // 获取锁成功，处理业务
        try {
            ShareUserRecordBo queryBo = new ShareUserRecordBo();
            queryBo.setNumber(event.getNumber());
            List<ShareUserRecordVo> shareUserRecordVos = shareUserRecordService.queryList(queryBo);
            for (ShareUserRecordVo shareUserRecordVo : shareUserRecordVos) {
                if ("0".equals(shareUserRecordVo.getInviteeStatus()) || "1".equals(shareUserRecordVo.getInviteeStatus())) {
                    ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
                    shareUserRecordBo.setRecordId(shareUserRecordVo.getRecordId());
                    shareUserRecordBo.setInviteeStatus(inviteeStatus);
                    shareUserRecordService.updateByBo(shareUserRecordBo);
                } else {
                    log.info("分销记录已是最终状态：{}", shareUserRecordVo);
                }
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    private void addShareUserRecord(Long shareUserId, Long number, BigDecimal awardAmount, Long inviteeUserId, String inviteeStatus) {
        if (null == shareUserId) {
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
