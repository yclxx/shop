package com.ruoyi.zlyyh.utils;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author 25487
 */
@Data
public class CloudRechargeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用ID
     */
    @NotNull(message = "应用ID不能为空")
    private String appId;

    /**
     * 当前时间戳
     */
    @NotNull(message = "时间戳不能为空")
    private String timestamp;

    /**
     * 签名
     */
    @NotBlank(message = "签名不能为空")
    private String sign;

    /**
     * 请求的数据(AES)加密后
     */
    @NotBlank(message = "请求的数据不能为空")
    private String encryptedData;

    private CloudRechargeResult cloudRechargeResult;
}
