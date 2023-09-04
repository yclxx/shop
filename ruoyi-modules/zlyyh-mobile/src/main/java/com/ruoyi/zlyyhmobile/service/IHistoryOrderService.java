package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.HistoryOrder;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.bo.HistoryOrderBo;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderVo;

import java.util.Collection;
import java.util.List;

/**
 * 历史订单Service接口
 *
 * @author yzg
 * @date 2023-08-01
 */
public interface IHistoryOrderService {

    /**
     * 查询历史订单
     */
    HistoryOrderVo queryById(Long number);

    /**
     * 查询历史订单列表
     */
    TableDataInfo<HistoryOrderVo> queryPageList(HistoryOrderBo bo, PageQuery pageQuery);


    void updateOrder(HistoryOrder historyOrder);

    /**
     * 历史订单退款
     * @param number
     * @param userId
     */
    public void historyOrderRefund(Long number, Long userId);

    /**
     * 订单迁移至历史订单
     */
    void orderToHistory();
}
