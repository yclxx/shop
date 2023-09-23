package com.ruoyi.zlyyhmobile.event;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 25487
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnionPayContentSendCouponCallbackEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 我方录入至银联的产品编号，一般就是产品ID
     */
    private String unionPayProductId;
    /**
     * 银联密钥
     */
    private String privateKey;
    /**
     * 原订单交易时间 发券时间
     */
    private String origTxnTime;
    /**
     * 原交易订单号 发券订单号
     */
    private String origOrderId;
    /**
     * 银联内容方AppId
     */
    private String appId;
    /**
     * 通知地址
     */
    private String callbackUrl;
    /**
     * 券码列表
     */
    private List<JSONObject> bondList;
    /**
     * 卡券类型：0-仅券码;1-券码+券密;2-短链;3- 直充
     */
    private String prodCertTp;
}
