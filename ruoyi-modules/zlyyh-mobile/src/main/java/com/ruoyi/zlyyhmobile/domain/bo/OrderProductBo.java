package com.ruoyi.zlyyhmobile.domain.bo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderProductBo {

    /**
     * 商品ID
     */
    @NotNull(message = "产品不能为空")
    private Long productId;

    /**
     * 购买数量
     */
    private Long quantity;

    /**
     * 购物车ID
     */
    private Long cartId;
}
