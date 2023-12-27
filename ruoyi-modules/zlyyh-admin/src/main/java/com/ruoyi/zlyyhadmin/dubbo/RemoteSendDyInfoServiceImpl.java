package com.ruoyi.zlyyhadmin.dubbo;

import com.ruoyi.system.api.RemoteSendDyInfoService;
import com.ruoyi.zlyyhadmin.service.ISendDyInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteSendDyInfoServiceImpl implements RemoteSendDyInfoService {
    private final ISendDyInfoService sendDyInfoService;


    @Async
    @Override
    public void sendHuBeiDyInfo(String job) {
        if ("123".equals(job)){
            log.info("订阅定时任务可以正常执行");
            return;
        }
        sendDyInfoService.sendHuBeiDyInfo();
    }




}
