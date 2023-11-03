package com.ruoyi.zlyyhmobile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步任务
 *
 * @author 25487
 */
@Slf4j
@Async
@Component
@RequiredArgsConstructor
public class AsyncService {
    private final IMissionUserRecordService missionUserRecordService;

    /**
     * 异步发奖
     *
     * @param missionUserRecordId 需要发放的抽奖记录
     */
    public void sendDraw(Long missionUserRecordId) {
        missionUserRecordService.sendDraw(missionUserRecordId);
    }
    /**
     * 异步发送给邀请者抽奖机会（签到和购买时发送奖励）
     */
    public void sendInviteDraw() {
        //先查询缓存



    }
}
