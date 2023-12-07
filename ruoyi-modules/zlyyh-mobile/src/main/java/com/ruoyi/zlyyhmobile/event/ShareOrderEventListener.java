package com.ruoyi.zlyyhmobile.event;

import com.ruoyi.system.api.RemoteShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步调用
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ShareOrderEventListener {
    private final RemoteShareService remoteShareService;

    /**
     * 分销订单事件
     */
    @Async
    @EventListener
    public void shareOrderEvent(ShareOrderEvent event) {
        remoteShareService.shareOrderEvent(event.getShareUserId(), event.getNumber());
    }

}
