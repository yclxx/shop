package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.UnionpayMission;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 银联任务配置Service接口
 *
 * @author yzg
 * @date 2024-02-21
 */
public interface IUnionpayMissionService {

    /**
     * 查询银联任务配置
     */
    UnionpayMissionVo queryById(Long upMissionId);

    /**
     * 查询银联任务配置列表
     */
    TableDataInfo<UnionpayMissionVo> queryPageList(UnionpayMissionBo bo, PageQuery pageQuery);

    /**
     * 查询银联任务配置列表
     */
    List<UnionpayMissionVo> queryList(UnionpayMissionBo bo);

    /**
     * 修改银联任务配置
     */
    Boolean insertByBo(UnionpayMissionBo bo);

    /**
     * 修改银联任务配置
     */
    Boolean updateByBo(UnionpayMissionBo bo);

    /**
     * 校验并批量删除银联任务配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
