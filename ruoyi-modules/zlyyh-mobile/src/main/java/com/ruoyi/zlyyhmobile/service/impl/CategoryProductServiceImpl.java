package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.CategoryProduct;
import com.ruoyi.zlyyh.domain.vo.CategoryProductVo;
import com.ruoyi.zlyyh.mapper.CategoryProductMapper;
import com.ruoyi.zlyyhmobile.service.ICategoryProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 栏目商品关联Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class CategoryProductServiceImpl implements ICategoryProductService {

    private final CategoryProductMapper baseMapper;

    /**
     * 查询栏目商品关联列表
     */
    @Cacheable(cacheNames = CacheNames.CATEGORY_PRODUCT,key = "#categoryId")
    @Override
    public List<CategoryProductVo> queryList(Long categoryId) {
        LambdaQueryWrapper<CategoryProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(CategoryProduct::getCategoryId, categoryId);
        return baseMapper.selectVoList(lqw);
    }
}
