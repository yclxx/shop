package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.HistoryOrder;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderBackTrans;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.HistoryOrderMapper;
import com.ruoyi.zlyyh.mapper.OrderBackTransMapper;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.utils.BigDecimalUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.sdk.LogUtil;
import com.ruoyi.zlyyh.utils.sdk.PayUtils;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 退款订单Service业务层处理
 *
 * @author yzg
 * @date 2023-04-03
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderBackTransServiceImpl implements IOrderBackTransService {

    private final OrderBackTransMapper baseMapper;
    private final IOrderInfoService orderInfoService;
    private final IMerchantService merchantService;
    private final OrderMapper orderMapper;
    private final HistoryOrderMapper historyOrderMapper;
    private final IHistoryOrderInfoService historyOrderInfoService;
    private final IProductService productService;
    private final IUserService userService;

    /**
     * 查询退款订单
     */
    @Override
    public OrderBackTransVo queryById(String thNumber) {
        return baseMapper.selectVoById(thNumber);
    }

    /**
     * 查询退款订单列表
     */
    @Override
    public TableDataInfo<OrderBackTransVo> queryPageList(OrderBackTransBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderBackTrans> lqw = buildQueryWrapper(bo);
        Page<OrderBackTransVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询退款订单列表
     */
    @Override
    public List<OrderBackTransVo> queryList(OrderBackTransBo bo) {
        LambdaQueryWrapper<OrderBackTrans> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderBackTrans> buildQueryWrapper(OrderBackTransBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderBackTrans> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getNumber() != null, OrderBackTrans::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getPickupMethod()), OrderBackTrans::getPickupMethod, bo.getPickupMethod());
        lqw.eq(bo.getRefund() != null, OrderBackTrans::getRefund, bo.getRefund());
        lqw.eq(StringUtils.isNotBlank(bo.getRefundId()), OrderBackTrans::getRefundId, bo.getRefundId());
        lqw.eq(StringUtils.isNotBlank(bo.getChannel()), OrderBackTrans::getChannel, bo.getChannel());
        lqw.eq(StringUtils.isNotBlank(bo.getUserReceivedAccount()), OrderBackTrans::getUserReceivedAccount, bo.getUserReceivedAccount());
        lqw.eq(StringUtils.isNotBlank(bo.getTxnTime()), OrderBackTrans::getTxnTime, bo.getTxnTime());
        lqw.eq(StringUtils.isNotBlank(bo.getTraceTime()), OrderBackTrans::getTraceTime, bo.getTraceTime());
        lqw.eq(StringUtils.isNotBlank(bo.getQueryId()), OrderBackTrans::getQueryId, bo.getQueryId());
        lqw.eq(StringUtils.isNotBlank(bo.getOrigQryId()), OrderBackTrans::getOrigQryId, bo.getOrigQryId());
        lqw.eq(StringUtils.isNotBlank(bo.getTraceNo()), OrderBackTrans::getTraceNo, bo.getTraceNo());
        lqw.eq(bo.getSuccessTime() != null, OrderBackTrans::getSuccessTime, bo.getSuccessTime());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderBackTransState()), OrderBackTrans::getOrderBackTransState, bo.getOrderBackTransState());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            OrderBackTrans::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增退款订单
     */
    @Override
    public Boolean insertByBo(OrderBackTransBo bo, boolean refund) {
        Order order = orderMapper.selectById(bo.getNumber());
        ProductVo productVo = productService.queryById(order.getProductId());
        if (bo.getRefund().compareTo(order.getOutAmount()) > 0) {
            throw new ServiceException("退款金额不能超出订单金额");
        }
        if (!refund && !"2".equals(order.getStatus())) {
            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
        }
        MerchantVo merchantVo = null;
        if (!"2".equals(order.getPickupMethod())) {
            merchantVo = merchantService.queryById(order.getPayMerchant());
            if (ObjectUtil.isEmpty(merchantVo)) {
                throw new ServiceException("商户不存在，请联系客服处理");
            }
        }
        bo.setThNumber(IdUtils.getDateUUID("T"));
        bo.setOrderBackTransState("0");
        bo.setNumber(order.getNumber());
        order.setStatus("4");

        //1.付费领取
        if ("1".equals(order.getPickupMethod())) {
            OrderInfoVo orderInfoVo = orderInfoService.queryById(bo.getNumber());
            Map<String, String> result = PayUtils.backTransReq(orderInfoVo.getQueryId(), BigDecimalUtils.toMinute(bo.getRefund()).toString(), bo.getThNumber(), merchantVo.getRefundCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), orderInfoVo.getIssAddnData());
            if (ObjectUtil.isEmpty(result)) {
                throw new ServiceException("请求失败，请联系客服处理");
            }
            String respCode = result.get("respCode");
            String queryId = result.get("queryId");
            String txnTime = result.get("txnTime");
            String origQryId = result.get("origQryId");
            if (("00").equals(respCode)) {
                bo.setQueryId(queryId);
                bo.setOrigQryId(origQryId);
                bo.setTxnTime(txnTime);
                bo.setOrderBackTransState("2");
                bo.setPickupMethod("1");
                bo.setSuccessTime(DateUtils.getNowDate());
                order.setStatus("5");
            } else if (("03").equals(respCode) || ("04").equals(respCode) || ("05").equals(respCode)) {
                //后续需发起交易状态查询交易确定交易状态
            } else {
                LogUtil.writeLog("【" + bo.getNumber() + "】退款失败，应答信息respCode=" + respCode + ",respMsg=" + result.get("respMsg"));
                //其他应答码为失败请排查原因
                bo.setOrderBackTransState("1");
                order.setStatus("6");
            }
        } else if ("2".equals(order.getPickupMethod())) {
            //2.积点兑换
            UserVo userVo = userService.queryById(order.getUserId());
            if (ObjectUtil.isEmpty(userVo)) {
                throw new ServiceException("用户不存在，请联系客服处理");
            }
            R<Void> result = YsfUtils.memberPointAcquire(order.getNumber(), Integer.toUnsignedLong(bo.getRefund().intValue()), "1", productVo.getProductName() + "退款", userVo.getOpenId(), "1", order.getPlatformKey());
            if (result.getCode() == 200) {
                bo.setPickupMethod("2");
                bo.setSuccessTime(DateUtils.getNowDate());
                bo.setOrderBackTransState("2");
                order.setStatus("5");
            } else {
                bo.setOrderBackTransState("1");
                order.setStatus("6");
            }
        }
        orderMapper.updateById(order);
        if ("9".equals(order.getOrderType())) {
            Order ob = new Order();
            ob.setStatus(order.getStatus());
            orderMapper.update(ob, new LambdaQueryWrapper<Order>().eq(Order::getParentNumber, order.getNumber()));
        }
        OrderBackTrans add = BeanUtil.toBean(bo, OrderBackTrans.class);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 不判断状态直接退款
     */
    @Override
    public Boolean insertDirectByBo(OrderBackTransBo bo) {
        Order order = orderMapper.selectById(bo.getNumber());
        if (bo.getRefund().compareTo(order.getOutAmount()) > 0) {
            throw new ServiceException("退款金额不能超出订单金额");
        }
        if (!"2".equals(order.getStatus())) {
            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
        }
        MerchantVo merchantVo = null;
        if (!"2".equals(order.getPickupMethod())) {
            merchantVo = merchantService.queryById(order.getPayMerchant());
            if (ObjectUtil.isEmpty(merchantVo)) {
                throw new ServiceException("商户不存在，请联系客服处理");
            }
        }
        bo.setThNumber(IdUtils.getDateUUID("T"));
        bo.setOrderBackTransState("0");
        bo.setNumber(order.getNumber());
        order.setStatus("4");

        //1.付费领取
        if ("1".equals(order.getPickupMethod())) {
            OrderInfoVo orderInfoVo = orderInfoService.queryById(bo.getNumber());
            Map<String, String> result = PayUtils.backTransReq(orderInfoVo.getQueryId(), BigDecimalUtils.toMinute(bo.getRefund()).toString(), bo.getThNumber(), merchantVo.getRefundCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), orderInfoVo.getIssAddnData());
            if (ObjectUtil.isEmpty(result)) {
                throw new ServiceException("请求失败，请联系客服处理");
            }
            String respCode = result.get("respCode");
            String queryId = result.get("queryId");
            String txnTime = result.get("txnTime");
            String origQryId = result.get("origQryId");
            if (("00").equals(respCode)) {
                bo.setQueryId(queryId);
                bo.setOrigQryId(origQryId);
                bo.setTxnTime(txnTime);
                bo.setOrderBackTransState("2");
                bo.setPickupMethod("1");
                bo.setSuccessTime(DateUtils.getNowDate());
                order.setStatus("5");
            } else if (("03").equals(respCode) || ("04").equals(respCode) || ("05").equals(respCode)) {
                //后续需发起交易状态查询交易确定交易状态
            } else {
                LogUtil.writeLog("【" + bo.getNumber() + "】退款失败，应答信息respCode=" + respCode + ",respMsg=" + result.get("respMsg"));
                //其他应答码为失败请排查原因
                bo.setOrderBackTransState("1");
                order.setStatus("6");
            }
        } else if ("2".equals(order.getPickupMethod())) {
            //2.积点兑换
            UserVo userVo = userService.queryById(order.getUserId());
            if (ObjectUtil.isEmpty(userVo)) {
                throw new ServiceException("用户不存在，请联系客服处理");
            }
            R<Void> result = YsfUtils.memberPointAcquire(order.getNumber(), Integer.toUnsignedLong(bo.getRefund().intValue()), "1", order.getProductName() + "退款", userVo.getOpenId(), "1", order.getPlatformKey());
            if (result.getCode() == 200) {
                bo.setPickupMethod("2");
                bo.setSuccessTime(DateUtils.getNowDate());
                bo.setOrderBackTransState("2");
                order.setStatus("5");
            } else {
                bo.setOrderBackTransState("1");
                order.setStatus("6");
            }
        }
        orderMapper.updateById(order);
        OrderBackTrans add = BeanUtil.toBean(bo, OrderBackTrans.class);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 新增退款订单（历史订单）
     */
    @Override
    public Boolean insertByBoHistory(OrderBackTransBo bo, boolean refund) {
        HistoryOrder order = historyOrderMapper.selectById(bo.getNumber());
        ProductVo productVo = productService.queryById(order.getProductId());
        if (bo.getRefund().compareTo(order.getOutAmount()) > 0) {
            throw new ServiceException("退款金额不能超出订单金额");
        }
        if (!refund && !"2".equals(order.getStatus())) {
            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
        }
        MerchantVo merchantVo = null;
        if (!"2".equals(order.getPickupMethod())) {
            merchantVo = merchantService.queryById(order.getPayMerchant());
            if (ObjectUtil.isEmpty(merchantVo)) {
                throw new ServiceException("商户不存在，请联系客服处理");
            }
        }
        bo.setThNumber(IdUtils.getDateUUID("T"));
        bo.setOrderBackTransState("0");
        bo.setNumber(order.getNumber());
        order.setStatus("4");

        //1.付费领取
        if ("1".equals(order.getPickupMethod())) {
            HistoryOrderInfoVo orderInfoVo = historyOrderInfoService.queryById(bo.getNumber());
            Map<String, String> result = PayUtils.backTransReq(orderInfoVo.getQueryId(), BigDecimalUtils.toMinute(bo.getRefund()).toString(), bo.getThNumber(), merchantVo.getRefundCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), orderInfoVo.getIssAddnData());
            if (ObjectUtil.isEmpty(result)) {
                throw new ServiceException("请求失败，请联系客服处理");
            }
            String respCode = result.get("respCode");
            String queryId = result.get("queryId");
            String txnTime = result.get("txnTime");
            String origQryId = result.get("origQryId");
            if (("00").equals(respCode)) {
                bo.setQueryId(queryId);
                bo.setOrigQryId(origQryId);
                bo.setTxnTime(txnTime);
                bo.setOrderBackTransState("2");
                bo.setPickupMethod("1");
                bo.setSuccessTime(DateUtils.getNowDate());
                order.setStatus("5");
            } else if (("03").equals(respCode) || ("04").equals(respCode) || ("05").equals(respCode)) {
                //后续需发起交易状态查询交易确定交易状态
            } else {
                LogUtil.writeLog("【" + bo.getNumber() + "】退款失败，应答信息respCode=" + respCode + ",respMsg=" + result.get("respMsg"));
                //其他应答码为失败请排查原因
                bo.setOrderBackTransState("1");
                order.setStatus("6");
            }
        } else if ("2".equals(order.getPickupMethod())) {
            //2.积点兑换
            UserVo userVo = userService.queryById(order.getUserId());
            if (ObjectUtil.isEmpty(userVo)) {
                throw new ServiceException("用户不存在，请联系客服处理");
            }
            R<Void> result = YsfUtils.memberPointAcquire(order.getNumber(), Integer.toUnsignedLong(bo.getRefund().intValue()), "1", productVo.getProductName() + "退款", userVo.getOpenId(), "1", order.getPlatformKey());
            if (result.getCode() == 200) {
                bo.setPickupMethod("2");
                bo.setSuccessTime(DateUtils.getNowDate());
                bo.setOrderBackTransState("2");
                order.setStatus("5");
            } else {
                bo.setOrderBackTransState("1");
                order.setStatus("6");
            }
        }
        historyOrderMapper.updateById(order);
        if ("9".equals(order.getOrderType())) {
            HistoryOrder ob = new HistoryOrder();
            ob.setStatus(order.getStatus());
            historyOrderMapper.update(ob, new LambdaQueryWrapper<HistoryOrder>().eq(HistoryOrder::getParentNumber, order.getNumber()));
        }
        OrderBackTrans add = BeanUtil.toBean(bo, OrderBackTrans.class);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 不判断状态直接退款(历史订单)
     */
    @Override
    public Boolean insertDirectByBoHistory(OrderBackTransBo bo) {
        HistoryOrder order = historyOrderMapper.selectById(bo.getNumber());
        if (bo.getRefund().compareTo(order.getOutAmount()) > 0) {
            throw new ServiceException("退款金额不能超出订单金额");
        }
        if (!"2".equals(order.getStatus())) {
            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
        }
        MerchantVo merchantVo = null;
        if (!"2".equals(order.getPickupMethod())) {
            merchantVo = merchantService.queryById(order.getPayMerchant());
            if (ObjectUtil.isEmpty(merchantVo)) {
                throw new ServiceException("商户不存在，请联系客服处理");
            }
        }
        bo.setThNumber(IdUtils.getDateUUID("T"));
        bo.setOrderBackTransState("0");
        bo.setNumber(order.getNumber());
        order.setStatus("4");

        //1.付费领取
        if ("1".equals(order.getPickupMethod())) {
            HistoryOrderInfoVo orderInfoVo = historyOrderInfoService.queryById(bo.getNumber());
            Map<String, String> result = PayUtils.backTransReq(orderInfoVo.getQueryId(), BigDecimalUtils.toMinute(bo.getRefund()).toString(), bo.getThNumber(), merchantVo.getRefundCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), orderInfoVo.getIssAddnData());
            if (ObjectUtil.isEmpty(result)) {
                throw new ServiceException("请求失败，请联系客服处理");
            }
            String respCode = result.get("respCode");
            String queryId = result.get("queryId");
            String txnTime = result.get("txnTime");
            String origQryId = result.get("origQryId");
            if (("00").equals(respCode)) {
                bo.setQueryId(queryId);
                bo.setOrigQryId(origQryId);
                bo.setTxnTime(txnTime);
                bo.setOrderBackTransState("2");
                bo.setPickupMethod("1");
                bo.setSuccessTime(DateUtils.getNowDate());
                order.setStatus("5");
            } else if (("03").equals(respCode) || ("04").equals(respCode) || ("05").equals(respCode)) {
                //后续需发起交易状态查询交易确定交易状态
            } else {
                LogUtil.writeLog("【" + bo.getNumber() + "】退款失败，应答信息respCode=" + respCode + ",respMsg=" + result.get("respMsg"));
                //其他应答码为失败请排查原因
                bo.setOrderBackTransState("1");
                order.setStatus("6");
            }
        } else if ("2".equals(order.getPickupMethod())) {
            //2.积点兑换
            UserVo userVo = userService.queryById(order.getUserId());
            if (ObjectUtil.isEmpty(userVo)) {
                throw new ServiceException("用户不存在，请联系客服处理");
            }
            R<Void> result = YsfUtils.memberPointAcquire(order.getNumber(), Integer.toUnsignedLong(bo.getRefund().intValue()), "1", order.getProductName() + "退款", userVo.getOpenId(), "1", order.getPlatformKey());
            if (result.getCode() == 200) {
                bo.setPickupMethod("2");
                bo.setSuccessTime(DateUtils.getNowDate());
                bo.setOrderBackTransState("2");
                order.setStatus("5");
            } else {
                bo.setOrderBackTransState("1");
                order.setStatus("6");
            }
        }
        historyOrderMapper.updateById(order);
        OrderBackTrans add = BeanUtil.toBean(bo, OrderBackTrans.class);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 修改退款订单
     */
    @Override
    public Boolean updateByBo(OrderBackTransBo bo) {
        OrderBackTrans update = BeanUtil.toBean(bo, OrderBackTrans.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderBackTrans entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除退款订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
