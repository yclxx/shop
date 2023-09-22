package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.utils.*;
import com.ruoyi.zlyyh.constant.UnionPayConstants;
import com.ruoyi.zlyyh.domain.UnionPayContentOrder;
import com.ruoyi.zlyyh.domain.vo.CodeVo;
import com.ruoyi.zlyyh.domain.vo.DistributorVo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.domain.vo.UnionPayContentOrderVo;
import com.ruoyi.zlyyh.mapper.UnionPayContentOrderMapper;
import com.ruoyi.zlyyhmobile.domain.bo.UnionPayCreateBo;
import com.ruoyi.zlyyhmobile.event.UnionPayContentRefundCallbackEvent;
import com.ruoyi.zlyyhmobile.event.UnionPayContentSendCouponCallbackEvent;
import com.ruoyi.zlyyhmobile.service.ICodeService;
import com.ruoyi.zlyyhmobile.service.IDistributorService;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import com.ruoyi.zlyyhmobile.service.IUnionPayContentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 25487
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UnionPayContentOrderServiceImpl implements IUnionPayContentOrderService {

    private final IDistributorService distributorService;
    private final UnionPayContentOrderMapper baseMapper;
    private final IOrderService orderService;
    private final ICodeService codeService;

    /**
     * 银联分销
     */
    @Override
    public JSONObject unionPay(HttpServletRequest request, HttpServletResponse response, UnionPayCreateBo unionPayCreateBo) {
        String postData = ServletUtils.getParamJson(request);
        log.info("银联分销,请求头：{}，请求参数：{}", JsonUtils.toJsonString(ServletUtils.getHeaderMap(request)), postData);
        // 版本号
        String version = ServletUtils.getHeader(UnionPayConstants.VERSION);
        // 发送方索引类型
        String appType = ServletUtils.getHeader(UnionPayConstants.APP_TYPE);
        // 发送方索引标识码
        String appId = ServletUtils.getHeader(UnionPayConstants.APP_ID);
        // 接口类型 标识交易类型
        String bizMethod = ServletUtils.getHeader(UnionPayConstants.BIZ_METHOD);
        // 签名 由请求或应答的发送方根据报文签名方法生成，填写对报文摘要的签名
        String sign = ServletUtils.getHeader(UnionPayConstants.SIGN);
        // 签名或摘要方式
        String signMethod = ServletUtils.getHeader(UnionPayConstants.SIGN_METHOD);
        // 发送方流水号
        String reqId = ServletUtils.getHeader(UnionPayConstants.REQ_ID);
        // 查询采购商
        DistributorVo distributorVo = distributorService.queryById(appId);
        // 获取采购商验签证书
        if (null == distributorVo || !"0".equals(distributorVo.getStatus())) {
            if (null != distributorVo) {
                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00030000", "采购商不存在", null, null, null, distributorVo.getPrivateKey(), null);
            }
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00030000", "采购商不存在", null, null, null, null, null);
        }
        // 验签
        String str = "version=" + version + "&appId=" + appId + "&bizMethod=" + bizMethod + "&reqId=" + reqId + "&body=" + postData;
        if ("RSA2".equalsIgnoreCase(signMethod)) {
            //验证签名
            boolean verify = RSAUtils.verifySign(distributorVo.getPublicKey(), str, sign);
            if (!verify) {
                log.error("银联分销验签失败,签名方法：{},签名原文：{},待验签字符串：{}", signMethod, sign, str);
//                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40030000", "验签失败", null, null, null, privateKey, null);
            }
        } else {
            log.error("银联分销取码签名方式不支持{}", signMethod);
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40030000", "签名方式错误", null, null, null, distributorVo.getPrivateKey(), null);
        }
        if ("up.supp.querybond".equals(bizMethod)) {
            // 发券
            return upSuppQuerybond(request, response, unionPayCreateBo, distributorVo);
        } else if ("up.supp.returnbond".equals(bizMethod)) {
            return upSuppReturnbond(request, response, unionPayCreateBo, distributorVo);
        } else if ("up.supp.stquery".equals(bizMethod)) {
            // 交易状态查询
            return upSuppStquery(request, response, unionPayCreateBo, distributorVo);
        } else {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD20000000", "请求方式不支持，请联系内容提供方", null, null, null, distributorVo.getPrivateKey(), null);
        }
    }

    /**
     * 银联分销退券
     */
    private JSONObject upSuppReturnbond(HttpServletRequest request, HttpServletResponse response, UnionPayCreateBo unionPayCreateBo, DistributorVo distributorVo) {
        String codeNo = RSAUtils.decryptByPrivateKey(unionPayCreateBo.getBondNo(), distributorVo.getPrivateKey());
        if (StringUtils.isEmpty(codeNo)) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00020022", "券码解密失败", null, null, null, distributorVo.getPrivateKey(), null);
        }
        // 查询券码
        CodeVo codeVo = codeService.queryByCodeNo(codeNo);
        if (null == codeVo) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00020022", "核销码错误", null, null, null, distributorVo.getPrivateKey(), null);
        }
        if ("1".equals(codeVo.getUsedStatus())) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00020022", "券码已核销不可退款", null, null, null, distributorVo.getPrivateKey(), null);
        }
        // 查询订单
        OrderVo orderVo = orderService.queryBaseOrderById(codeVo.getNumber());
        if (null == orderVo) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00020022", "订单不存在", null, null, null, distributorVo.getPrivateKey(), null);
        }
        // 作废券码
        boolean b = codeService.cancellationCode(codeVo.getCodeNo());
        if (!b) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00020022", "退券失败", null, null, null, distributorVo.getPrivateKey(), null);
        }
        // 异步通知退券结果
        Date cancellationTime = codeVo.getUsedTime();
        if (null == cancellationTime) {
            cancellationTime = new Date();
        }
        String prodTime = DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, cancellationTime);
        UnionPayContentRefundCallbackEvent event = new UnionPayContentRefundCallbackEvent(getProductId(orderVo), distributorVo.getPrivateKey(), distributorVo.getDistributorId(), distributorVo.getBackUrl(), RSAUtils.encryptByPublicKey(codeVo.getCodeNo(), distributorVo.getPublicKey()), prodTime);
        SpringUtils.context().publishEvent(event);
        return unionPayOrderResultVo(request, response, unionPayCreateBo, "0000000000", "退券成功", "00", null, null, distributorVo.getPrivateKey(), null);
    }

    /**
     * 银联分销 发券状态查询
     */
    private JSONObject upSuppStquery(HttpServletRequest request, HttpServletResponse response, UnionPayCreateBo unionPayCreateBo, DistributorVo distributorVo) {
        if ("up.supp.querybond".equals(unionPayCreateBo.getOrigBizMethod())) {
            OrderVo orderVo = orderService.queryByExternalOrderNumber(unionPayCreateBo.getOrigOrderId());
            if (null == orderVo) {
                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "订单不存在", null, null, null, distributorVo.getPrivateKey(), null);
            }
            if (!"2".equals(orderVo.getStatus())) {
                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "订单未支付", null, null, null, distributorVo.getPrivateKey(), null);
            }
            List<JSONObject> bondList = this.queryCodeVoList(orderVo, distributorVo.getPublicKey());
            if (ObjectUtil.isEmpty(bondList)) {
                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "未找到对应券码", null, null, null, distributorVo.getPrivateKey(), null);
            }
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "0000000000", "发券成功", null, "0", bondList, distributorVo.getPrivateKey(), "00");
        } else if ("up.supp.returnbond".equals(unionPayCreateBo.getOrigBizMethod())) {
            // todo 退券交易查询
        }
        return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "原交易类型不支持", null, null, null, distributorVo.getPrivateKey(), null);
    }

    /**
     * 银联分销发券
     */
    private JSONObject upSuppQuerybond(HttpServletRequest request, HttpServletResponse response, UnionPayCreateBo unionPayCreateBo, DistributorVo distributorVo) {
        OrderVo orderVo = orderService.queryByExternalOrderNumber(unionPayCreateBo.getOrderId());
        if (null == orderVo) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "订单创建失败,暂不支持该渠道方", null, null, null, distributorVo.getPrivateKey(), null);
        }
        if (!"2".equals(orderVo.getStatus())) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "订单创建失败,订单非支付成功状态", null, null, null, distributorVo.getPrivateKey(), null);
        }
        List<JSONObject> bondList = this.queryCodeVoList(orderVo, distributorVo.getPublicKey());
        if (ObjectUtil.isEmpty(bondList)) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "订单创建失败,未找到对应券码", null, null, null, distributorVo.getPrivateKey(), null);
        }
        // 查询是否新增过内容订单
        UnionPayContentOrderVo unionPayContentOrderVo = this.queryByUnionPayContentOrderId(unionPayCreateBo.getOrderId(), distributorVo.getDistributorId());
        if (null == unionPayContentOrderVo) {
            // 新增
            boolean b = this.insertUnionPayContentOrder(unionPayCreateBo, distributorVo.getDistributorId(), orderVo.getNumber(), "2");
            if (!b) {
                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "订单创建失败,系统异常", null, null, null, distributorVo.getPrivateKey(), null);
            }
            unionPayContentOrderVo = this.queryByUnionPayContentOrderId(unionPayCreateBo.getOrderId(), distributorVo.getDistributorId());
            if (null == unionPayContentOrderVo) {
                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "订单创建失败,系统异常", null, null, null, distributorVo.getPrivateKey(), null);
            }
        }
        String prodCertTp = "0";
        // 异步通知发券成功
        UnionPayContentSendCouponCallbackEvent event = getUnionPayContentSendCouponCallbackEvent(unionPayCreateBo, distributorVo, orderVo, bondList, prodCertTp);
        SpringUtils.context().publishEvent(event);
        // 同步返回结果
        return unionPayOrderResultVo(request, response, unionPayCreateBo, "0000000000", "发券成功", "00", prodCertTp, bondList, distributorVo.getPrivateKey(), null);
    }

    /**
     * 组装异步通知内容
     *
     * @param unionPayCreateBo 请求信息
     * @param distributorVo    分销商信息
     * @param orderVo          订单信息
     * @return 通知内容
     */
    @NotNull
    private UnionPayContentSendCouponCallbackEvent getUnionPayContentSendCouponCallbackEvent(UnionPayCreateBo unionPayCreateBo, DistributorVo distributorVo, OrderVo orderVo, List<JSONObject> bondList, String prodCertTp) {
        UnionPayContentSendCouponCallbackEvent event = new UnionPayContentSendCouponCallbackEvent();
        event.setUnionPayProductId(getProductId(orderVo));
        event.setPrivateKey(distributorVo.getPrivateKey());
        event.setOrigTxnTime(unionPayCreateBo.getTxnTime());
        event.setOrigOrderId(unionPayCreateBo.getOrderId());
        event.setAppId(distributorVo.getDistributorId());
        event.setCallbackUrl(distributorVo.getBackUrl());
        event.setBondList(bondList);
        event.setProdCertTp(prodCertTp);
        return event;
    }

    /**
     * 查询订单券码记录
     *
     * @param orderVo 订单信息
     * @return 券码集合
     */
    private List<JSONObject> queryCodeVoList(OrderVo orderVo, String publicKey) {
        List<CodeVo> codeVos = codeService.queryByNumber(orderVo.getNumber());
        if (ObjectUtil.isEmpty(codeVos)) {
            return new ArrayList<>();
        }
        // 转换
        List<JSONObject> resultList = new ArrayList<>(codeVos.size());
        for (CodeVo codeVo : codeVos) {
            JSONObject result = new JSONObject();
            result.put("effectDtTm", null != orderVo.getUsedStartTime() ? DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, orderVo.getUsedStartTime()) : DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, orderVo.getCreateTime()));
            result.put("exprDtTm", null != orderVo.getUsedEndTime() ? DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, orderVo.getUsedEndTime()) : DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, DateUtils.addDays(orderVo.getCreateTime(), 30)));
            result.put("bondNo", RSAUtils.encryptByPublicKey(codeVo.getCodeNo(), publicKey));
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 新增银联内容分销商品订单
     *
     * @param unionPayCreateBo 银联分销请求内容
     * @param appId            银联分销内容方AppID
     * @param number           我方订单号
     * @param orderSendStatus  订单发券状态
     */
    private boolean insertUnionPayContentOrder(UnionPayCreateBo unionPayCreateBo, String appId, Long number, String orderSendStatus) {
        UnionPayContentOrder unionPayContentOrder = new UnionPayContentOrder();
        unionPayContentOrder.setUnionPayAppId(appId);
        unionPayContentOrder.setUnionPayOrderId(unionPayCreateBo.getOrderId());
        unionPayContentOrder.setUnionPayProdId(unionPayCreateBo.getProdId());
        unionPayContentOrder.setUnionPayTxnTime(unionPayCreateBo.getTxnTime());
        unionPayContentOrder.setUnionPayPurQty(unionPayCreateBo.getPurQty());
        unionPayContentOrder.setUnionPayProdAstIdTp(unionPayCreateBo.getProdAstIdTp());
        unionPayContentOrder.setUnionPayProdAstId(unionPayCreateBo.getProdAstId());
        unionPayContentOrder.setNumber(number);
        if ("2".equals(orderSendStatus)) {
            unionPayContentOrder.setUnionPayResultStatus("00");
        } else if ("3".equals(orderSendStatus)) {
            unionPayContentOrder.setUnionPayResultStatus("10");
        } else {
            unionPayContentOrder.setUnionPayResultStatus("20");
        }
        int insert = baseMapper.insert(unionPayContentOrder);
        return insert > 0;
    }

    /**
     * 查询内容分销内容方订单
     *
     * @param orderId 内容方订单号
     * @param appId   内容方AppID
     * @return 订单信息
     */
    private UnionPayContentOrderVo queryByUnionPayContentOrderId(String orderId, String appId) {
        LambdaQueryWrapper<UnionPayContentOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(UnionPayContentOrder::getUnionPayOrderId, orderId);
        lqw.eq(UnionPayContentOrder::getUnionPayAppId, appId);
        lqw.last("order by id desc limit 1");
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 银联分销发券返回结果
     *
     * @param unionPayCreateBo 请求参数
     * @param code             应答码
     * @param msg              应答描述
     * @param procSt           交易处理状态
     */
    private JSONObject unionPayOrderResultVo(HttpServletRequest request, HttpServletResponse response, UnionPayCreateBo unionPayCreateBo, String code, String msg, String procSt, String prodCertTp, List<JSONObject> bondLst, String privateKey, String origProcSt) {
        JSONObject result = new JSONObject();
        result.put("bizMethod", unionPayCreateBo.getBizMethod());
        result.put("txnTime", unionPayCreateBo.getTxnTime());
        result.put("orderId", unionPayCreateBo.getOrderId());
        result.put("code", code);
        result.put("msg", msg);
        if (StringUtils.isNotBlank(procSt)) {
            result.put("procSt", procSt);
        }
        if (StringUtils.isNotBlank(prodCertTp)) {
            result.put("prodCertTp", prodCertTp);
        }
        if (ObjectUtil.isNotEmpty(bondLst)) {
            result.put("bondLst", bondLst);
        }
        if (StringUtils.isNotEmpty(unionPayCreateBo.getOrigBizMethod())) {
            result.put("origBizMethod", unionPayCreateBo.getOrigBizMethod());
        }
        if (StringUtils.isNotEmpty(unionPayCreateBo.getOrigTxnTime())) {
            result.put("origTxnTime", unionPayCreateBo.getOrigTxnTime());
        }
        if (StringUtils.isNotEmpty(unionPayCreateBo.getOrigOrderId())) {
            result.put("origOrderId", unionPayCreateBo.getOrigOrderId());
        }
        if (StringUtils.isNotEmpty(origProcSt)) {
            result.put("origProcSt", origProcSt);
        }
        // 设置响应头
        response.setHeader(UnionPayConstants.VERSION, request.getHeader(UnionPayConstants.VERSION));
        response.setHeader(UnionPayConstants.APP_TYPE, request.getHeader(UnionPayConstants.APP_TYPE));
        response.setHeader(UnionPayConstants.APP_ID, request.getHeader(UnionPayConstants.APP_ID));
        response.setHeader(UnionPayConstants.BIZ_METHOD, request.getHeader(UnionPayConstants.BIZ_METHOD));
        response.setHeader(UnionPayConstants.SIGN_METHOD, request.getHeader(UnionPayConstants.SIGN_METHOD));
        response.setHeader(UnionPayConstants.REQ_ID, request.getHeader(UnionPayConstants.REQ_ID));
        String str = "version=" + request.getHeader(UnionPayConstants.VERSION) + "&appId=" + request.getHeader(UnionPayConstants.APP_ID) + "&bizMethod=" + request.getHeader(UnionPayConstants.BIZ_METHOD) + "&reqId=" + request.getHeader(UnionPayConstants.REQ_ID) + "&body=" + result;
        String sign = "";
        if (StringUtils.isNotBlank(privateKey)) {
            sign = RSAUtils.sign(privateKey, str);
            response.setHeader(UnionPayConstants.SIGN, sign);
        }
        log.info("银联分销，reqId:{},返回参数：{},签名原文：{},签名结果：{}", request.getHeader(UnionPayConstants.REQ_ID), result, str, sign);
        return result;
    }

    /**
     * 获取产品编号 如果有规格则返回规格ID
     *
     * @param orderVo 订单信息
     * @return 产品编号
     */
    private String getProductId(OrderVo orderVo) {
        String productId = orderVo.getProductId().toString();
        if (null != orderVo.getProductSkuId()) {
            productId = orderVo.getProductSkuId().toString();
        }
        return productId;
    }
}
