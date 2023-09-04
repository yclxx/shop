package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderBackTrans;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.OrderBackTransMapper;
import com.ruoyi.zlyyh.mapper.OrderInfoMapper;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.utils.BigDecimalUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.sdk.LogUtil;
import com.ruoyi.zlyyh.utils.sdk.PayUtils;
import com.ruoyi.zlyyhmobile.service.IMerchantService;
import com.ruoyi.zlyyhmobile.service.IOrderBackTransService;
import com.ruoyi.zlyyhmobile.service.IProductService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final OrderMapper orderMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final IMerchantService merchantService;
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
     * 退款回调业务 云闪付
     *
     * @param data 通知内容
     */
    @Transactional
    @Override
    public void refundCallBack(Map<String, String> data) {
        // 处理业务,退款一定是要等到相关产品作废或已退回才发起申请，所以这里不做任何其他处理
        // 退货交易的交易流水号，供查询用
        String queryId = data.get("queryId");
        // 	交易传输时间
        String traceTime = data.get("traceTime");
        // 交易金额，单位分
        String txnAmt = data.get("txnAmt");
        // 转换单位为元
        BigDecimal txnAmount = (new BigDecimal(txnAmt)).multiply(new BigDecimal("0.01"));
        // 系统跟踪号
        String traceNo = data.get("traceNo");
        // 记录订单发送时间	 商户代码merId、商户订单号orderId、订单发送时间txnTime三要素唯一确定一笔交易。
        String txnTime = data.get("txnTime");
        // 商户订单号
        String orderId = data.get("orderId");
        // 原交易查询流水号
        String origQryId = data.get("origQryId");
        // 查询退款订单是否存在
        OrderBackTrans orderBackTrans = baseMapper.selectById(orderId);
        if (ObjectUtil.isEmpty(orderBackTrans)) {
            log.info("银联退款回调订单【" + orderId + "】不存在，请核实");
            return;
        }
        orderBackTrans.setTraceNo(traceNo);
        orderBackTrans.setTraceTime(traceTime);
        orderBackTrans.setOrderBackTransState("2");

        baseMapper.updateById(orderBackTrans);
        // 修改订单信息
        OrderVo orderVo = orderMapper.selectVoById(orderBackTrans.getNumber());
        if (ObjectUtil.isEmpty(orderVo)) {
            throw new ServiceException("订单不存在");
        }
        Order order = new Order();
        order.setNumber(orderVo.getNumber());
        order.setStatus("5");
        orderMapper.updateById(order);
        if ("9".equals(order.getOrderType())) {
            Order ob = new Order();
            ob.setStatus(order.getStatus());
            orderMapper.update(ob, new LambdaQueryWrapper<Order>().eq(Order::getParentNumber, order.getNumber()));
        }
    }

    @Override
    public Boolean insertByBo(OrderBackTransBo bo) {
        Order order = orderMapper.selectById(bo.getNumber());
        ProductVo productVo = productService.queryById(order.getProductId());
        if (bo.getRefund().compareTo(order.getOutAmount()) > 0) {
            throw new ServiceException("退款金额不能超出订单金额");
        }
        if (!"2".equals(order.getStatus())) {
            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
        }
//        if (!"3".equals(order.getSendStatus()) && !"5".equals(productType)){
//            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
//        }
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
            OrderInfoVo orderInfoVo = orderInfoMapper.selectVoById(bo.getNumber());
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
}
