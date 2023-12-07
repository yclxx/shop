package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.PromotionLogBo;
import com.ruoyi.zlyyh.domain.vo.PromotionLogVo;

import java.util.Collection;
import java.util.List;

/**
 * 推广任务记录Service接口
 *
 * @author yzg
 * @date 2023-11-22
 */
public interface IPromotionLogService {

    /**
     * 查询推广任务记录
     */
    PromotionLogVo queryById(Long id);

    /**
     * 查询推广任务记录列表
     */
    TableDataInfo<PromotionLogVo> queryPageList(PromotionLogBo bo, PageQuery pageQuery);

    /**
     * 查询推广任务记录列表
     */
    List<PromotionLogVo> queryList(PromotionLogBo bo);

    /**
     * 修改推广任务记录
     */
    Boolean insertByBo(PromotionLogBo bo);

    /**
     * 修改推广任务记录
     */
    Boolean updateByBo(PromotionLogBo bo);

    /**
     * 校验并批量删除推广任务记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
