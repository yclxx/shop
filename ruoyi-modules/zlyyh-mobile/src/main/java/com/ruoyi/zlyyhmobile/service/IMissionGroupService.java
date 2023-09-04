package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.MissionGroupVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;

import java.util.List;

/**
 * 任务组Service接口
 *
 * @author yzg
 * @date 2023-05-10
 */
public interface IMissionGroupService {

    /**
     * 查询任务组
     */
    MissionGroupVo queryById(Long missionGroupId);

    List<ProductVo> missionProduct(Long missionGroupId);

    List<MissionGroupVo> queryList();
}
