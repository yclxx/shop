package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.PromotionTaskBo;
import com.ruoyi.zlyyh.domain.vo.PromotionTaskVo;

import java.util.Collection;
import java.util.List;

/**
 * 推广任务Service接口
 *
 * @author yzg
 * @date 2023-11-16
 */
public interface IPromotionTaskService {

    /**
     * 查询推广任务
     */
    PromotionTaskVo queryById(Long taskId);

    /**
     * 查询推广任务列表
     */
    TableDataInfo<PromotionTaskVo> queryPageList(PromotionTaskBo bo, PageQuery pageQuery);

    /**
     * 查询推广任务列表
     */
    List<PromotionTaskVo> queryList(PromotionTaskBo bo);

    /**
     * 修改推广任务
     */
    Boolean insertByBo(PromotionTaskBo bo);

    /**
     * 修改推广任务
     */
    Boolean updateByBo(PromotionTaskBo bo);

    /**
     * 校验并批量删除推广任务信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
