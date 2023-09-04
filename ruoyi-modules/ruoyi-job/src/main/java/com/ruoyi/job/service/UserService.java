package com.ruoyi.job.service;

import com.ruoyi.system.api.RemoteAppUserService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 用户相关定时任务
 */
@Slf4j
@Service
public class UserService {

    @DubboReference(retries = 0)
    private RemoteAppUserService remoteAppUserService;

    /**
     * 过期取消订单
     */
    @XxlJob("userLog")
    public void userLog() {
        try {
            remoteAppUserService.userLog();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

}
