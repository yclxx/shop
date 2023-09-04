package com.ruoyi.zlyyhmobile.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zlyyh.domain.ProductUnionPay;
import com.ruoyi.zlyyh.mapper.ProductUnionPayMapper;
import com.ruoyi.zlyyhmobile.service.IProductUnionPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductUnionPayServiceImpl implements IProductUnionPayService {
    private final ProductUnionPayMapper baseMapper;

    /**
     * 根据商品id获取详情
     * @param productId 商品id
     * @return 商品详情
     */
    public ProductUnionPay selectProductById(Long productId) {
        return baseMapper.selectById(productId);
    }

    /**
     * 根据第三方id获取商品详情
     * @param externalProductId 第三方商品id
     * @return 商品详情
     */
    public ProductUnionPay selectProductByExternalProductId(String externalProductId) {
        LambdaQueryWrapper<ProductUnionPay> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductUnionPay::getExternalProductId, externalProductId);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 处理银联分销 （商品详情）
     */
    public ProductUnionPay productHandler(JSONObject productDetail) {
        ProductUnionPay productUnionPay = JSONObject.toJavaObject(productDetail, ProductUnionPay.class);
        baseMapper.updateById(productUnionPay);
        return productUnionPay;
    }
}
