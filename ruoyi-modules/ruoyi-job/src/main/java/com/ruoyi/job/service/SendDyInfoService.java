package com.ruoyi.job.service;

import com.ruoyi.system.api.RemoteAppUserService;
import com.ruoyi.system.api.RemoteSendDyInfo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 订阅相关定时任务
 */
@Slf4j
@Service
public class SendDyInfoService {
    @DubboReference(retries = 0)
    private RemoteSendDyInfo remoteSendDyInfo;

    /**
     * 过期取消订单
     */
    @XxlJob("sendHuBeiDyInfo")
    public void userLog() {
        remoteSendDyInfo.sendHuBeiDyInfo();
    }
}
