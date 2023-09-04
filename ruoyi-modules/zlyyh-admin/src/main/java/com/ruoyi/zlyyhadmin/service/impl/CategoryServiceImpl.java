package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.Category;
import com.ruoyi.zlyyh.domain.CategoryProduct;
import com.ruoyi.zlyyh.domain.bo.CategoryBo;
import com.ruoyi.zlyyh.domain.bo.CategoryProductBo;
import com.ruoyi.zlyyh.domain.vo.CategoryProductVo;
import com.ruoyi.zlyyh.domain.vo.CategoryVo;
import com.ruoyi.zlyyh.mapper.CategoryMapper;
import com.ruoyi.zlyyhadmin.service.ICategoryProductService;
import com.ruoyi.zlyyhadmin.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 栏目Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-31
 */
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryMapper baseMapper;
    private final ICategoryProductService categoryProductService;

    /**
     * 查询栏目
     */
    @Override
    public CategoryVo queryById(Long categoryId) {
        CategoryVo categoryVo = baseMapper.selectVoById(categoryId);
        if ("0".equals(categoryVo.getCategoryListType())) {
            CategoryProductBo categoryProductBo = new CategoryProductBo();
            categoryProductBo.setCategoryId(categoryId);
            categoryVo.setProductIds(categoryProductService.queryList(categoryProductBo).stream().map(CategoryProductVo::getProductId).toArray(Long[]::new));
        }
        if ("1".equals(categoryVo.getCategoryListType())) {
            CategoryProductBo categoryProductBo = new CategoryProductBo();
            categoryProductBo.setCategoryId(categoryId);
            categoryVo.setCommercialTenantIds(categoryProductService.queryList(categoryProductBo).stream().map(CategoryProductVo::getProductId).toArray(Long[]::new));
        }
        return categoryVo;
    }

    /**
     * 查询栏目列表
     */
    @Override
    public List<CategoryVo> queryList(CategoryBo bo) {
        LambdaQueryWrapper<Category> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Category> buildQueryWrapper(CategoryBo bo) {
        LambdaQueryWrapper<Category> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCategoryName()), Category::getCategoryName, bo.getCategoryName());
        lqw.eq(StringUtils.isNotBlank(bo.getCategoryListType()), Category::getCategoryListType, bo.getCategoryListType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Category::getStatus, bo.getStatus());
        lqw.eq(bo.getParentId() != null, Category::getParentId, bo.getParentId());
        lqw.eq(bo.getSort() != null, Category::getSort, bo.getSort());
        lqw.eq(bo.getPlatformKey() != null, Category::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }

    /**
     * 新增栏目
     */
    @CacheEvict(cacheNames = CacheNames.CATEGORY_LIST, allEntries = true)
    @Override
    public Boolean insertByBo(CategoryBo bo) {
        Category add = BeanUtil.toBean(bo, Category.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCategoryId(add.getCategoryId());
            cateGoryProduct(bo, false);
        }
        return flag;
    }

    /**
     * 修改栏目
     */
    @CacheEvict(cacheNames = {CacheNames.CATEGORY_LIST, CacheNames.CATEGORY}, allEntries = true)
    @Override
    public Boolean updateByBo(CategoryBo bo) {
        Category update = BeanUtil.toBean(bo, Category.class);
        cateGoryProduct(bo, true);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除栏目
     */
    @CacheEvict(cacheNames = {CacheNames.CATEGORY_LIST, CacheNames.CATEGORY}, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (ObjectUtil.isEmpty(ids)) {
            return false;
        }
        // 删除对应的关联信息
        categoryProductService.remove(new LambdaQueryWrapper<CategoryProduct>().in(CategoryProduct::getCategoryId, ids));
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 新增 修改 更新栏目商品关联信息
     */
    private void cateGoryProduct(CategoryBo bo, boolean update) {
        if (update) {
            categoryProductService.remove(new LambdaQueryWrapper<CategoryProduct>().eq(CategoryProduct::getCategoryId, bo.getCategoryId()));
        }
        Long[] productIds = null;
        if ("0".equals(bo.getCategoryListType()) && ObjectUtil.isNotEmpty(bo.getProductIds())) {
            productIds = bo.getProductIds();
        }
        if ("1".equals(bo.getCategoryListType()) && ObjectUtil.isNotEmpty(bo.getCommercialTenantIds())) {
            productIds = bo.getCommercialTenantIds();
        }
        if (null == productIds) {
            return;
        }
        for (Long productId : productIds) {
            CategoryProductBo categoryProductBo = new CategoryProductBo();
            categoryProductBo.setCategoryId(bo.getCategoryId());
            categoryProductBo.setProductId(productId);
            categoryProductService.insertByBo(categoryProductBo);
        }
    }
}
