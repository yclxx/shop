package com.ruoyi.zlyyhadmin.domain.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductActionBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 批次ID
     */
    private Long actionId;
    /**
     * 商品id
     */
    private Long productId;

    private Integer type;

    private List<Long> productIds;
}
