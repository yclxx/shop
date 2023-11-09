package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.CollectiveOrder;
import com.ruoyi.zlyyh.domain.vo.CollectiveOrderVo;
import com.ruoyi.zlyyh.domain.bo.CollectiveOrderBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 大订单Service接口
 *
 * @author yzg
 * @date 2023-10-16
 */
public interface ICollectiveOrderService {

    /**
     * 查询大订单
     */
    CollectiveOrderVo queryById(Long collectiveNumber);

    /**
     * 查询大订单列表
     */
    TableDataInfo<CollectiveOrderVo> queryPageList(CollectiveOrderBo bo, PageQuery pageQuery);

    void addCollectiveOrder();



}
