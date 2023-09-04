package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.vo.OrderAndUserNumber;
import com.ruoyi.zlyyh.domain.vo.OrderVo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 订单Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IOrderService {

    /**
     * 查询订单
     */
    OrderVo queryById(Long number);

    /**
     * 查询订单列表
     */
    TableDataInfo<OrderVo> queryPageList(OrderBo bo, PageQuery pageQuery);

    /**
     * 查询订单列表
     */
    List<OrderVo> queryList(OrderBo bo);

    /**
     * 修改订单
     */
    Boolean insertByBo(OrderBo bo);

    /**
     * 修改订单
     */
    Boolean updateByBo(OrderBo bo);

    /**
     * 校验并批量删除订单信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<OrderAndUserNumber> queryUserAndOrderNum(Date startDateTime, Date endDateTime, Integer indexNum, Integer indexPage);
}
