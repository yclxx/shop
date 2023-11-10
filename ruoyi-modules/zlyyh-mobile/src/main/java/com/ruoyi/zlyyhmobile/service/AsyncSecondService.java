package com.ruoyi.zlyyhmobile.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.Mission;
import com.ruoyi.zlyyh.domain.bo.InviteUserLogBo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.mapper.MissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Async
@Component
@RequiredArgsConstructor
public class AsyncSecondService {
    private final IInviteUserLogService inviteUserLogService;
    private final MissionMapper missionMapper;
    private final String inviteBind = "inviteBind:";

    /**
     * 异步发送给邀请者抽奖机会（签到和购买时发送奖励）->创建订单时调用
     */
    public void sendInviteDraw(Long inviteUserId,Long platformId,Long missionGroupId,String channel,String cityName,String adCode) {
        //创建订单时先查询缓存
        List<MissionVo> missionVos = missionMapper.selectVoList(new LambdaQueryWrapper<Mission>().eq(Mission::getMissionGroupId, missionGroupId).eq(Mission::getMissionType, "2"));
        if (ObjectUtil.isEmpty(missionVos)){
            return;
        }
        for (MissionVo missionVo : missionVos) {
            String cacheKey = inviteBind + missionVo.getMissionId() + ":" + inviteUserId;
            //这个是邀请人Id
            Long cacheObject = RedisUtils.getCacheObject(cacheKey);
            //如果缓存不存在直接过滤
            if (ObjectUtil.isEmpty(cacheObject)){
                continue;
            }
            log.info("邀请任务检测开始"+"邀请人："+cacheObject+"被邀请人："+cacheKey);
            InviteUserLogBo inviteUserLogBo = new InviteUserLogBo();
            inviteUserLogBo.setUserId(cacheObject);
            inviteUserLogBo.setInviteUserId(inviteUserId);
            inviteUserLogBo.setPlatformKey(platformId);
            inviteUserLogBo.setMissionId(missionVo.getMissionId());
            inviteUserLogService.insertByBo(inviteUserLogBo,platformId, inviteUserLogBo.getUserId(),channel,cityName,adCode);
        }
    }
}
