package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容分销内容方订单对象 t_union_pay_content_order
 *
 * @author yzg
 * @date 2023-09-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_union_pay_content_order")
public class UnionPayContentOrder extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 银联发券订单号
     */
    private String unionPayOrderId;
    /**
     * 银联发券商品编号
     */
    private String unionPayProdId;
    /**
     * 银联发券交易时间
     */
    private String unionPayTxnTime;
    /**
     * 银联发券购买数量
     */
    private String unionPayPurQty;
    /**
     * 银联发券账号类型：0-手机号;1-qq号;2-微信号;3-其他类 型账号
     */
    private String unionPayProdAstIdTp;
    /**
     * 银联发券账号
     */
    private String unionPayProdAstId;
    /**
     * 银联发券状态
     */
    private String unionPayResultStatus;
    /**
     * 订单号
     */
    private Long number;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
