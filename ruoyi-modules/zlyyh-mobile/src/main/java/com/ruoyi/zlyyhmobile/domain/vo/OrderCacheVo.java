package com.ruoyi.zlyyhmobile.domain.vo;

import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单缓存信息
 * @author 25487
 */
@Data
public class OrderCacheVo implements Serializable {
    /**
     * 订单信息
     */
    private Order order;
    /**
     * 订单扩展
     */
    private OrderInfo orderInfo;
}
