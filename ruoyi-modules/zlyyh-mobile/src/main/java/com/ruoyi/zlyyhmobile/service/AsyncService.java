package com.ruoyi.zlyyhmobile.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.Mission;
import com.ruoyi.zlyyh.domain.bo.InviteUserLogBo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.mapper.MissionMapper;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

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



}
