package com.ruoyi.zlyyhmobile.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 25487
 */
@Data
public class UserProductCount implements Serializable {
    /**
     * 购买次数
     */
    private Integer payCount;
    /**
     * 今日是否可购买
     */
    private boolean dayPay;

    /**
     * 今日购买产品ID
     */
    private Long productId;

    public UserProductCount() {
        this.payCount = 0;
        this.dayPay = true;
    }
}
