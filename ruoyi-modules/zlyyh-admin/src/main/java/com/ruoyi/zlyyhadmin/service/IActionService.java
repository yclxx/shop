package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ActionBo;
import com.ruoyi.zlyyh.domain.vo.ActionVo;

import java.util.Collection;
import java.util.List;

/**
 * 优惠券批次Service接口
 *
 * @author yzg
 * @date 2023-10-12
 */
public interface IActionService {

    /**
     * 查询优惠券批次
     */
    ActionVo queryById(Long actionId);

    /**
     * 查询优惠券批次列表
     */
    TableDataInfo<ActionVo> queryPageList(ActionBo bo, PageQuery pageQuery);

    /**
     * 查询优惠券批次列表
     */
    List<ActionVo> queryList(ActionBo bo);

    /**
     * 修改优惠券批次
     */
    Boolean insertByBo(ActionBo bo);

    /**
     * 修改优惠券批次
     */
    Boolean updateByBo(ActionBo bo);

    /**
     * 校验并批量删除优惠券批次信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
