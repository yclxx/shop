package com.ruoyi.zlyyh.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 25487
 */
@Data
public class CloudRechargeResult implements Serializable {
    /**
     * 状态码
     */
    private int code;

    /**
     * 返回内容
     */
    private String msg;

    /**
     * 数据对象
     */
    private String data;
}
