package com.ruoyi.system.api;

public interface RemoteOrderService {
    /**
     * 订单数据统计(每天)
     */
    void dataStatisticsDay();

    /**
     * 订单数据统计(每月)
     */
    void dataStatisticsMonth();
}
