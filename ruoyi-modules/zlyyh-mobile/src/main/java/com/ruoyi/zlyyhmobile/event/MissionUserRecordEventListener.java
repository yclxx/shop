package com.ruoyi.zlyyhmobile.event;

import com.ruoyi.zlyyhmobile.service.IMissionUserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步调用
 *
 * @author ruoyi
 */
@Component
public class MissionUserRecordEventListener {
    @Autowired
    private IMissionUserRecordService missionUserRecordService;

    /**
     * 保存发放数量
     */
    @Async
    @EventListener
    public void saveDrawCount(MissionUserRecordEvent missionUserRecordEvent) {
        missionUserRecordService.saveDrawCount(missionUserRecordEvent.getMissionUserRecord(), missionUserRecordEvent.getCacheTime());
    }
}
