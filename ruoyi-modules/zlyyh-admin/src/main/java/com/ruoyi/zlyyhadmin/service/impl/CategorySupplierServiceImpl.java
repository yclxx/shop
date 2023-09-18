package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.CategorySupplier;
import com.ruoyi.zlyyh.domain.bo.CategorySupplierBo;
import com.ruoyi.zlyyh.domain.vo.CategorySupplierVo;
import com.ruoyi.zlyyh.mapper.CategorySupplierMapper;
import com.ruoyi.zlyyhadmin.service.ICategorySupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 供应商产品分类Service业务层处理
 *
 * @author yzg
 * @date 2023-09-15
 */
@RequiredArgsConstructor
@Service
public class CategorySupplierServiceImpl implements ICategorySupplierService {

    private final CategorySupplierMapper baseMapper;

    /**
     * 查询供应商产品分类
     */
    @Override
    public CategorySupplierVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public CategorySupplier queryBySupplierId(String supplierId, String fullName) {
        LambdaQueryWrapper<CategorySupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategorySupplier::getSupplierId, supplierId);
        queryWrapper.eq(CategorySupplier::getFullName, fullName);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询供应商产品分类列表
     */
    @Override
    public TableDataInfo<CategorySupplierVo> queryPageList(CategorySupplierBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CategorySupplier> lqw = buildQueryWrapper(bo);
        Page<CategorySupplierVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询供应商产品分类列表
     */
    @Override
    public List<CategorySupplierVo> queryList(CategorySupplierBo bo) {
        LambdaQueryWrapper<CategorySupplier> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CategorySupplier> buildQueryWrapper(CategorySupplierBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<CategorySupplier> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getSupplierId()), CategorySupplier::getSupplierId, bo.getSupplierId());
        lqw.like(StringUtils.isNotBlank(bo.getSupplierName()), CategorySupplier::getSupplierName, bo.getSupplierName());
        lqw.like(StringUtils.isNotBlank(bo.getFullName()), CategorySupplier::getFullName, bo.getFullName());
        lqw.eq(StringUtils.isNotBlank(bo.getCategoryId()), CategorySupplier::getCategoryId, bo.getCategoryId());
        return lqw;
    }

    /**
     * 新增供应商产品分类
     */
    @Override
    public Boolean insertByBo(CategorySupplierBo bo) {
        CategorySupplier add = BeanUtil.toBean(bo, CategorySupplier.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    @Override
    public Boolean insert(CategorySupplier add) {
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            add.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改供应商产品分类
     */
    @Override
    public Boolean updateByBo(CategorySupplierBo bo) {
        CategorySupplier update = BeanUtil.toBean(bo, CategorySupplier.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CategorySupplier entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除供应商产品分类
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
