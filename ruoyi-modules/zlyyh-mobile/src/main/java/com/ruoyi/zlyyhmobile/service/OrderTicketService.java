package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.OrderTicketVo;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderTicketBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;

public interface OrderTicketService {

    /**
     * 创建演出票订单
     */
    CreateOrderResult createTicketOrder(CreateOrderTicketBo params);

    /**
     * 查询订单详情
     *
     * @param number
     * @return
     */
    OrderTicketVo orderInfo(Long number);

    /**
     * 查询历史订单详情
     */
    OrderTicketVo getHistoryOrderInfo(Long number);
}
