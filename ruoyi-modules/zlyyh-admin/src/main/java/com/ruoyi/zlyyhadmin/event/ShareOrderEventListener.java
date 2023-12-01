package com.ruoyi.zlyyhadmin.event;

import com.ruoyi.system.api.RemoteShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
    @DubboReference(retries = 0)
    private RemoteShareService remoteShareService;

    /**
     * 分销订单事件
     */
    @Async
    @EventListener
    public void shareOrderEvent(ShareOrderEvent event) {
        remoteShareService.shareOrderEvent(event.getShareUserId(), event.getNumber());
    }
}
