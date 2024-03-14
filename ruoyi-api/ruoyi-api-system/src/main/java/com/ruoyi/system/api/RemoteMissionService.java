package com.ruoyi.system.api;

public interface RemoteMissionService {

    /**
     * 定时查询用户任务进度
     */
    void queryUserMissionProgress(String jobParam);
}
