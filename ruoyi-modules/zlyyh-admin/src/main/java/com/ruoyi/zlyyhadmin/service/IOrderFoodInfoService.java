package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.vo.OrderFoodInfoVo;
import com.ruoyi.zlyyh.domain.bo.OrderFoodInfoBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 美食套餐详细订单Service接口
 *
 * @author yzg
 * @date 2023-05-15
 */
public interface IOrderFoodInfoService {

    /**
     * 查询美食套餐详细订单
     */
    OrderFoodInfoVo queryById(Long number);

    /**
     * 查询美食套餐详细订单列表
     */
    TableDataInfo<OrderFoodInfoVo> queryPageList(OrderFoodInfoBo bo, PageQuery pageQuery);

    /**
     * 查询美食套餐详细订单列表
     */
    List<OrderFoodInfoVo> queryList(OrderFoodInfoBo bo);

    /**
     * 修改美食套餐详细订单
     */
    Boolean insertByBo(OrderFoodInfoBo bo);

    /**
     * 修改美食套餐详细订单
     */
    Boolean updateByBo(OrderFoodInfoBo bo);

    /**
     * 校验并批量删除美食套餐详细订单信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
