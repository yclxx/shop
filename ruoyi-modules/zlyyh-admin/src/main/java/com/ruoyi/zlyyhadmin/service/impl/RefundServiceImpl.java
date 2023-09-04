package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.HistoryOrder;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.Refund;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.bo.RefundBo;
import com.ruoyi.zlyyh.domain.vo.OrderBackTransVo;
import com.ruoyi.zlyyh.domain.vo.RefundVo;
import com.ruoyi.zlyyh.mapper.HistoryOrderMapper;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.mapper.RefundMapper;
import com.ruoyi.zlyyhadmin.service.IOrderBackTransService;
import com.ruoyi.zlyyhadmin.service.IRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 退款订单登记Service业务层处理
 *
 * @author yzg
 * @date 2023-08-07
 */
@RequiredArgsConstructor
@Service
public class RefundServiceImpl implements IRefundService {

    private final RefundMapper baseMapper;
    private final OrderMapper orderMapper;
    private final HistoryOrderMapper historyOrderMapper;
    private final IOrderBackTransService orderBackTransService;

    /**
     * 查询退款订单登记
     */
    @Override
    public RefundVo queryById(Long refundId) {
        return baseMapper.selectVoById(refundId);
    }

    /**
     * 查询退款订单登记列表
     */
    @Override
    public TableDataInfo<RefundVo> queryPageList(RefundBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Refund> lqw = buildQueryWrapper(bo);
        Page<RefundVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询退款订单登记列表
     */
    @Override
    public List<RefundVo> queryList(RefundBo bo) {
        LambdaQueryWrapper<Refund> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Refund> buildQueryWrapper(RefundBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Refund> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getNumber() != null, Refund::getNumber, bo.getNumber());
        lqw.eq(bo.getRefundAmount() != null, Refund::getRefundAmount, bo.getRefundAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getRefundApplicant()), Refund::getRefundApplicant, bo.getRefundApplicant());
        lqw.eq(StringUtils.isNotBlank(bo.getRefundReviewer()), Refund::getRefundReviewer, bo.getRefundReviewer());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Refund::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getRefuseReason()), Refund::getRefuseReason, bo.getRefuseReason());
        lqw.eq(StringUtils.isNotBlank(bo.getRefundRemark()), Refund::getRefundRemark, bo.getRefundRemark());
        return lqw;
    }

    /**
     * 新增退款订单登记
     */
    @Override
    public Boolean insertByBo(RefundBo bo) {
        Refund add = BeanUtil.toBean(bo, Refund.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setRefundId(add.getRefundId());
        }
        return flag;
    }

    /**
     * 修改退款订单登记
     */
    @Override
    public Boolean updateByBo(RefundBo bo) {
        Refund update = BeanUtil.toBean(bo, Refund.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    @Transactional
    @Override
    public void agreeSubmit(Long refundId) {
        Refund refund = baseMapper.selectById(refundId);
        Order order = orderMapper.selectById(refund.getNumber());
        OrderBackTransBo orderBackTransBo = new OrderBackTransBo();
        orderBackTransBo.setNumber(refund.getNumber());
        List<OrderBackTransVo> orderBackTransVos = orderBackTransService.queryList(orderBackTransBo);
        if (ObjectUtil.isNotEmpty(orderBackTransVos)) {
            //如果有退款记录则返回已退款
            throw new ServiceException("订单已申请过退款");
        }
        if (ObjectUtil.isEmpty(order)) {
            //订单不存在 查询历史订单
            HistoryOrder historyOrder = historyOrderMapper.selectById(refund.getNumber());
            if (ObjectUtil.isEmpty(historyOrder)) {
                throw new ServiceException("订单不存在");
            }
            //历史订单进行退款
            orderBackTransBo.setRefund(historyOrder.getOutAmount());
            if ("5".equals(historyOrder.getOrderType())) {
                //美食订单 判断供应商是否已经退款
                if (!"1".equals(historyOrder.getCancelStatus())) {
                    throw new ServiceException("供应商未成功退款");
                }
            }
            refund.setStatus("1");
            baseMapper.updateById(refund);
            //进行 历史订单退款
            orderBackTransService.insertByBoHistory(orderBackTransBo, true);
            return;
        }
        orderBackTransBo.setRefund(order.getOutAmount());
        if ("5".equals(order.getOrderType())) {
            //美食订单 判断供应商是否已经退款
            if (!"1".equals(order.getCancelStatus())) {
                throw new ServiceException("供应商未成功退款");
            }
        }
        refund.setStatus("1");
        baseMapper.updateById(refund);
        //进行退款
        orderBackTransService.insertByBo(orderBackTransBo, true);
    }

    @Override
    public void refuseSubmit(Long refundId) {
        Refund refund = baseMapper.selectById(refundId);
        refund.setStatus("2");
        Order order = orderMapper.selectById(refund.getNumber());
        if (ObjectUtil.isEmpty(order)) {
            //订单不存在 查询历史订单
            HistoryOrder historyOrder = historyOrderMapper.selectById(refund.getNumber());
            if (ObjectUtil.isEmpty(historyOrder)) {
                throw new ServiceException("订单不存在");
            }
            historyOrder.setStatus("6");
            historyOrderMapper.updateById(historyOrder);
            baseMapper.updateById(refund);
            return;
        }
        //审核拒绝状态
        order.setStatus("6");
        orderMapper.updateById(order);
        baseMapper.updateById(refund);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Refund entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除退款订单登记
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
