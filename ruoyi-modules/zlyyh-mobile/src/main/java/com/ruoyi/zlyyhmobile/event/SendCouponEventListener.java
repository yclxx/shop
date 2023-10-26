package com.ruoyi.zlyyhmobile.event;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyhmobile.mq.producer.OrderStreamProducer;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 异步调用
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SendCouponEventListener {
    private final OrderStreamProducer orderStreamProducer;
    private final IOrderService orderService;
    private final YsfConfigService ysfConfigService;
    private final IPlatformService platformService;

    /**
     * 发券
     */
    @Async
    @EventListener
    public void sendCoupon(SendCouponEvent sendCouponEvent) {
        long atomicValue1 = RedisUtils.getAtomicValue(ZlyyhConstants.SEND_COUPON_NUMBER);
        // 设置发券数量
        if (atomicValue1 <= 0) {
            RedisUtils.setAtomicValue(ZlyyhConstants.SEND_COUPON_NUMBER, 1);
        } else {
            RedisUtils.incrAtomicValue(ZlyyhConstants.SEND_COUPON_NUMBER);
        }
        long atomicValue = RedisUtils.getAtomicValue(ZlyyhConstants.SEND_COUPON_MQ_COUNT);
        if (atomicValue < 0) {
            RedisUtils.setAtomicValue(ZlyyhConstants.SEND_COUPON_MQ_COUNT, 0);
        }
        int max = 500;
        String maxMqCount = ysfConfigService.queryValueByKey(sendCouponEvent.getPlatformKey(), "maxMqCount");
        if (NumberUtil.isInteger(maxMqCount)) {
            max = Integer.parseInt(maxMqCount);
        }
        if (atomicValue > max) {
            log.info("订单直接发券:{}", sendCouponEvent);
            orderService.sendCoupon(sendCouponEvent.getNumber());
            return;
        }
        RedisUtils.incrAtomicValue(ZlyyhConstants.SEND_COUPON_MQ_COUNT);
        orderStreamProducer.streamOrderMsg(sendCouponEvent.getNumber().toString());

        // 判断并发送消息
        try {
            String sendMsgKey = "sendMsgBySendCoupon";
            String sendData = RedisUtils.getCacheObject(sendMsgKey);
            if (StringUtils.isBlank(sendData)) {
                String sendCouponNumber = ysfConfigService.queryValueByKey(sendCouponEvent.getPlatformKey(), "sendCouponNumber");
                if (NumberUtil.isLong(sendCouponNumber)) {
                    long num = Long.parseLong(sendCouponNumber);
                    long atomicValue2 = RedisUtils.getAtomicValue(ZlyyhConstants.SEND_COUPON_NUMBER);
                    if (atomicValue2 == num) {
                        PlatformVo platformVo = platformService.queryById(sendCouponEvent.getPlatformKey(), PlatformEnumd.MP_YSF.getChannel());
                        if (null != platformVo) {
                            String backendToken = YsfUtils.getBackendToken(platformVo.getAppId(), platformVo.getSecret(), false, platformVo.getPlatformKey());
                            orderService.ysfForewarningMessage(platformVo.getPlatformKey(), backendToken, "sendDescDetails", "sendTemplateValue");
                            RedisUtils.setCacheObject(sendMsgKey, DateUtil.now(), Duration.ofMinutes(10));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("预警消息发送异常：", e);
        }
    }
}
