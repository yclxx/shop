package com.ruoyi.zlyyhmobile.dubbo;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.RemoteMissionService;
import com.ruoyi.zlyyh.domain.UnionpayMissionUser;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionUserBo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserVo;
import com.ruoyi.zlyyh.mapper.UnionpayMissionUserMapper;
import com.ruoyi.zlyyhmobile.service.IUnionpayMissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteMissionServiceImpl implements RemoteMissionService {
    private final UnionpayMissionUserMapper unionpayMissionUserMapper;
    private final IUnionpayMissionService unionpayMissionService;

    /**
     * 定时查询任务进度
     */
    @Async
    @Override
    public void queryUserMissionProgress(String jobParam) {
        log.info("任务组id：{}",jobParam);
        if (StringUtils.isEmpty(jobParam)) {
            log.error("任务组id参数为空");
            return;
        }
        List<UnionpayMissionUserVo> missionUserVos = unionpayMissionUserMapper.selectVoList(new LambdaQueryWrapper<UnionpayMissionUser>().eq(UnionpayMissionUser::getUpMissionGroupId, jobParam).eq(UnionpayMissionUser::getStatus, "0"));
        log.info("任务用户数量：{}",missionUserVos.size());
        if (ObjectUtil.isNotEmpty(missionUserVos)) {
            for (UnionpayMissionUserVo missionUserVo : missionUserVos) {
                UnionpayMissionUserBo bo = new UnionpayMissionUserBo();
                bo.setUpMissionGroupId(missionUserVo.getUpMissionGroupId());
                bo.setUserId(missionUserVo.getUserId());
                bo.setPlatformKey(missionUserVo.getPlatformKey());
                unionpayMissionService.getMissionProgress(bo);
            }
        }
    }
}
