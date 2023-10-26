package com.ruoyi.system.api;

import java.math.BigDecimal;

public interface RemoteOrderService {
    /**
     * 订单数据统计(每天)
     */
    void dataStatisticsDay();

    /**
     * 订单数据统计(每月)
     */
    void dataStatisticsMonth();

    /**
     * 订单退款
     *
     * @param number       订单号
     * @param amount       退款金额
     * @param refundRemark 退款原因
     */
    void refundOrder(Long number, BigDecimal amount, String refundRemark);
}
