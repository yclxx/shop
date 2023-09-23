package com.ruoyi.zlyyhmobile.event;

import com.ruoyi.zlyyhmobile.enums.UnionPayCallbackBizMethodType;
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
public class UnionPayContentCallbackEvent implements Serializable {
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
     * 银联内容方AppId
     */
    private String appId;
    /**
     * 通知地址
     */
    private String callbackUrl;
    /**
     * 券码
     */
    private String bondNo;
    /**
     * 时间
     */
    private String procTime;

    private UnionPayCallbackBizMethodType bizMethodType;
}
