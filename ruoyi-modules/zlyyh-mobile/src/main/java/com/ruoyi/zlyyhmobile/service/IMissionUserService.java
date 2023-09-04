package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.MissionUserBo;
import com.ruoyi.zlyyh.domain.vo.MissionUserVo;

/**
 * 任务用户Service接口
 *
 * @author yzg
 * @date 2023-05-10
 */
public interface IMissionUserService {

    /**
     * 查询任务用户
     */
    MissionUserVo queryById(Long missionUserId);

    /**
     * 查询任务用户
     */
    MissionUserVo queryByUserIdAndGroupId(Long missionGroupId, Long userId, Long platformKey);

    /**
     * 修改任务用户
     */
    Boolean insertByBo(MissionUserBo bo);

    /**
     * 查询任务用户列表
     */
    TableDataInfo<MissionUserVo> queryPageList(Long missionGroupId, PageQuery pageQuery);

}
