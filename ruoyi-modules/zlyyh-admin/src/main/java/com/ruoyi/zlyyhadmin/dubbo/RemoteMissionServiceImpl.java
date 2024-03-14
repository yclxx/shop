package com.ruoyi.zlyyhadmin.dubbo;

import com.ruoyi.system.api.RemoteMissionService;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteMissionServiceImpl implements RemoteMissionService {
    private final IUnionpayMissionService unionpayMissionService;

    /**
     * 定时查询任务进度
     */
    @Async
    @Override
    public void queryUserMissionProgress(String jobParam) {

    }
}
