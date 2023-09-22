package com.ruoyi.zlyyhmobile.event;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.zlyyhmobile.utils.UnionPayUtils;
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
public class UnionPayContentRefundCallbackEventListener {

    /**
     * 退券通知
     */
    @Async
    @EventListener
    public void refundCoupon(UnionPayContentRefundCallbackEvent event) {
        String orderId = IdUtil.getSnowflakeNextIdStr();
        String bizMethod = "up.supp.returnbond";

        JSONObject params = UnionPayUtils.getCallbackJson(orderId, bizMethod, event.getAppId(), event.getUnionPayProductId());
        params.put("bondNo", event.getBondNo());
        params.put("procTime", event.getProcTime());

        UnionPayUtils.postUnionPay(params,event.getAppId(),bizMethod,orderId,event.getPrivateKey(), event.getCallbackUrl());
    }
}
