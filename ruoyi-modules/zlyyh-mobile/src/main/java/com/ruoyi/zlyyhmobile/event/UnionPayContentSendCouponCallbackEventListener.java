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
public class UnionPayContentSendCouponCallbackEventListener {

    /**
     * 发券
     */
    @Async
    @EventListener
    public void sendCoupon(UnionPayContentSendCouponCallbackEvent event) {
        String orderId = IdUtil.getSnowflakeNextIdStr();
        String bizMethod = "up.supp.querybondres";

        JSONObject params = UnionPayUtils.getCallbackJson(orderId, bizMethod, event.getAppId(), event.getUnionPayProductId());
        params.put("origTxnTime", event.getOrigTxnTime());
        params.put("origOrderId", event.getOrigOrderId());
        params.put("origProcSt", "00");
        params.put("bondLst", event.getBondList());
        params.put("prodCertTp", event.getProdCertTp());

        UnionPayUtils.postUnionPay(params,event.getAppId(),bizMethod,orderId,event.getPrivateKey(), event.getCallbackUrl());
    }
}
