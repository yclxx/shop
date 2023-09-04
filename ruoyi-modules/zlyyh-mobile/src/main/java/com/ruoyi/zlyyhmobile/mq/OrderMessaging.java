package com.ruoyi.zlyyhmobile.mq;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Lion Li
 */
@Data
@Accessors(chain = true)
public class OrderMessaging {
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 消息内容
     */
    private String msgText;
}
