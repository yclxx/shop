package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.CommercialTenantProduct;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantProductVo;
import com.ruoyi.zlyyh.mapper.CommercialTenantProductMapper;
import com.ruoyi.zlyyhmobile.service.ICommercialTenantProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户商品配置Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class CommercialTenantProductServiceImpl implements ICommercialTenantProductService {

    private final CommercialTenantProductMapper baseMapper;

    /**
     * 查询商户商品配置列表
     */
    @Cacheable(cacheNames = CacheNames.COMMERCIAL_PRODUCT_IDS,key = "#commercialTenantId")
    @Override
    public List<CommercialTenantProductVo> queryListByCommercialTenantId(Long commercialTenantId) {
        LambdaQueryWrapper<CommercialTenantProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommercialTenantProduct::getCommercialTenantId, commercialTenantId);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询商户商品配置列表
     */
    @Override
    public List<Long> queryListByProductIds(List<Long> productIds) {
        QueryWrapper<CommercialTenantProduct> lqw = Wrappers.query();
        lqw.select("DISTINCT commercial_tenant_id").lambda().in(CommercialTenantProduct::getProductId, productIds);
        List<CommercialTenantProductVo> commercialTenantProductVos = baseMapper.selectVoList(lqw);
        if(ObjectUtil.isEmpty(commercialTenantProductVos)){
            return new ArrayList<>();
        }
        return commercialTenantProductVos.stream().map(CommercialTenantProductVo::getCommercialTenantId).collect(Collectors.toList());
    }
}
