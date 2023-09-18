package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.RSAUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.constant.UnionPayConstants;
import com.ruoyi.zlyyh.domain.vo.DistributorVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyhmobile.domain.bo.UnionPayCreateBo;
import com.ruoyi.zlyyhmobile.service.IDistributorService;
import com.ruoyi.zlyyhmobile.service.IProductService;
import com.ruoyi.zlyyhmobile.service.IUnionPayContentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 25487
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UnionPayContentOrderServiceImpl implements IUnionPayContentOrderService {

    private final IDistributorService distributorService;
    private final IProductService productService;

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
            // 退券
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00020022", "不允许退券", null, null, null, distributorVo.getPrivateKey(), null);
        } else if ("up.supp.stquery".equals(bizMethod)) {
//            // 交易状态查询
//            OrderVo orderVo = queryOrderByExternalOrderNumber(unionPayCreateBo.getOrigOrderId(), purchasingAgentVo.getPurchasingAgentId());
//            if (null != orderVo) {
//                return upSuppStquery(request, response, unionPayCreateBo, orderVo, distributorVo.getPrivateKey());
//            } else {
//                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00050025", "订单不存在", null, null, null, distributorVo.getPrivateKey(), null);
//            }
        } else {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD20000000", "请求方式不支持，请联系内容提供方", null, null, null, distributorVo.getPrivateKey(), null);
        }
        return null;
    }

    /**
     * 银联分销发券
     */
    private JSONObject upSuppQuerybond(HttpServletRequest request, HttpServletResponse response, UnionPayCreateBo unionPayCreateBo, DistributorVo distributorVo) {
        // TODO 银联内容方直接查询订单表中是否存在对应供应商订单号的订单，如果存在，根据类型返回对应订单信息即可，不存在返回失败。
        // 获取产品编号
        String prodId = unionPayCreateBo.getProdId();
        if (StringUtils.isBlank(prodId)) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "参数错误[prodId]", null, null, null, distributorVo.getPrivateKey(), null);
        }
        Long productId;
        Long skuId = null;
        if (prodId.contains("_")) {
            String[] s = prodId.split("_");
            if (s.length < 2) {
                return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "参数错误[prodId]", null, null, null, distributorVo.getPrivateKey(), null);
            }
            productId = getProductId(s[0]);
            skuId = getProductId(s[1]);
        } else {
            productId = getProductId(prodId);
        }
        if(null == productId){
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "参数错误[prodId]", null, null, null, distributorVo.getPrivateKey(), null);
        }
        ProductVo productVo = productService.queryById(productId);
        if (null == productVo || !"0".equals(productVo.getStatus())) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD40000000", "商品不存在或已下架!", null, null, null, distributorVo.getPrivateKey(), null);
        }
        if ("1".equals(productVo.getProductAffiliation())) {
            return unionPayOrderResultVo(request, response, unionPayCreateBo, "PD00030000", "商品不可购买", null, null, null, distributorVo.getPrivateKey(), null);
        }
        // 查询订单是否存在，存在直接返回结果

        // 发券
        return null;
    }

    private Long getProductId(String prodId) {
        try {
            return Long.parseLong(prodId);
        } catch (NumberFormatException e) {
            return null;
        }
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
}
