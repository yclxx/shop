package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.OrderUnionPayBo;
import com.ruoyi.zlyyh.domain.vo.OrderUnionPayVo;

import java.util.Collection;
import java.util.List;

/**
 * 银联分销订单详情Service接口
 *
 * @author yzg
 * @date 2023-08-09
 */
public interface IOrderUnionPayService {

    /**
     * 查询银联分销订单详情
     */
    OrderUnionPayVo queryById(Long number);

    /**
     * 查询银联分销订单详情列表
     */
    TableDataInfo<OrderUnionPayVo> queryPageList(OrderUnionPayBo bo, PageQuery pageQuery);

    /**
     * 查询银联分销订单详情列表
     */
    List<OrderUnionPayVo> queryList(OrderUnionPayBo bo);

    /**
     * 修改银联分销订单详情
     */
    Boolean insertByBo(OrderUnionPayBo bo);

    /**
     * 修改银联分销订单详情
     */
    Boolean updateByBo(OrderUnionPayBo bo);

    /**
     * 校验并批量删除银联分销订单详情信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
