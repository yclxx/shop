package com.ruoyi.system.api;

public interface RemoteSendDyInfoService {

    /**
     * 发送鄂U惠订阅信息
     */
    void sendHuBeiDyInfo(String job);

    /**
     * 发送微信订阅信息
     */
    void sendWxMsg(String job);

}
