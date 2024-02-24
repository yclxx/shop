package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.UnionpayMissionProgress;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionProgressVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionProgressBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 银联任务进度Service接口
 *
 * @author yzg
 * @date 2024-02-22
 */
public interface IUnionpayMissionProgressService {

    /**
     * 查询银联任务进度
     */
    UnionpayMissionProgressVo queryById(Long progressId);

    /**
     * 查询银联任务进度列表
     */
    TableDataInfo<UnionpayMissionProgressVo> queryPageList(UnionpayMissionProgressBo bo, PageQuery pageQuery);

    /**
     * 查询银联任务进度列表
     */
    List<UnionpayMissionProgressVo> queryList(UnionpayMissionProgressBo bo);

    /**
     * 修改银联任务进度
     */
    Boolean insertByBo(UnionpayMissionProgressBo bo);

    /**
     * 修改银联任务进度
     */
    Boolean updateByBo(UnionpayMissionProgressBo bo);

    /**
     * 校验并批量删除银联任务进度信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
