package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderBackTrans;
import com.ruoyi.zlyyh.domain.bo.AppWxPayCallbackParams;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.OrderBackTransMapper;
import com.ruoyi.zlyyh.mapper.OrderInfoMapper;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.utils.BigDecimalUtils;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyh.utils.WxUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.sdk.LogUtil;
import com.ruoyi.zlyyh.utils.sdk.PayUtils;
import com.ruoyi.zlyyhmobile.event.ShareOrderEvent;
import com.ruoyi.zlyyhmobile.service.IMerchantService;
import com.ruoyi.zlyyhmobile.service.IOrderBackTransService;
import com.ruoyi.zlyyhmobile.service.IProductService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Date;
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
        bo.setThNumber(IdUtils.getSnowflakeNextIdStr("T"));
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
            UserVo userVo = userService.queryById(order.getUserId(), order.getSupportChannel());
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
        PermissionUtils.setDeptIdAndUserId(add, order.getSysDeptId(), order.getSysUserId());
        return baseMapper.insert(add) > 0;
    }

    /**
     * 微信退款回调业务处理
     */
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void wxRefundCallBack(Long merchantId, HttpServletRequest request) {
        //验证签名
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IoUtil.read(reader);
        log.info("微信退款回调通知，商户号ID：{},通知内容：{}", merchantId, body);
        AppWxPayCallbackParams appWxPayCallbackParams = JsonUtils.parseObject(body, AppWxPayCallbackParams.class);
        MerchantVo merchantVo = merchantService.queryById(merchantId);
        if (null == merchantVo) {
            log.error("微信退款通知，商户不存在");
            throw new ServiceException("商户不存在");
        }
        String apiV3Key, merchantKey, mchid, merchantSerialNumber;
        apiV3Key = merchantVo.getApiKey();
        merchantKey = merchantVo.getCertPath();
        mchid = merchantVo.getMerchantNo();
        merchantSerialNumber = merchantVo.getMerchantKey();
        String s;
        try {
            s = WxUtils.decryptToString(apiV3Key.getBytes(StandardCharsets.UTF_8), appWxPayCallbackParams.getResource().getAssociated_data().getBytes(StandardCharsets.UTF_8), appWxPayCallbackParams.getResource().getNonce().getBytes(StandardCharsets.UTF_8), appWxPayCallbackParams.getResource().getCiphertext());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("解密异常");
        }
        log.info("微信退款回调信息：" + s);

        String wechatPayTimestamp = request.getHeader("Wechatpay-Timestamp");
        String wechatPayNonce = request.getHeader("Wechatpay-Nonce");
        String wechatSignature = request.getHeader("Wechatpay-Signature");
        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        //签名信息
        String signMessage = wechatPayTimestamp + "\n" + wechatPayNonce + "\n" + body + "\n";
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
            WxUtils.getCertInput(merchantKey));

        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
            new WechatPay2Credentials(mchid, new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)),
            apiV3Key.getBytes(StandardCharsets.UTF_8));
        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3秘钥）
        try {
            boolean verify = verifier.verify(wechatPaySerial, signMessage.getBytes(StandardCharsets.UTF_8), wechatSignature);
            if (!verify) {
                log.info("退款回调验签失败，签名信息：" + signMessage + "平台证书序列号：" + wechatPaySerial + "签名：" + wechatSignature);
                //            throw new ServiceException("验签失败");
            }
        } catch (Exception e) {
            log.error("微信退款回调异常：", e);
        }
        log.info("微信退款回调验签成功");
        Map<String, Object> result = JsonUtils.parseMap(s);
        if (null == result || null == result.get("out_refund_no")) {
            throw new ServiceException("无退款订单号out_refund_no");
        }
        // 退款单号
        String out_refund_no = (String) result.get("out_refund_no");
        // 退款成功时间
        String success_time = (String) result.get("success_time");
        // 退款状态
        // SUCCESS：退款成功
        // CLOSED：退款关闭
        // PROCESSING：退款处理中
        // ABNORMAL：退款异常
        String refund_status = (String) result.get("refund_status");
        // 金额信息
        Map<String, Object> map = BeanUtil.beanToMap(result.get("amount"));
        // 查询退款订单是否存在
        OrderBackTransVo orderBackTransVo = baseMapper.selectVoById(out_refund_no);
        if (null == orderBackTransVo) {
            log.info("微信退款回调订单【" + out_refund_no + "】不存在，请核实");
            return;
        }
        if (!"0".equals(orderBackTransVo.getOrderBackTransState())) {
            return;
        }
        OrderVo orderVo = orderMapper.selectVoById(orderBackTransVo.getNumber());
        if (null == orderVo) {
            log.info("微信退款回调订单【" + out_refund_no + "】不存在，请核实");
            return;
        }
        Order order = new Order();
        order.setNumber(orderBackTransVo.getNumber());

        OrderBackTrans backTrans = new OrderBackTrans();
        backTrans.setThNumber(orderBackTransVo.getThNumber());
        backTrans.setSuccessTime(new Date());

        if (("SUCCESS").equals(refund_status)) {
            backTrans.setOrderBackTransState("2");
            order.setStatus("5");
        } else {
            backTrans.setOrderBackTransState("1");
            order.setStatus("6");
        }

        baseMapper.updateById(backTrans);
        orderMapper.updateById(order);
        if ("9".equals(order.getOrderType())) {
            Order ob = new Order();
            ob.setStatus(order.getStatus());
            orderMapper.update(ob, new LambdaQueryWrapper<Order>().eq(Order::getParentNumber, order.getNumber()));
        }
        SpringUtils.context().publishEvent(new ShareOrderEvent(null, orderVo.getNumber()));
    }
}
