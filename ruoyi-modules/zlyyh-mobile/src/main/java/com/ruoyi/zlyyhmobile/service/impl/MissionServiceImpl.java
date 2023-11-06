package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.Mission;
import com.ruoyi.zlyyh.domain.bo.MissionBo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.mapper.MissionMapper;
import com.ruoyi.zlyyhmobile.service.IMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 任务配置Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@RequiredArgsConstructor
@Service
public class MissionServiceImpl implements IMissionService {

    private final MissionMapper baseMapper;

    @Cacheable(cacheNames = CacheNames.MISSION, key = "#missionId")
    @Override
    public MissionVo queryById(Long missionId) {
        return baseMapper.selectVoById(missionId);
    }

    /**
     * 查询任务配置列表
     */
    @Cacheable(cacheNames = CacheNames.MISSION_LIST, key = "#bo.getPlatformKey()+'-'+#bo.getSupportChannel()+'-'+#bo.showCity+'-'+#bo.showIndex+'-'+#bo.missionAffiliation+'-'+#bo.weekDate+'-'+#bo.getMissionGroupId()")
    @Override
    public List<MissionVo> queryList(MissionBo bo) {
        LambdaQueryWrapper<Mission> lqw = Wrappers.lambdaQuery();
        lqw.eq(Mission::getStatus, "0");
        lqw.eq(bo.getPlatformKey() != null, Mission::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getShowIndex()), Mission::getShowIndex, bo.getShowIndex());
        lqw.eq(StringUtils.isNotBlank(bo.getMissionType()), Mission::getMissionType, bo.getMissionType());
        lqw.eq(StringUtils.isNotBlank(bo.getMissionAffiliation()), Mission::getMissionAffiliation, bo.getMissionAffiliation());
        lqw.eq(null != bo.getMissionGroupId(), Mission::getMissionGroupId, bo.getMissionGroupId());
        if (StringUtils.isNotBlank(bo.getShowCity())) {
            lqw.and(lq -> lq.eq(Mission::getShowCity, "ALL").or().like(Mission::getShowCity, bo.getShowCity()));
        }
        if (StringUtils.isNotBlank(bo.getWeekDate())) {
            lqw.and(lq -> lq.eq(Mission::getAssignDate, "0").or().like(Mission::getWeekDate, bo.getWeekDate()));
        }
        lqw.and(lm ->
            lm.isNull(Mission::getShowStartDate).or().lt(Mission::getShowStartDate, new Date())
        );
        lqw.and(lm ->
            lm.isNull(Mission::getShowEndDate).or().gt(Mission::getShowEndDate, new Date())
        );
        if (StringUtils.isNotBlank(bo.getSupportChannel())) {
            lqw.and(lm -> lm.eq(Mission::getSupportChannel, "ALL").or().like(Mission::getSupportChannel, bo.getSupportChannel()));
        }
        lqw.last("order by sort asc");
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询银联任务组ID
     *
     * @return 任务组ID
     */
    @Override
    public Set<Long> queryMissionGroupIds() {
        LambdaQueryWrapper<Mission> lqw = Wrappers.lambdaQuery();
        lqw.eq(Mission::getStatus, "0");
        lqw.eq(Mission::getMissionType, "0");
        lqw.and(lm ->
            lm.isNull(Mission::getShowStartDate).or().lt(Mission::getShowStartDate, new Date())
        );
        lqw.and(lm ->
            lm.isNull(Mission::getShowEndDate).or().gt(Mission::getShowEndDate, new Date())
        );
        lqw.last("order by sort asc");
        List<MissionVo> missionVos = baseMapper.selectVoList(lqw);
        if (ObjectUtil.isEmpty(missionVos)) {
            return new HashSet<>(0);
        }
        return missionVos.stream().map(MissionVo::getMissionGroupId).collect(Collectors.toSet());
    }
}
