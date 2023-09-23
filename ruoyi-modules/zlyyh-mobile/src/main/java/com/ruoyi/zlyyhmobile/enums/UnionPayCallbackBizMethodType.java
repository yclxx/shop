package com.ruoyi.zlyyhmobile.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 银联分销回调通知类型
 *
 * @author Lion Li
 */
@Getter
@AllArgsConstructor
public enum UnionPayCallbackBizMethodType {

    /**
     * 票券返还
     */
    ROLLBACK("up.supp.rollback"),

    /**
     * 退券
     */
    RETURN_BOND("up.supp.returnbond"),

    /**
     * 核销
     */
    VERIFY_BOND("up.supp.verifybond");

    private final String bizMethod;
}
