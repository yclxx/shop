package com.ruoyi.zlyyhmobile.mq.consumer;

import cn.hutool.core.thread.ThreadUtil;
import com.ruoyi.zlyyhmobile.mq.TestMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 订单发券消费者
 *
 * @author 25487
 */
@Slf4j
@Component
public class TestStreamConsumer {

    @Bean
    Consumer<TestMessaging> test() {
        log.info("初始化订阅");
        return msg -> {
            ThreadUtil.sleep(1000);
            log.info("测试通过stream消费到消息 => {}", msg.toString());
        };
    }

}
