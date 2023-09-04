package com.ruoyi.zlyyhmobile.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.zlyyh.domain.ProductUnionPay;

/**
 * 银联分销详情商品列表
 */
public interface IProductUnionPayService {
    /**
     * 根据商品id获取详情
     * @param productId 商品id
     * @return 商品详情
     */
    ProductUnionPay selectProductById(Long productId);

    /**
     * 根据第三方id获取商品详情
     * @param externalProductId 第三方商品id
     * @return 商品详情
     */
    ProductUnionPay selectProductByExternalProductId(String externalProductId);

    /**
     * 处理银联分销 （商品详情）
     */
    ProductUnionPay productHandler(JSONObject productDetail);
}
