package com.ruoyi.job.service;

import com.ruoyi.system.api.RemoteAppOrderService;
import com.ruoyi.system.api.RemoteOrderService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 订单定时任务
 */
@Slf4j
@Service
public class OrderService {

    @DubboReference(retries = 0)
    private RemoteAppOrderService remoteAppOrderService;

    @DubboReference(retries = 0)
    private RemoteOrderService remoteOrderService;

    /**
     * 查询订单支付状态
     */
    @XxlJob("queryOrderHandler")
    public void queryOrderHandler() {
        remoteAppOrderService.queryOrderHandler();
    }

    /**
     * 过期取消订单
     */
    @XxlJob("cancelOrderHandler")
    public void cancelOrderHandler() {
        remoteAppOrderService.cancelOrder();
    }

    /**
     * 自动补发
     */
    @XxlJob("reloadOrderHandler")
    public void reloadOrderHandler() {
        remoteAppOrderService.reloadOrder();
    }

    /**
     * 获取用户任务完成进度
     */
    @XxlJob("queryMission")
    public void queryMission() {
        remoteAppOrderService.queryMission();
    }

    /**
     * 订单数据统计(按天统计)
     */
    @XxlJob("orderComputeDay")
    public void orderComputeDay() {
        remoteOrderService.dataStatisticsDay();
    }

    /**
     * 订单数据统计(按月统计)
     */
    @XxlJob("orderComputeMonty")
    public void orderComputeMonth() {
        remoteOrderService.dataStatisticsMonth();
    }

    /**
     * 订单迁移至历史订单
     */
    @XxlJob("orderToHistory")
    public void orderToHistory() {
        remoteAppOrderService.orderToHistory();
    }

    /**
     * 订单迁移至历史订单
     */
    @XxlJob("addCollectiveOrder")
    public void addCollectiveOrder() {
        remoteAppOrderService.addCollectiveOrder();
    }

    /**
     * 查询订单核销状态
     */
    @XxlJob("queryOrderUsedStatus")
    public void queryOrderUsedStatus() {
        remoteAppOrderService.queryOrderUsedStatus();
    }

    /**
     * 分销订单状态查询
     */
    @XxlJob("queryShareStatus")
    public void queryShareStatus() {
        remoteAppOrderService.queryShareStatus();
    }

    /**
     * 分销订单发放状态查询
     */
    @XxlJob("queryShareAwardStatus")
    public void queryShareAwardStatus() {
        remoteAppOrderService.queryShareAwardStatus();
    }
}
