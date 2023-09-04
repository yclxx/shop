package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 平台信息对象 t_platform
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_platform")
public class Platform extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 平台标识
     */
    @TableId(value = "platform_key")
    private Long platformKey;
    /**
     * 平台名称
     */
    private String platformName;
    /**
     * 小程序标题
     */
    private String platformTitle;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * appId
     */
    private String appId;
    /**
     * 小程序ID
     */
    private String encryptAppId;
    /**
     * 密钥
     */
    private String secret;
    /**
     * 对称密钥
     */
    private String symmetricKey;
    /**
     * rsa签名私钥
     */
    private String rsaPrivateKey;
    /**
     * rsa签名公钥
     */
    private String rsaPublicKey;
    /**
     * 客服电话
     */
    private String serviceTel;
    /**
     * 客服服务时间
     */
    private String serviceTime;
    /**
     * 活动城市：ALL-全部、否则填城市行政区号，多个之间用英文逗号隔开
     */
    private String platformCity;
    /**
     * 默认支付商户
     */
    private Long merchantId;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
