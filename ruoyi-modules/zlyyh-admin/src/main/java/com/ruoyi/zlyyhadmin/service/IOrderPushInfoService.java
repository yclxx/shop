package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.OrderPushInfo;
import com.ruoyi.zlyyh.domain.vo.OrderPushInfoVo;
import com.ruoyi.zlyyh.domain.bo.OrderPushInfoBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 订单取码记录Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IOrderPushInfoService {

    /**
     * 查询订单取码记录
     */
    OrderPushInfoVo queryById(Long id);

    /**
     * 查询订单取码记录列表
     */
    TableDataInfo<OrderPushInfoVo> queryPageList(OrderPushInfoBo bo, PageQuery pageQuery);

    /**
     * 查询订单取码记录列表
     */
    List<OrderPushInfoVo> queryList(OrderPushInfoBo bo);

    /**
     * 修改订单取码记录
     */
    Boolean insertByBo(OrderPushInfoBo bo);

    /**
     * 修改订单取码记录
     */
    Boolean updateByBo(OrderPushInfoBo bo);

    /**
     * 校验并批量删除订单取码记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
