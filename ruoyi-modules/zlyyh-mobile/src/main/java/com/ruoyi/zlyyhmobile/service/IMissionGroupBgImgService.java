package com.ruoyi.zlyyhmobile.service;

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
 * @date 2024-01-04
 */
public interface IMissionGroupBgImgService {

    /**
     * 查询任务组背景图片配置
     */
    MissionGroupBgImgVo queryById(Long missionBgImgId);


    /**
     * 查询任务组背景图片配置列表
     */
    MissionGroupBgImgVo queryListOne(MissionGroupBgImgBo bo);


}
