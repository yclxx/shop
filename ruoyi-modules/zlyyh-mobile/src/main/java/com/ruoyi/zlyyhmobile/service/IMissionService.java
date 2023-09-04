package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.MissionBo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;

import java.util.List;
import java.util.Set;

/**
 * 任务配置Service接口
 *
 * @author yzg
 * @date 2023-05-10
 */
public interface IMissionService {

    /**
     * 查询任务配置列表
     */
    List<MissionVo> queryList(MissionBo bo);

    MissionVo queryById(Long missionId);

    /**
     * 查询银联任务组ID
     *
     * @return 任务组ID
     */
    Set<Long> queryMissionGroupIds();
}
