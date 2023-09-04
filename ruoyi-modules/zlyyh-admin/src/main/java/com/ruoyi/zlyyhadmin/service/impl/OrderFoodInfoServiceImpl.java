package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.IOrderFoodInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.OrderFoodInfoBo;
import com.ruoyi.zlyyh.domain.vo.OrderFoodInfoVo;
import com.ruoyi.zlyyh.domain.OrderFoodInfo;
import com.ruoyi.zlyyh.mapper.OrderFoodInfoMapper;


import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 美食套餐详细订单Service业务层处理
 *
 * @author yzg
 * @date 2023-05-15
 */
@RequiredArgsConstructor
@Service
public class OrderFoodInfoServiceImpl implements IOrderFoodInfoService {

    private final OrderFoodInfoMapper baseMapper;

    /**
     * 查询美食套餐详细订单
     */
    @Override
    public OrderFoodInfoVo queryById(Long number){
        return baseMapper.selectVoById(number);
    }

    /**
     * 查询美食套餐详细订单列表
     */
    @Override
    public TableDataInfo<OrderFoodInfoVo> queryPageList(OrderFoodInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderFoodInfo> lqw = buildQueryWrapper(bo);
        Page<OrderFoodInfoVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询美食套餐详细订单列表
     */
    @Override
    public List<OrderFoodInfoVo> queryList(OrderFoodInfoBo bo) {
        LambdaQueryWrapper<OrderFoodInfo> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderFoodInfo> buildQueryWrapper(OrderFoodInfoBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderFoodInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getBizOrderId()), OrderFoodInfo::getBizOrderId, bo.getBizOrderId());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), OrderFoodInfo::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getTicketCode()), OrderFoodInfo::getTicketCode, bo.getTicketCode());
        lqw.eq(StringUtils.isNotBlank(bo.getVoucherId()), OrderFoodInfo::getVoucherId, bo.getVoucherId());
        lqw.eq(StringUtils.isNotBlank(bo.getVoucherStatus()), OrderFoodInfo::getVoucherStatus, bo.getVoucherStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getEffectTime()), OrderFoodInfo::getEffectTime, bo.getEffectTime());
        lqw.eq(StringUtils.isNotBlank(bo.getExpireTime()), OrderFoodInfo::getExpireTime, bo.getExpireTime());
        lqw.eq(bo.getTotalAmount() != null, OrderFoodInfo::getTotalAmount, bo.getTotalAmount());
        lqw.eq(bo.getUsedAmount() != null, OrderFoodInfo::getUsedAmount, bo.getUsedAmount());
        lqw.eq(bo.getRefundAmount() != null, OrderFoodInfo::getRefundAmount, bo.getRefundAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderStatus()), OrderFoodInfo::getOrderStatus, bo.getOrderStatus());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), OrderFoodInfo::getProductName, bo.getProductName());
        lqw.eq(StringUtils.isNotBlank(bo.getItemId()), OrderFoodInfo::getItemId, bo.getItemId());
        lqw.eq(bo.getSellingPrice() != null, OrderFoodInfo::getSellingPrice, bo.getSellingPrice());
        lqw.eq(bo.getOfficialPrice() != null, OrderFoodInfo::getOfficialPrice, bo.getOfficialPrice());
        return lqw;
    }

    /**
     * 新增美食套餐详细订单
     */
    @Override
    public Boolean insertByBo(OrderFoodInfoBo bo) {
        OrderFoodInfo add = BeanUtil.toBean(bo, OrderFoodInfo.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setNumber(add.getNumber());
        }
        return flag;
    }

    /**
     * 修改美食套餐详细订单
     */
    @Override
    public Boolean updateByBo(OrderFoodInfoBo bo) {
        OrderFoodInfo update = BeanUtil.toBean(bo, OrderFoodInfo.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderFoodInfo entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除美食套餐详细订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
