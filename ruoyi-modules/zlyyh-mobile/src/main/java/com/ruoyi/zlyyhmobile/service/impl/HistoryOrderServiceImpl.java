package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.HistoryOrderBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyh.properties.YsfFoodProperties;
import com.ruoyi.zlyyh.utils.BigDecimalUtils;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyh.utils.YsfFoodUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.sdk.LogUtil;
import com.ruoyi.zlyyh.utils.sdk.PayUtils;
import com.ruoyi.zlyyhmobile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 历史订单Service业务层处理
 *
 * @author yzg
 * @date 2023-08-01
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class HistoryOrderServiceImpl implements IHistoryOrderService {
    private static final YsfFoodProperties YSF_FOOD_PROPERTIES = SpringUtils.getBean(YsfFoodProperties.class);

    private final HistoryOrderMapper baseMapper;
    private final IUserService userService;
    private final IMerchantService merchantService;
    private final HistoryOrderInfoMapper historyOrderInfoMapper;
    private final OrderFoodInfoMapper orderFoodInfoMapper;
    private final HistoryOrderPushInfoMapper historyOrderPushInfoMapper;
    private final IOrderCardService orderCardService;
    private final IOrderService orderService;
    private final OrderInfoMapper orderInfoMapper;
    private final OrderPushInfoMapper orderPushInfoMapper;
    private final OrderMapper orderMapper;
    private final OrderBackTransMapper orderBackTransMapper;
    private final RefundMapper refundMapper;
    private final OrderUnionSendService orderUnionSendService;

    /**
     * 查询订单
     */
    @Override
    public HistoryOrderVo queryById(Long number) {
        HistoryOrderVo orderVo = baseMapper.selectVoById(number);

        //订单为美食订单加上info
        if ("1".equals(orderVo.getOrderType()) || "5".equals(orderVo.getOrderType())) {
            //调用接口查询美食订单
            orderFoodProcessed(orderVo);
        } else if ("7".equals(orderVo.getOrderType())) {
            if ("2".equals(orderVo.getStatus())) {
                List<OrderCardVo> orderCardVos = orderCardService.queryListByNumber(number);
                orderVo.setOrderCardVos(orderCardVos);
            }
        } else if ("11".equals(orderVo.getOrderType()) || "12".equals(orderVo.getOrderType())) {
            List<OrderUnionSendVo> orderUnionSendVos = orderUnionSendService.queryListByNumber(number);
            orderVo.setOrderUnionSendVos(orderUnionSendVos);
        }
        return orderVo;
    }

    /**
     * 口碑 订单进行处理
     *
     * @param order 订单信息
     */
    private void orderFoodProcessed(HistoryOrderVo order) {
        OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(order.getNumber());
        if (ObjectUtil.isNotEmpty(orderFoodInfoVo)) {
            //如果没有电子码
            if (("2".equals(order.getStatus()) || "4".equals(order.getStatus()) || "5".equals(order.getStatus())) && StringUtils.isEmpty(orderFoodInfoVo.getTicketCode())) {
                // 查询口碑订单
                queryFoodOrder(order.getExternalOrderNumber());
                //防止重复加密再查询一遍
                orderFoodInfoVo = orderFoodInfoMapper.selectVoById(order.getNumber());
            }
        }
        order.setOrderFoodInfoVo(orderFoodInfoVo);
    }

    /**
     * 查询美食订单接口
     */
    private void queryFoodOrder(String externalOrderNumber) {
        String appId = YSF_FOOD_PROPERTIES.getAppId();
        String rsaPrivateKey = YSF_FOOD_PROPERTIES.getRsaPrivateKey();
        String queryOrderUrl = YSF_FOOD_PROPERTIES.getQueryOrderUrl();
        HistoryOrder order = baseMapper.selectOne(new LambdaQueryWrapper<HistoryOrder>().eq(HistoryOrder::getExternalOrderNumber, externalOrderNumber));
        if (ObjectUtil.isEmpty(order)) {
            return;
        }
        HistoryOrderPushInfo orderPushInfo = historyOrderPushInfoMapper.selectOne(new LambdaQueryWrapper<HistoryOrderPushInfo>().eq(HistoryOrderPushInfo::getNumber, order.getNumber()));
        OrderFoodInfo orderFoodInfo = orderFoodInfoMapper.selectById(order.getNumber());
        String orderQuery = YsfFoodUtils.queryOrder(appId, externalOrderNumber, rsaPrivateKey, queryOrderUrl);
        if (ObjectUtil.isNotEmpty(orderQuery)) {
            JSONObject orderObject = JSONObject.parseObject(orderQuery);
            //同步订单状态
            JSONObject orderKbProduct = orderObject.getJSONObject("orderKbProduct");
            order.setExternalOrderNumber(orderKbProduct.getString("number"));
            String ticketCode = orderKbProduct.getString("ticketCode");
            String voucherId = orderKbProduct.getString("voucherId");
            String voucherStatus = orderKbProduct.getString("voucherStatus");
            String effectTime = orderKbProduct.getString("effectTime");
            String expireTime = orderKbProduct.getString("expireTime");
            Integer totalAmount = orderKbProduct.getInteger("totalAmount");
            Integer usedAmount = orderKbProduct.getInteger("usedAmount");
            Integer refundAmount = orderKbProduct.getInteger("refundAmount");
            String orderStatus = orderKbProduct.getString("orderStatus");
            BigDecimal officialPrice = orderKbProduct.getBigDecimal("officialPrice");
            BigDecimal sellingPrice = orderKbProduct.getBigDecimal("sellingPrice");
            if (ObjectUtil.isNotEmpty(ticketCode)) {
                order.setSendStatus("2");
                if (ObjectUtil.isNotEmpty(orderPushInfo)) {
                    orderPushInfo.setStatus("1");
                    historyOrderPushInfoMapper.updateById(orderPushInfo);
                }
            }
            orderFoodInfo.setVoucherId(voucherId);
            orderFoodInfo.setTicketCode(ticketCode);
            orderFoodInfo.setVoucherStatus(voucherStatus);
            orderFoodInfo.setEffectTime(effectTime);
            orderFoodInfo.setExpireTime(expireTime);
            orderFoodInfo.setTotalAmount(totalAmount);
            orderFoodInfo.setUsedAmount(usedAmount);
            orderFoodInfo.setRefundAmount(refundAmount);
            orderFoodInfo.setOrderStatus(orderStatus);
            orderFoodInfo.setOfficialPrice(officialPrice);
            orderFoodInfo.setSellingPrice(sellingPrice);
            baseMapper.updateById(order);
            orderFoodInfoMapper.updateById(orderFoodInfo);
        }

    }

    /**
     * 查询订单列表
     */
    @Override
    public TableDataInfo<HistoryOrderVo> queryPageList(HistoryOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HistoryOrder> lqw = buildQueryWrapper(bo);
        Page<HistoryOrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public void updateOrder(HistoryOrder historyOrder) {
        baseMapper.updateById(historyOrder);
    }

    @Override
    public void historyOrderRefund(Long number, Long userId) {
        HistoryOrderVo orderVo = baseMapper.selectVoById(number);
        HistoryOrder order = baseMapper.selectById(orderVo.getNumber());
        if (!orderVo.getUserId().equals(userId)) {
            throw new ServiceException("登录超时,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        //先查该订单是否支持用户侧退款
        if (ObjectUtil.isEmpty(orderVo.getCusRefund()) || !orderVo.getCusRefund().equals("1")) {
            throw new ServiceException("该订单暂不支持退款");
        }
        if (!"2".equals(orderVo.getStatus())) {
            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
        }
        Refund refund = new Refund();
        refund.setNumber(orderVo.getNumber());
        //退款申请人
        refund.setRefundApplicant(userId.toString());
        refund.setRefundAmount(orderVo.getOutAmount());
        refund.setRefundRemark("用户申请退款");
        //审核中
        refund.setStatus("0");
        PermissionUtils.setDeptIdAndUserId(refund, order.getSysDeptId(), order.getSysUserId());
        String orderType = orderVo.getOrderType();
        //根据订单类型进行退款
        if (orderType.equals("5")) {
            //如果是美食订单 先查询订单
            queryFoodOrder(orderVo.getExternalOrderNumber());
            OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(orderVo.getNumber());
            if (ObjectUtil.isNotEmpty(orderFoodInfoVo.getVoucherStatus()) && !orderFoodInfoVo.getVoucherStatus().equals("EFFECTIVE")) {
                throw new ServiceException("该订单无法申请退款");
            }
            order.setCancelStatus("0");
            order.setStatus("4");
            baseMapper.updateById(order);
            //如果电子券为未使用状态 在这里先走退款接口
            if (ObjectUtil.isNotEmpty(orderFoodInfoVo.getVoucherStatus()) && orderFoodInfoVo.getVoucherStatus().equals("EFFECTIVE")) {
                String appId = YSF_FOOD_PROPERTIES.getAppId();
                String rsaPrivateKey = YSF_FOOD_PROPERTIES.getRsaPrivateKey();
                String refundUrl = YSF_FOOD_PROPERTIES.getRefundUrl();
                //请求美食退款订单接口
                if ("1".equals(orderVo.getCancelStatus())) {
                    throw new ServiceException("退款已提交,不可重复申请");
                }
                YsfFoodUtils.cancelOrder(appId, orderVo.getExternalOrderNumber(), rsaPrivateKey, refundUrl);
            }
            refundMapper.insert(refund);
            return;
        } else if (orderType.equals("7")) {
            //如果是电子券卡密订单 等待同意
            refundMapper.insert(refund);
            order.setStatus("4");
            baseMapper.updateById(order);
            return;
        } else {
            //其他订单 只在失败的情况下才能申请退款
            if (!orderVo.getSendStatus().equals("3")) {
                throw new ServiceException("该订单无法申请退款");
            }
        }
        //失败订单直接给退钱
        refund.setStatus("1");
        refund.setRefundReviewer("系统");
        refundMapper.insert(refund);
        MerchantVo merchantVo = null;
        if (!"2".equals(orderVo.getPickupMethod())) {
            merchantVo = merchantService.queryById(orderVo.getPayMerchant());
            if (ObjectUtil.isEmpty(merchantVo)) {
                throw new ServiceException("商户不存在，请联系客服处理");
            }
        }
        OrderBackTrans orderBackTrans = new OrderBackTrans();
        orderBackTrans.setRefund(orderVo.getOutAmount());
        orderBackTrans.setThNumber(IdUtils.getDateUUID("T"));
        orderBackTrans.setOrderBackTransState("0");
        orderBackTrans.setNumber(orderVo.getNumber());
        //根据订单类型进行退款
        order.setStatus("4");
        //1.付费领取
        if ("1".equals(order.getPickupMethod())) {
            HistoryOrderInfoVo orderInfoVo = historyOrderInfoMapper.selectVoById(orderBackTrans.getNumber());
            Map<String, String> result = PayUtils.backTransReq(orderInfoVo.getQueryId(), BigDecimalUtils.toMinute(orderBackTrans.getRefund()).toString(), orderBackTrans.getThNumber(), merchantVo.getRefundCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), orderInfoVo.getIssAddnData());
            if (ObjectUtil.isEmpty(result)) {
                throw new ServiceException("请求失败，请联系客服处理");
            }
            String respCode = result.get("respCode");
            String queryId = result.get("queryId");
            String txnTime = result.get("txnTime");
            String origQryId = result.get("origQryId");
            if (("00").equals(respCode)) {
                orderBackTrans.setQueryId(queryId);
                orderBackTrans.setOrigQryId(origQryId);
                orderBackTrans.setTxnTime(txnTime);
                orderBackTrans.setOrderBackTransState("2");
                orderBackTrans.setPickupMethod("1");
                orderBackTrans.setSuccessTime(DateUtils.getNowDate());
                order.setStatus("5");
            } else if (("03").equals(respCode) || ("04").equals(respCode) || ("05").equals(respCode)) {
                //后续需发起交易状态查询交易确定交易状态
            } else {
                LogUtil.writeLog("【" + orderBackTrans.getNumber() + "】退款失败，应答信息respCode=" + respCode + ",respMsg=" + result.get("respMsg"));
                //其他应答码为失败请排查原因
                orderBackTrans.setOrderBackTransState("1");
                order.setStatus("6");
            }
        } else if ("2".equals(order.getPickupMethod())) {
            //2.积点兑换
            UserVo userVo = userService.queryById(order.getUserId());
            if (ObjectUtil.isEmpty(userVo)) {
                throw new ServiceException("用户不存在，请联系客服处理");
            }
            R<Void> result = YsfUtils.memberPointAcquire(orderVo.getNumber(), Integer.toUnsignedLong(orderBackTrans.getRefund().intValue()), "1", orderVo.getProductName() + "退款", userVo.getOpenId(), "1", orderVo.getPlatformKey());
            if (result.getCode() == 200) {
                orderBackTrans.setPickupMethod("2");
                orderBackTrans.setSuccessTime(DateUtils.getNowDate());
                orderBackTrans.setOrderBackTransState("2");
                order.setStatus("5");
            } else {
                orderBackTrans.setOrderBackTransState("1");
                order.setStatus("6");
            }
        }
        baseMapper.updateById(order);
        if ("9".equals(order.getOrderType())) {
            HistoryOrder ob = new HistoryOrder();
            ob.setStatus(order.getStatus());
            baseMapper.update(ob, new LambdaQueryWrapper<HistoryOrder>().eq(HistoryOrder::getParentNumber, order.getNumber()));
        }
        PermissionUtils.setDeptIdAndUserId(orderBackTrans, order.getSysDeptId(), order.getSysUserId());
        orderBackTransMapper.insert(orderBackTrans);
    }

    /**
     * 迁移一个月前的订单
     */
    @Override
    public void orderToHistory() {
        Integer pageNum = 1;
        Integer pageSize = 200;
        long total = -1;
        log.info("迁移订单数据开始：{}", DateUtil.now());
        while (true) {
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(pageNum);
            pageQuery.setPageSize(pageSize);
            TableDataInfo<Order> tableDataInfo = orderService.queryHistoryPageList(30, pageQuery);
            if (total == -1) {
                total = tableDataInfo.getTotal();
            }
            log.info("迁移订单数据第{}页，共：{}条数据，时间：{}", pageNum, tableDataInfo.getRows().size(), DateUtil.now());
            for (Order row : tableDataInfo.getRows()) {
                //事务 一起成功一起失败
                try {
                    historyOrderSet(row);
                } catch (Exception e) {
                    log.error("订单迁移失败：", e);
                }
            }
            if (Integer.valueOf(pageNum * pageSize).longValue() >= total) {
                break;
            }
            pageNum++;
            //每天跑不超过40万条数据
            if (pageNum > 2000) {
                break;
            }
        }
        log.info("迁移订单数据结束：{}", DateUtil.now());
    }

    @Transactional(rollbackFor = Exception.class)
    public void historyOrderSet(Order order) {
        //先存入订单
        HistoryOrder copyOrder = new HistoryOrder();
        BeanUtil.copyProperties(order, copyOrder);
        //再存入订单详情
        HistoryOrderInfo historyOrderInfo = new HistoryOrderInfo();
        OrderInfo orderInfo = orderInfoMapper.selectById(order.getNumber());
        if (ObjectUtil.isNotEmpty(orderInfo)) {
            BeanUtil.copyProperties(orderInfo, historyOrderInfo);
            //保存
            int insert = historyOrderInfoMapper.insert(historyOrderInfo);
            //删除原先订单详情
            if (insert < 1) {
                throw new ServiceException("新增订单详情失败");
            }
            Long delete = orderInfoMapper.deleteByNumber(order.getNumber().toString());
            if (delete < 1) {
                throw new ServiceException("删除原订单详情失败");
            }
        }
        LambdaQueryWrapper<OrderPushInfo> orderPushInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderPushInfoLambdaQueryWrapper.eq(OrderPushInfo::getNumber, order.getNumber());
        List<OrderPushInfo> orderPushInfos = orderPushInfoMapper.selectList(orderPushInfoLambdaQueryWrapper);
        if (ObjectUtil.isNotEmpty(orderPushInfos)) {
            for (OrderPushInfo orderPushInfo : orderPushInfos) {
                HistoryOrderPushInfo historyOrderPushInfo = new HistoryOrderPushInfo();
                BeanUtil.copyProperties(orderPushInfo, historyOrderPushInfo);
                //保存
                int insert = historyOrderPushInfoMapper.insert(historyOrderPushInfo);
                //删除原先发放订单
                if (insert < 1) {
                    throw new ServiceException("新增发放订单失败");
                }
                //新增一条删一条
                Long delete = orderPushInfoMapper.deleteById(orderPushInfo.getId());
                if (delete < 1) {
                    throw new ServiceException("删除原发放订单详情失败");
                }
            }
        }
        //保存订单
        int insert = baseMapper.insert(copyOrder);
        //删除原先订单
        if (insert < 1) {
            throw new ServiceException("新增订单失败");
        }
        Long delete = orderMapper.deleteByNumber(order.getNumber().toString());
        if (delete < 1) {
            throw new ServiceException("删除原订单失败");
        }
    }

    private LambdaQueryWrapper<HistoryOrder> buildQueryWrapper(HistoryOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<HistoryOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, HistoryOrder::getProductId, bo.getProductId());
        lqw.eq(bo.getUserId() != null, HistoryOrder::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getPickupMethod()), HistoryOrder::getPickupMethod, bo.getPickupMethod());
        if (StringUtils.isNotBlank(bo.getStatus())) {
            lqw.in(HistoryOrder::getStatus, bo.getStatus().split(","));
        }
        lqw.eq(StringUtils.isNotBlank(bo.getSendStatus()), HistoryOrder::getSendStatus, bo.getSendStatus());
        return lqw;
    }

}
