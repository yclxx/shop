package com.ruoyi.zlyyhmobile.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayResultVo implements Serializable {
    /**
     * 支付信息
     */
    private String payData;
    /**
     * 支付前是否提示 Y提示
     */
    private String isPoup;
    /**
     * 支付前是否提示内容
     */
    private String poupText;
}
