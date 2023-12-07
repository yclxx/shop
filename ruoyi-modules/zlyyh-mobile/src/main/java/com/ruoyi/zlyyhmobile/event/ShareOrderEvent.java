package com.ruoyi.zlyyhmobile.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 25487
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareOrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 分销用户ID
     */
    private Long shareUserId;
    /**
     * 分销订单
     */
    private Long number;
}
