package com.ruoyi.zlyyhmobile.event;

import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.vo.MissionUserRecordVo;
import com.ruoyi.zlyyh.mapper.MissionUserRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步调用
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CacheMissionRecordEventListener {
    private final MissionUserRecordMapper missionUserRecordMapper;

    /**
     * 发券
     */
    @Async
    @EventListener
    public void cacheMissionRecord(CacheMissionRecordEvent event) {
        MissionUserRecordVo missionUserRecordVo = missionUserRecordMapper.selectVoById(event.getMissionUserRecordId());
        if (null == missionUserRecordVo || "9".equals(missionUserRecordVo.getDrawType()) || !"1".equals(missionUserRecordVo.getStatus()) || StringUtils.isBlank(missionUserRecordVo.getSendAccount()) || StringUtils.isBlank(missionUserRecordVo.getDrawName())) {
            return;
        }
        String key = CacheNames.recordList + ":" + missionUserRecordVo.getMissionGroupId();
        if (RedisUtils.isExistsObject(key)) {
            int cacheListSize = RedisUtils.getCacheListSize(key);
            RedisUtils.setCacheList(key, 0, missionUserRecordVo);
            if (cacheListSize > 50) {
                RedisUtils.delCacheList(key, 50);
            }
        }
    }
}
