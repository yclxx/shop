package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.CommercialTenantProductVo;

import java.util.List;

/**
 * 商户商品配置Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface ICommercialTenantProductService {

    /**
     * 查询商户商品配置列表
     */
    List<Long> queryListByProductIds(List<Long> productIds);

    /**
     * 查询商户商品配置列表
     */
    List<CommercialTenantProductVo> queryListByCommercialTenantId(Long commercialTenantId);
}
