package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.MissionGroupBgImg;
import com.ruoyi.zlyyh.domain.vo.MissionGroupBgImgVo;
import com.ruoyi.zlyyh.domain.bo.MissionGroupBgImgBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 任务组背景图片配置Service接口
 *
 * @author yzg
 * @date 2024-01-03
 */
public interface IMissionGroupBgImgService {

    /**
     * 查询任务组背景图片配置
     */
    MissionGroupBgImgVo queryById(Long missionGroupId);

    /**
     * 查询任务组背景图片配置列表
     */
    TableDataInfo<MissionGroupBgImgVo> queryPageList(MissionGroupBgImgBo bo, PageQuery pageQuery);

    /**
     * 查询任务组背景图片配置列表
     */
    List<MissionGroupBgImgVo> queryList(MissionGroupBgImgBo bo);

    /**
     * 修改任务组背景图片配置
     */
    Boolean insertByBo(MissionGroupBgImgBo bo);

    /**
     * 修改任务组背景图片配置
     */
    Boolean updateByBo(MissionGroupBgImgBo bo);

    /**
     * 校验并批量删除任务组背景图片配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
