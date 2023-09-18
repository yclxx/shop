package com.ruoyi.zlyyhmobile.domain.bo;

import lombok.Data;

@Data
public class UnionPayCreateBo {
    /**
     * 交易类型
     */
    private String bizMethod;
    /**
     * 交易时间
     */
    private String txnTime;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 清算日期 根据内容提供方要求，涉及清算的交易可以填写，备用字段，暂不返回值
     */
    private String settleDt;
    /**
     * 商品编号
     */
    private String prodId;
    /**
     * 购买数量
     */
    private String purQty;
    /**
     * 发券账号类型
     */
    private String prodAstIdTp;
    /**
     * 发券账号
     */
    private String prodAstId;
    /**
     * 原始交易类型 (查询需要)
     */
    private String origBizMethod;
    /**
     * 原始交易时间 (查询需要)
     */
    private String origTxnTime;
    /**
     * 原始交易订单号 (查询需要)
     */
    private String origOrderId;
    /**
     * 券码 （退券需要）
     */
    private String bondNo;
    /**
     * 券密 （退券需要）
     */
    private String bondEncNo;
}
