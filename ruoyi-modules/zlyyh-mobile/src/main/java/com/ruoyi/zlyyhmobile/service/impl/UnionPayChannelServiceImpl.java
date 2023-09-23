package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderUnionPay;
import com.ruoyi.zlyyh.domain.vo.OrderUnionSendVo;
import com.ruoyi.zlyyh.enumd.UnionPay.UnionPayBizMethod;
import com.ruoyi.zlyyh.enumd.UnionPay.UnionPayParams;
import com.ruoyi.zlyyh.mapper.OrderUnionPayMapper;
import com.ruoyi.zlyyh.properties.utils.YsfDistributionPropertiesUtils;
import com.ruoyi.zlyyh.utils.BigDecimalUtils;
import com.ruoyi.zlyyh.utils.sdk.UnionPayDistributionUtil;
import com.ruoyi.zlyyhmobile.service.IUnionPayChannelService;
import com.ruoyi.zlyyhmobile.service.OrderUnionSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 银联分销渠道方Service业务处理层
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UnionPayChannelServiceImpl implements IUnionPayChannelService {
    private final OrderUnionPayMapper orderUnionPayMapper;
    private final OrderUnionSendService orderUnionSendService;

    /**
     * 银联分销 渠道方 直销 创建订单
     *
     * @param unionPayProductId 银联产品编号
     * @param order             订单信息
     */
    @Override
    public void createUnionPayOrder(String unionPayProductId, Order order) {
        // 元转分
        String orderAmt = BigDecimalUtils.toMinute(order.getWantAmount()).toString();
        String createOrder = UnionPayDistributionUtil.createOrder(order.getNumber(), unionPayProductId, order.getCount(), orderAmt, orderAmt, order.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(order.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJD(order.getPlatformKey()));
        JSONObject unionPayOrder = JSONObject.parseObject(createOrder);
        if (unionPayOrder.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
            OrderUnionPay orderUnionPay = new OrderUnionPay();
            orderUnionPay.setNumber(order.getNumber());
            orderUnionPay.setOrderId(unionPayOrder.getString("orderId"));
            orderUnionPay.setProdTn(unionPayOrder.getString("prodTn"));
            orderUnionPay.setTxnTime(unionPayOrder.getString("txnTime"));
            orderUnionPay.setUsrPayAmt(orderAmt);
            orderUnionPayMapper.insert(orderUnionPay);
            order.setExternalOrderNumber(orderUnionPay.getProdTn());
        } else {
            throw new ServiceException(unionPayOrder.getString("subMsg"));
        }
    }

    /**
     * 银联直销订单支付
     *
     * @param number      订单号
     * @param platformKey 平台标识
     * @return 支付tn号
     */
    public String getPayTn(Long number, Long platformKey) {
        // 银联分销订单处理
        OrderUnionPay orderUnionPay = orderUnionPayMapper.selectById(number);
        if (null == orderUnionPay) {
            throw new ServiceException("订单异常");
        }
        String orderPayStr = UnionPayDistributionUtil.orderPay(number, orderUnionPay.getProdTn(), orderUnionPay.getUsrPayAmt(), platformKey, YsfDistributionPropertiesUtils.getJDAppId(platformKey), YsfDistributionPropertiesUtils.getCertPathJD(platformKey));
        JSONObject orderPay = JSONObject.parseObject(orderPayStr);
        if (orderPay.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
            orderUnionPay.setPayTn(orderPay.getString("payTn"));
            orderUnionPay.setPayTxnTime(orderPay.getString("txnTime"));
            orderUnionPayMapper.updateById(orderUnionPay);
            return orderUnionPay.getPayTn();
        } else {
            throw new ServiceException(orderPay.getString("msg"));
        }
    }

    /**
     * 银联分销 直销 订单发券
     *
     * @param order 订单信息
     */
    public void orderSend(Order order) {
        List<OrderUnionSendVo> orderUnionSendVos = orderUnionSendService.queryListByNumber(order.getNumber());
        // 未发券，执行订单发券操作
        if (ObjectUtil.isEmpty(orderUnionSendVos)) {
            String externalProductId = "1".equals(order.getUnionPay()) ? order.getUnionProductId() : order.getExternalProductId();
            // 直销（银联分销）
            String orderSendStr = UnionPayDistributionUtil.orderSend(order.getNumber(), externalProductId, order.getExternalOrderNumber(), "0", order.getAccount(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(order.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJD(order.getPlatformKey()));
            JSONObject orderSend = JSONObject.parseObject(orderSendStr);
            OrderUnionPay orderUnionPay = orderUnionPayMapper.selectById(order.getNumber());
            this.orderUnionPaySendHand(externalProductId, order, orderUnionPay, orderSend);
        }
    }

    /**
     * 订单发券与发券接口返回后处理 (银联分销)
     */
    private void orderUnionPaySendHand(String externalProductId, Order order, OrderUnionPay orderUnionPay, JSONObject data) {
        if (data.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
            // 发起订单状态查询，向银联确认发券状态
            String orderStatusStr;
            String certPath;
            if ("11".equals(order.getOrderType())) {
                certPath = YsfDistributionPropertiesUtils.getCertPathJC(order.getPlatformKey());
                orderStatusStr = UnionPayDistributionUtil.orderStatus(UnionPayBizMethod.CHNLPUR.getBizMethod(), externalProductId, order.getNumber(), data.getString("txnTime"), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJCAppId(order.getPlatformKey()), certPath);
            } else {
                certPath = YsfDistributionPropertiesUtils.getCertPathJD(order.getPlatformKey());
                orderStatusStr = UnionPayDistributionUtil.orderStatus(UnionPayBizMethod.chnlpur.getBizMethod(), externalProductId, order.getNumber(), orderUnionPay.getTxnTime(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(order.getPlatformKey()), certPath);
            }
            JSONObject orderStatus = JSONObject.parseObject(orderStatusStr);
            // 查询成功，开始处理。
            if (orderStatus.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
                // 发放成功
                if ("05".equals(orderStatus.getString("prodOrderSt"))) {
                    String prodTp = orderStatus.getString("prodTp");
                    JSONArray bondLst = orderStatus.getJSONArray("bondLst");
                    if (orderUnionPay != null) {
                        orderUnionSendService.insertList(order.getNumber(), orderUnionPay.getProdTn(), prodTp, bondLst, certPath, YsfDistributionPropertiesUtils.getCertPwd(order.getPlatformKey()));
                    } else {
                        String prodTn = orderStatus.getString("prodTn");
                        orderUnionSendService.insertList(order.getNumber(), prodTn, prodTp, bondLst, certPath, YsfDistributionPropertiesUtils.getCertPwd(order.getPlatformKey()));
                        orderUnionPay = new OrderUnionPay();
                        orderUnionPay.setNumber(order.getNumber());
                        orderUnionPay.setOrderId(orderStatus.getString("orderId"));
                        orderUnionPay.setProdTn(prodTn);
                        orderUnionPay.setTxnTime(orderStatus.getString("txnTime"));
                        orderUnionPay.setUsrPayAmt("0");
                        orderUnionPayMapper.insertOrUpdate(orderUnionPay);
                        order.setExternalOrderNumber(prodTn);
                    }
                    order.setSendStatus("2");
                    order.setSendTime(DateUtil.date());
                } else if ("04".equals(orderStatus.getString("prodOrderSt"))) {
                    order.setSendStatus("3");
                } else if ("02".equals(orderStatus.getString("prodOrderSt"))) {
                    order.setSendStatus("1");
                }
            }
        } else {
            order.setSendStatus("3");
        }
    }
}
