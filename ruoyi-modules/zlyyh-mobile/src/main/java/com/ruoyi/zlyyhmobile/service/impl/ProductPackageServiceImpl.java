package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.ProductPackage;
import com.ruoyi.zlyyh.domain.vo.ProductPackageVo;
import com.ruoyi.zlyyh.mapper.ProductPackageMapper;
import com.ruoyi.zlyyhmobile.service.IProductPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品券包Service业务层处理
 *
 * @author yzg
 * @date 2023-06-30
 */
@RequiredArgsConstructor
@Service
public class ProductPackageServiceImpl implements IProductPackageService {

    private final ProductPackageMapper baseMapper;

    /**
     * 查询商品券包列表
     */
    @Cacheable(cacheNames = CacheNames.ProductPackage, key = "#productId")
    @Override
    public List<ProductPackageVo> queryListByProductId(Long productId) {
        LambdaQueryWrapper<ProductPackage> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductPackage::getProductId, productId);
        lqw.eq(ProductPackage::getStatus, "0");
        return baseMapper.selectVoList(lqw);
    }
}
