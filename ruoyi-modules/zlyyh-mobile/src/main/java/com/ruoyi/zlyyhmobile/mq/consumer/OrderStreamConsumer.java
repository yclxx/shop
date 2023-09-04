package com.ruoyi.zlyyhmobile.mq.consumer;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyhmobile.mq.OrderMessaging;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrderStreamConsumer {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private LockTemplate lockTemplate;

    @Bean
    Consumer<OrderMessaging> order() {
        log.info("初始化订阅");
        return msg -> {
            log.info("通过stream消费到消息 => {}", msg.toString());
            final LockInfo lockInfo = lockTemplate.lock(msg.getMsgId(), 20000L, 3000L, RedissonLockExecutor.class);
            if (null == lockInfo) {
                log.error("该条消息已经在被消费了：{}", msg);
                return;
            }
            RedisUtils.decrAtomicValue(ZlyyhConstants.SEND_COUPON_MQ_COUNT);
            // 获取锁成功，处理业务
            try {
                if (StringUtils.isBlank(msg.getMsgText()) || !NumberUtil.isLong(msg.getMsgText())) {
                    log.error("消息内容有误：{}", msg);
                    return;
                }
                orderService.sendCoupon(Long.parseLong(msg.getMsgText()));
            } finally {
                //释放锁
                lockTemplate.releaseLock(lockInfo);
            }
            //结束
        };
    }

}
