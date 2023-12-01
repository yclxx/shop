package com.ruoyi.system.api;

/**
 * 分销服务
 *
 * @author Lion Li
 */
public interface RemoteShareService {

    /**
     * 订单分销事件
     *
     * @param shareUserId 分销用户
     * @param number      订单号
     */
    void shareOrderEvent(Long shareUserId, Long number);
}
