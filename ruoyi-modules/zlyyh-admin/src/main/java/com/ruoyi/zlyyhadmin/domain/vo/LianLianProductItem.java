package com.ruoyi.zlyyhadmin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LianLianProductItem implements Serializable {
    private Long itemId;// 套餐编号
    private String subTitle;// 套餐名称
    private BigDecimal salePrice;// 售价(分)
    private BigDecimal originPrice;// 原价(分)
    private BigDecimal channelPrice;// 渠道结算价(分)
    private Long stock;// 库存
    private Long singleMax;// 最多购买
    private Long codeAmount;// 发码数量
}
