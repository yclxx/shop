package com.ruoyi.zlyyhmobile.event;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.zlyyhmobile.enums.UnionPayCallbackBizMethodType;
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
public class UnionPayContentCallbackEventListener {

    /**
     * 退券通知
     */
    @Async
    @EventListener
    public void unionPayCallback(UnionPayContentCallbackEvent event) {
        String orderId = IdUtil.getSnowflakeNextIdStr();

        JSONObject params = UnionPayUtils.getCallbackJson(orderId, event.getBizMethodType().getBizMethod(), event.getAppId(), event.getUnionPayProductId());
        params.put("bondNo", event.getBondNo());
        params.put("procTime", event.getProcTime());
        if (UnionPayCallbackBizMethodType.ROLLBACK.equals(event.getBizMethodType())) {
            params.put("bondSt", "05");
        }
        UnionPayUtils.postUnionPay(params, event.getAppId(), event.getBizMethodType().getBizMethod(), orderId, event.getPrivateKey(), event.getCallbackUrl());
    }
}
