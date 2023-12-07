package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.*;
import com.ruoyi.common.core.utils.reflect.ReflectUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.HistoryOrder;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderBackTrans;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.MerchantVo;
import com.ruoyi.zlyyh.domain.vo.OrderBackTransVo;
import com.ruoyi.zlyyh.domain.vo.OrderInfoVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.HistoryOrderMapper;
import com.ruoyi.zlyyh.mapper.OrderBackTransMapper;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.properties.WxProperties;
import com.ruoyi.zlyyh.utils.BigDecimalUtils;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyh.utils.WxUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.sdk.LogUtil;
import com.ruoyi.zlyyh.utils.sdk.PayUtils;
import com.ruoyi.zlyyhadmin.event.ShareOrderEvent;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final IUserService userService;
    private final WxProperties wxProperties;

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
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null, OrderBackTrans::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增退款订单
     */
    @Transactional
    @Override
    public Boolean insertByBo(OrderBackTransBo bo, boolean refund) {
        Order order = orderMapper.selectById(bo.getNumber());
        if (null == order.getOutAmount() || order.getOutAmount().signum() < 1) {
            // 有可能订单还没更新，休眠1秒
            ThreadUtil.sleep(2000);
            order = orderMapper.selectById(bo.getNumber());
        }
        // 退款检查
        try {
            checkOrderStatus(bo, order.getOutAmount(), order.getStatus(), refund);
        } catch (Exception e) {
            // 有可能订单还没更新，休眠1秒
            ThreadUtil.sleep(3000);
            order = orderMapper.selectById(bo.getNumber());
            try {
                checkOrderStatus(bo, order.getOutAmount(), order.getStatus(), refund);
            } catch (Exception ex) {
                checkOrderStatus(bo, order.getWantAmount(), order.getStatus(), refund);
            }
        }
        // 基本信息
        setBaseOrderBack(bo, order.getNumber(), order.getPickupMethod());
        order.setStatus("4");
        // 退款操作
        orderBack(bo, order);
        // 更改订单状态
        orderMapper.updateById(order);
        if ("9".equals(order.getOrderType())) {
            Order ob = new Order();
            ob.setStatus(order.getStatus());
            orderMapper.update(ob, new LambdaQueryWrapper<Order>().eq(Order::getParentNumber, order.getNumber()));
        }
        OrderBackTrans add = BeanUtil.toBean(bo, OrderBackTrans.class);
        PermissionUtils.setDeptIdAndUserId(add, order.getSysDeptId(), order.getSysUserId());
        return baseMapper.insert(add) > 0;
    }

    /**
     * 不判断状态直接退款
     */
    @Transactional
    @Override
    public Boolean insertDirectByBo(OrderBackTransBo bo) {
        return insertByBo(bo, true);
    }

    /**
     * 新增退款订单（历史订单）
     */
    @Transactional
    @Override
    public Boolean insertByBoHistory(OrderBackTransBo bo, boolean refund) {
        HistoryOrder order = historyOrderMapper.selectById(bo.getNumber());
        // 校验订单是否可退
        checkOrderStatus(bo, order.getOutAmount(), order.getStatus(), refund);
        // 基本信息
        setBaseOrderBack(bo, order.getNumber(), order.getPickupMethod());
        order.setStatus("4");
        // 退款操作
        orderBack(bo, order);
        // 更新订单状态
        historyOrderMapper.updateById(order);
        if ("9".equals(order.getOrderType())) {
            HistoryOrder ob = new HistoryOrder();
            ob.setStatus(order.getStatus());
            historyOrderMapper.update(ob, new LambdaQueryWrapper<HistoryOrder>().eq(HistoryOrder::getParentNumber, order.getNumber()));
        }
        OrderBackTrans add = BeanUtil.toBean(bo, OrderBackTrans.class);
        PermissionUtils.setDeptIdAndUserId(add, order.getSysDeptId(), order.getSysUserId());
        return baseMapper.insert(add) > 0;
    }

    /**
     * 不判断状态直接退款(历史订单)
     */
    @Transactional
    @Override
    public Boolean insertDirectByBoHistory(OrderBackTransBo bo) {
        return insertByBoHistory(bo, true);
    }

    /**
     * 修改退款订单
     */
    @Override
    public Boolean updateByBo(OrderBackTransBo bo) {
        OrderBackTrans update = BeanUtil.toBean(bo, OrderBackTrans.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除退款订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    private void setBaseOrderBack(OrderBackTransBo bo, Long number, String pickupMethod) {
        bo.setThNumber(IdUtils.getSnowflakeNextIdStr("T"));
        bo.setOrderBackTransState("0");
        bo.setNumber(number);
        bo.setPickupMethod(pickupMethod);
    }

    /**
     * 校验订单退款状态
     *
     * @param bo        退款信息
     * @param outAmount 退款金额
     * @param status    订单状态
     */
    private void checkOrderStatus(OrderBackTransBo bo, BigDecimal outAmount, String status, boolean refund) {
        if (bo.getRefund().compareTo(outAmount) > 0) {
            log.info("订单退款金额：{}，订单实际支付金额：{}", bo.getRefund(), outAmount);
            throw new ServiceException("退款金额不能超出订单金额");
        }
        if (!refund && !"2".equals(status)) {
            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
        }
    }

    /**
     * 积点兑换退款
     *
     * @param bo  退款信息
     * @param obj 订单信息
     */
    private void merberPointRefund(OrderBackTransBo bo, Object obj) {
        //2.积点兑换
        Long userId = ReflectUtils.invokeGetter(obj, "userId");
        Long platformKey = ReflectUtils.invokeGetter(obj, "platformKey");
        Long number = ReflectUtils.invokeGetter(obj, "number");
        String productName = ReflectUtils.invokeGetter(obj, "productName");

        UserVo userVo = userService.queryById(userId);
        if (ObjectUtil.isEmpty(userVo)) {
            throw new ServiceException("用户不存在，请联系客服处理");
        }
        R<Void> result = YsfUtils.memberPointAcquire(number, Integer.toUnsignedLong(bo.getRefund().intValue()), "1", productName + "退款", userVo.getOpenId(), "1", platformKey);
        if (result.getCode() == 200) {
            bo.setPickupMethod("2");
            bo.setSuccessTime(DateUtils.getNowDate());
            bo.setOrderBackTransState("2");
            ReflectUtils.invokeSetter(obj, "status", "5");
        } else {
            bo.setOrderBackTransState("1");
            ReflectUtils.invokeSetter(obj, "status", "6");
        }
    }

    /**
     * 订单退款
     *
     * @param bo  退款信息
     * @param obj 订单信息
     */
    private void orderBack(OrderBackTransBo bo, Object obj) {
        Order order = null;
        HistoryOrder historyOrder = null;
        if (obj instanceof HistoryOrder) {
            historyOrder = (HistoryOrder) obj;
        } else if (obj instanceof Order) {
            order = (Order) obj;
        } else {
            throw new ServiceException("订单异常obj");
        }
        String pickupMethod = null == order ? historyOrder.getPickupMethod() : order.getPickupMethod();
        Long payMerchant = null == order ? historyOrder.getPayMerchant() : order.getPayMerchant();
        //1.付费领取
        if ("1".equals(pickupMethod)) {
            MerchantVo merchantVo = merchantService.queryById(payMerchant);
            if (null == merchantVo) {
                throw new ServiceException("商户不存在，请联系客服处理");
            }
            if ("0".equals(merchantVo.getMerchantType())) {
                // 云闪付退款
                ysfRefund(bo, obj, merchantVo);
            } else if ("1".equals(merchantVo.getMerchantType())) {
                // 微信退款
                wxRefund(bo, obj, merchantVo);
            } else {
                throw new ServiceException("商户号错误");
            }
        } else if ("2".equals(pickupMethod)) {
            merberPointRefund(bo, obj);
        }
        if (null != order) {
            SpringUtils.context().publishEvent(new ShareOrderEvent(null, order.getNumber()));
        }
        if (null != historyOrder) {
            SpringUtils.context().publishEvent(new ShareOrderEvent(null, historyOrder.getNumber()));
        }
    }

    /**
     * 微信订单退款
     *
     * @param bo         退款信息
     * @param obj        订单信息
     * @param merchantVo 商户号信息
     */
    private void wxRefund(OrderBackTransBo bo, Object obj, MerchantVo merchantVo) {
        String refundCallbackUrl = merchantVo.getRefundCallbackUrl();
        if (!refundCallbackUrl.contains(merchantVo.getId().toString())) {
            refundCallbackUrl = refundCallbackUrl + "/" + merchantVo.getId();
        }
        BigDecimal outAmount = ReflectUtils.invokeGetter(obj, "outAmount");
        Integer amountTotal = BigDecimalUtils.toMinute(outAmount);
        if (amountTotal < 1) {
            outAmount = ReflectUtils.invokeGetter(obj, "wantAmount");
            amountTotal = BigDecimalUtils.toMinute(outAmount);
        }
        if (amountTotal < 1) {
            throw new ServiceException("退款金额错误");
        }
        String s;
        try {
            Long oldNumber = ReflectUtils.invokeGetter(obj, "collectiveNumber");
            if (null == oldNumber) {
                oldNumber = bo.getNumber();
            }
            s = WxUtils.wxRefund(oldNumber.toString(), bo.getThNumber(), wxProperties.getRefundUrl(), merchantVo.getMerchantNo(), null, BigDecimalUtils.toMinute(bo.getRefund()), amountTotal, refundCallbackUrl, merchantVo.getCertPath(), merchantVo.getMerchantKey(), merchantVo.getApiKey());
        } catch (Exception e) {
            log.error("微信退款异常：", e);
            throw new ServiceException("退款异常");
        }
        if (StringUtils.isBlank(s)) {
            throw new ServiceException("请求失败，请联系管理员处理");
        }
        log.info("订单：{}，微信退款返回数据：{}", bo.getNumber(), s);
        Map<String, Object> result = JsonUtils.parseMap(s);
        if (null == result) {
            throw new ServiceException("请求失败，请联系管理员处理");
        }
        // 微信退款单号
        String refund_id = (String) result.get("refund_id");
        // 退款成功时间
        String success_time = (String) result.get("success_time");
        // 退款受理时间
        String create_time = (String) result.get("create_time");
        // 退款状态
        // SUCCESS：退款成功
        // CLOSED：退款关闭
        // PROCESSING：退款处理中
        // ABNORMAL：退款异常
        String status = (String) result.get("status");
        // 优惠退款信息
        Object promotion_detail = result.get("promotion_detail");
        // 金额信息
        Map<String, Object> map = BeanUtil.beanToMap(result.get("amount"));
        // 新增退款订单信息
        bo.setRefundId(refund_id);
        if (("SUCCESS").equals(status)) {
            // 退款成功
            bo.setPickupMethod("1");
            bo.setOrderBackTransState("2");
            bo.setSuccessTime(DateUtils.getNowDate());
            ReflectUtils.invokeSetter(obj, "status", "5");
        } else if (!("PROCESSING").equals(status)) {
            bo.setOrderBackTransState("1");
            ReflectUtils.invokeSetter(obj, "status", "6");
        }
    }

    /**
     * 云闪付订单退款
     *
     * @param bo         退款信息
     * @param obj        订单信息
     * @param merchantVo 商户号信息
     */
    private void ysfRefund(OrderBackTransBo bo, Object obj, MerchantVo merchantVo) {
        OrderInfoVo orderInfoVo = orderInfoService.queryById(bo.getNumber());
        Map<String, String> result = PayUtils.backTransReq(orderInfoVo.getQueryId(), BigDecimalUtils.toMinute(bo.getRefund()).toString(), bo.getThNumber(), merchantVo.getRefundCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), orderInfoVo.getIssAddnData());
        if (null == result) {
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
            ReflectUtils.invokeSetter(obj, "status", "5");
        } else if (!("03").equals(respCode) && !("04").equals(respCode) && !("05").equals(respCode)) {
            LogUtil.writeLog("【" + bo.getNumber() + "】退款失败，应答信息respCode=" + respCode + ",respMsg=" + result.get("respMsg"));
            //其他应答码为失败请排查原因
            bo.setOrderBackTransState("1");
            ReflectUtils.invokeSetter(obj, "status", "6");
        }
    }
}
