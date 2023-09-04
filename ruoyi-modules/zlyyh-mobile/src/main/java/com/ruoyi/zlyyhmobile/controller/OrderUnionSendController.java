package com.ruoyi.zlyyhmobile.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.ProductUnionPay;
import com.ruoyi.zlyyh.enumd.UnionPay.UnionPayBizMethod;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyh.properties.utils.YsfDistributionPropertiesUtils;
import com.ruoyi.zlyyh.utils.sdk.UnionPayDistributionUtil;
import com.ruoyi.zlyyhmobile.service.IProductUnionPayService;
import com.ruoyi.zlyyhmobile.service.OrderUnionSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单控制器
 * 前端访问路由地址为:/zlyyh-mobile/order
 *
 * @author ruoyi
 * @date 2023-03-18
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderUnionSend")
public class OrderUnionSendController {
    private final OrderUnionSendService orderUnionSendService;
    private final ProductMapper productMapper;
    private final IProductUnionPayService productUnionPayService;

    /**
     * 通知 （银联分销 银联发送给我方）
     */
    @RequestMapping("/ignore/notification")
    public void couponNotification(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject data) throws IOException {
        String bizMethod = request.getHeader("bizMethod");
        if (StringUtils.isEmpty(bizMethod)) {
            Map<String, String> result = new HashMap<>();
            result.put("code", "40040000");
            result.put("msg", "请求业务失败");
            response.getWriter().print(result);
            return;
        }

        if (bizMethod.equals(UnionPayBizMethod.BONDCANCEL.getBizMethod())) {
            log.info("银联分销核销通知开始...");
            log.info("银联分销核销通知请求参数：{}", data);
            orderUnionSendService.couponNotification(response, data);
            log.info("银联分销核销通知结束...");
        } else if (bizMethod.equals(UnionPayBizMethod.BONDRFD.getBizMethod())) {
            log.info("银联分销退券通知开始...");
            log.info("银联分销退券通知请求参数：{}", data);
            orderUnionSendService.couponRefundNotification(response, data);
            log.info("银联分销退券通知结束...");
        } else if (bizMethod.equals(UnionPayBizMethod.BONDROLLBACK.getBizMethod())) {
            log.info("银联分销卡券返还通知开始...");
            log.info("银联分销卡券返还通知请求参数：{}", data);
            orderUnionSendService.couponRestitutionNotification(response, data);
            log.info("银联分销卡券返还通知结束...");
        } else if (bizMethod.equals(UnionPayBizMethod.REFUND.getBizMethod())) {
            log.info("银联分销退款通知开始...");
            log.info("银联分销退款通知请求参数：{}", data);
            orderUnionSendService.refundNotification(response, data);
            log.info("银联分销退款通知结束...");
        } else if (bizMethod.equals(UnionPayBizMethod.CHNLSYN.getBizMethod())) {
            log.info("银联分销渠道信息同步通知开始...");
            log.info("银联分销渠道信息同步通知请求参数：{}", data);
            if (data.getString("chnlProdIdLst") != null) {
                String[] chnlProdIdLst = data.getString("chnlProdIdLst").split("\\|");
                for (int i = 0; i < chnlProdIdLst.length; i++) {
                    ProductUnionPay productUnionPay = productUnionPayService.selectProductByExternalProductId(chnlProdIdLst[i]);
                    if (productUnionPay != null) {
                        Product product = productMapper.selectById(productUnionPay.getProductId());
                        if (product != null) {
                            String productDetailsStr;
                            if (productUnionPay.getCoopMd().equals("3")) {
                                // 直销
                                productDetailsStr = UnionPayDistributionUtil.selectProductDetails(productUnionPay.getExternalProductId(), product.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(product.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJD(product.getPlatformKey()));
                            } else { // 代销
                                productDetailsStr = UnionPayDistributionUtil.selectProductDetails(productUnionPay.getExternalProductId(), product.getPlatformKey(), YsfDistributionPropertiesUtils.getJCAppId(product.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJC(product.getPlatformKey()));
                            }
                            JSONObject productDetails = JSONObject.parseObject(productDetailsStr);
                            productUnionPayService.productHandler(productDetails);

                            this.productHandle(product, productDetails);
                        }
                    }
                }
            }
            log.info("银联分销渠道信息同步通知结束...");
        }
    }

    /**
     * 处理商品相关功能
     */
    public void productHandle(Product product, JSONObject productDetails) {
        BigDecimal bigDecimal = BigDecimal.valueOf(100);
        String prodValue = productDetails.getString("prodValue"); // 商品面值
        String prodPrice = productDetails.getString("prodPrice"); // 商品价格
        String stkMd = productDetails.getString("stkMd"); // 商品库存类型 0-数量;1-状态;
        String salesStartTm = productDetails.getString("salesStartTm"); // 销售起始时间
        String salesEndTm = productDetails.getString("salesEndTm"); // 销售截止时间
        String prodSt = productDetails.getString("prodSt");// 商品状态
        String cstkAvlNum = productDetails.getString("cstkAvlNum");// 商品状态
        if (StringUtils.isNotEmpty(prodValue)) {
            product.setOriginalAmount(new BigDecimal(prodValue).divide(bigDecimal));
        }
        if (StringUtils.isNotEmpty(prodPrice)) {
            product.setSellAmount(new BigDecimal(prodPrice).divide(bigDecimal));
        }
        if (StringUtils.isNotEmpty(prodSt)) {
            if (prodSt.equals("1")) {
                product.setStatus("0");
            } else {
                product.setStatus("1");
            }
        }
        if (StringUtils.isNotEmpty(stkMd)) {
            if (stkMd.equals("0")) {
                product.setTotalCount(Long.valueOf(cstkAvlNum));
            } else {
                product.setTotalCount(-1L);
            }
        }
        if (StringUtils.isNotEmpty(salesStartTm) & StringUtils.isNotEmpty(salesStartTm)) {
            Date startTm = DateUtils.parseDate(salesStartTm);
            Date endTm = DateUtils.parseDate(salesEndTm);
            product.setShowStartDate(startTm);
            product.setShowEndDate(endTm);
            product.setSellStartDate(startTm);
            product.setSellEndDate(endTm);
        }
        productMapper.updateById(product);
    }
}
