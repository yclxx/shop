package com.ruoyi.system.api;

/**
 * 银联分销，商品列表接口
 */
public interface RemoteProductService {
    /**
     * 获取接口
     */
    void selectUnionPayProductList(Long platformKey);
}
