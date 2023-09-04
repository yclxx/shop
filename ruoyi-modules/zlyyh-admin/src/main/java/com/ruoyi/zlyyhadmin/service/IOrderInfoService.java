package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.OrderInfo;
import com.ruoyi.zlyyh.domain.vo.OrderInfoVo;
import com.ruoyi.zlyyh.domain.bo.OrderInfoBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 订单扩展信息Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IOrderInfoService {

    /**
     * 查询订单扩展信息
     */
    OrderInfoVo queryById(Long number);

    /**
     * 查询订单扩展信息列表
     */
    TableDataInfo<OrderInfoVo> queryPageList(OrderInfoBo bo, PageQuery pageQuery);

    /**
     * 查询订单扩展信息列表
     */
    List<OrderInfoVo> queryList(OrderInfoBo bo);

    /**
     * 修改订单扩展信息
     */
    Boolean insertByBo(OrderInfoBo bo);

    /**
     * 修改订单扩展信息
     */
    Boolean updateByBo(OrderInfoBo bo);

    /**
     * 校验并批量删除订单扩展信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
