package com.ruoyi.job.service;

import com.ruoyi.system.api.RemoteMissionService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 银联任务相关定时任务
 */
@Slf4j
@Service
public class MissionService {
    @DubboReference(retries = 0)
    private RemoteMissionService remoteMissionService;

    /**
     * 定时查询用户任务进度
     */
    @XxlJob("queryUserMissionProgress")
    public void queryUserMissionProgress() {
        String jobParam = XxlJobHelper.getJobParam();
        remoteMissionService.queryUserMissionProgress(jobParam);
    }
}
