package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.OrderUnionSendBo;
import com.ruoyi.zlyyh.domain.vo.OrderUnionSendVo;

import java.util.Collection;
import java.util.List;

/**
 * 银联分销订单卡券Service接口
 *
 * @author yzg
 * @date 2023-08-22
 */
public interface IOrderUnionSendService {

    /**
     * 查询银联分销订单卡券
     */
    OrderUnionSendVo queryById(Long number);

    /**
     * 查询银联分销订单卡券列表
     */
    TableDataInfo<OrderUnionSendVo> queryPageList(OrderUnionSendBo bo, PageQuery pageQuery);

    /**
     * 查询银联分销订单卡券列表
     */
    List<OrderUnionSendVo> queryList(OrderUnionSendBo bo);

    /**
     * 修改银联分销订单卡券
     */
    Boolean insertByBo(OrderUnionSendBo bo);

    /**
     * 修改银联分销订单卡券
     */
    Boolean updateByBo(OrderUnionSendBo bo);

    /**
     * 校验并批量删除银联分销订单卡券信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
