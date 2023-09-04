package com.ruoyi.system.api;

import com.ruoyi.common.core.domain.R;

/**
 * 订单服务
 *
 * @author Lion Li
 */
public interface RemoteAppOrderService {

    /**
     * 订单发券
     */
    void sendCoupon(Long number);

    void cancelFoodOrder(Long number);


    void cancelHistoryFoodOrder(Long number);


    /**
     * 定时任务 查询订单发放状态
     */
    void queryOrderSendStatus(String pushNumber);

    /**
     * 定时任务 保存订单数据至MySQL中去
     */
    void orderCacheSaveToMySql();

    /**
     * 定时任务 关闭失效订单
     */
    void cancelOrder();

    /**
     * 补领订单
     */
    void reloadOrder();

    /**
     * 活动订单补发
     */
    void sendDraw(Long missionUserRecordId);

    /**
     * 查询任务完成进度
     */
    void queryMission();


    /**
     * 银联分销 退券
     */
    R<Void> couponRefundOrder(Long number);

    /**
     * 订单迁移至历史订单
     */
    void orderToHistory();
}
