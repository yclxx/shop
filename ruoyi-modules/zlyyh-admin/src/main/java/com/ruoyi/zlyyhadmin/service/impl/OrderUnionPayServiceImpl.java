package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.OrderUnionPay;
import com.ruoyi.zlyyh.domain.bo.OrderUnionPayBo;
import com.ruoyi.zlyyh.domain.vo.OrderUnionPayVo;
import com.ruoyi.zlyyh.mapper.OrderUnionPayMapper;
import com.ruoyi.zlyyhadmin.service.IOrderUnionPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 银联分销订单详情Service业务层处理
 *
 * @author yzg
 * @date 2023-08-22
 */
@RequiredArgsConstructor
@Service
public class OrderUnionPayServiceImpl implements IOrderUnionPayService {

    private final OrderUnionPayMapper baseMapper;

    /**
     * 查询银联分销订单详情
     */
    @Override
    public OrderUnionPayVo queryById(Long number){
        return baseMapper.selectVoById(number);
    }

    /**
     * 查询银联分销订单详情列表
     */
    @Override
    public TableDataInfo<OrderUnionPayVo> queryPageList(OrderUnionPayBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderUnionPay> lqw = buildQueryWrapper(bo);
        Page<OrderUnionPayVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询银联分销订单详情列表
     */
    @Override
    public List<OrderUnionPayVo> queryList(OrderUnionPayBo bo) {
        LambdaQueryWrapper<OrderUnionPay> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderUnionPay> buildQueryWrapper(OrderUnionPayBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderUnionPay> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getNumber() != null, OrderUnionPay::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderId()), OrderUnionPay::getOrderId, bo.getOrderId());
        lqw.eq(StringUtils.isNotBlank(bo.getProdTn()), OrderUnionPay::getProdTn, bo.getProdTn());
        lqw.eq(StringUtils.isNotBlank(bo.getTxnTime()), OrderUnionPay::getTxnTime, bo.getTxnTime());
        return lqw;
    }

    /**
     * 新增银联分销订单详情
     */
    @Override
    public Boolean insertByBo(OrderUnionPayBo bo) {
        OrderUnionPay add = BeanUtil.toBean(bo, OrderUnionPay.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setNumber(add.getNumber());
        }
        return flag;
    }

    /**
     * 修改银联分销订单详情
     */
    @Override
    public Boolean updateByBo(OrderUnionPayBo bo) {
        OrderUnionPay update = BeanUtil.toBean(bo, OrderUnionPay.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderUnionPay entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除银联分销订单详情
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
