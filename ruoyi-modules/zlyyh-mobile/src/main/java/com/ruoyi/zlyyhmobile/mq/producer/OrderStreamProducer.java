package com.ruoyi.zlyyhmobile.mq.producer;

import com.ruoyi.zlyyhmobile.mq.OrderMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 订单发券生产者
 * @author 25487
 */
@Component
public class OrderStreamProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void streamOrderMsg(String msg) {
        // 构建消息对象
        OrderMessaging messaging = new OrderMessaging()
            .setMsgId(UUID.randomUUID().toString())
            .setMsgText(msg);
        streamBridge.send("order-out-0", MessageBuilder.withPayload(messaging).build());
    }
}
