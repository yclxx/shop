package com.ruoyi.zlyyhmobile.mq.producer;

import com.ruoyi.zlyyhmobile.mq.TestMessaging;
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
public class TestStreamProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void streamTestMsg(String msg) {
        // 构建消息对象
        TestMessaging messaging = new TestMessaging()
            .setMsgId(UUID.randomUUID().toString())
            .setMsgText(msg);
        streamBridge.send("test-out-0", MessageBuilder.withPayload(messaging).build());
    }
}
