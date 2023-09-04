package com.ruoyi.zlyyhmobile.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 创建订单返回结果
 * @author 25487
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResult implements Serializable {
    /** 订单号 */
    private Long number;
    /** 领取状态： 0-领取成功，1-需前往支付 */
    private String status;
}
