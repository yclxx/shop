package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.Order;

/**
 * @author 25487
 */
public interface IUnionPayChannelService {
    /**
     * 银联分销 渠道方 直销 创建订单
     *
     * @param unionPayProductId 银联产品编号
     * @param order             订单信息
     */
    void createUnionPayOrder(String unionPayProductId, Order order);

    /**
     * 银联直销订单支付
     *
     * @param number      订单号
     * @param platformKey 平台标识
     * @return 支付tn号
     */
    String getPayTn(Long number, Long platformKey);

    /**
     * 银联分销 直销 订单发券
     *
     * @param order         订单信息
     */
    void orderSend(Order order);
}
