package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.OrderBackTransVo;

import java.util.Collection;
import java.util.List;

/**
 * 退款订单Service接口
 *
 * @author yzg
 * @date 2023-04-03
 */
public interface IOrderBackTransService {

    /**
     * 查询退款订单
     */
    OrderBackTransVo queryById(String thNumber);

    /**
     * 查询退款订单列表
     */
    TableDataInfo<OrderBackTransVo> queryPageList(OrderBackTransBo bo, PageQuery pageQuery);

    /**
     * 查询退款订单列表
     */
    List<OrderBackTransVo> queryList(OrderBackTransBo bo);

    /**
     * 修改退款订单
     */
    Boolean insertByBo(OrderBackTransBo bo, boolean refund);

    Boolean insertDirectByBo(OrderBackTransBo bo);

    /**
     * 修改退款历史订单
     */
    Boolean insertByBoHistory(OrderBackTransBo bo, boolean refund);
    /**
     * 修改退款历史订单（直接退款）
     */
    Boolean insertDirectByBoHistory(OrderBackTransBo bo);

    /**
     * 修改退款订单
     */
    Boolean updateByBo(OrderBackTransBo bo);

    /**
     * 校验并批量删除退款订单信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
