package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.CategoryPlatformProduct;
import com.ruoyi.zlyyh.domain.CategoryProduct;
import com.ruoyi.zlyyhadmin.service.AsyncService;
import com.ruoyi.zlyyhadmin.service.ICategoryPlatformProductService;
import com.ruoyi.zlyyhadmin.service.ICategoryPlatformService;
import com.ruoyi.zlyyhadmin.service.ICategoryProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.CategoryPlatformBo;
import com.ruoyi.zlyyh.domain.vo.CategoryPlatformVo;
import com.ruoyi.zlyyh.domain.CategoryPlatform;
import com.ruoyi.zlyyh.mapper.CategoryPlatformMapper;


import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 多平台类别Service业务层处理
 *
 * @author yzg
 * @date 2024-02-27
 */
@RequiredArgsConstructor
@Service
public class CategoryPlatformServiceImpl implements ICategoryPlatformService {

    private final CategoryPlatformMapper baseMapper;
    private final ICategoryProductService categoryProductService;
    private final ICategoryPlatformProductService categoryPlatformProductService;
    private final AsyncService asyncService;

    /**
     * 查询多平台类别
     */
    @Override
    public CategoryPlatformVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询多平台类别列表
     */
    @Override
    public TableDataInfo<CategoryPlatformVo> queryPageList(CategoryPlatformBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CategoryPlatform> lqw = buildQueryWrapper(bo);
        Page<CategoryPlatformVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询多平台类别列表
     */
    @Override
    public List<CategoryPlatformVo> queryList(CategoryPlatformBo bo) {
        LambdaQueryWrapper<CategoryPlatform> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CategoryPlatform> buildQueryWrapper(CategoryPlatformBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<CategoryPlatform> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), CategoryPlatform::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCategoryIds()), CategoryPlatform::getCategoryIds, bo.getCategoryIds());
        return lqw;
    }

    /**
     * 新增多平台类别
     */
    @Override
    public Boolean insertByBo(CategoryPlatformBo bo) {
        CategoryPlatform add = BeanUtil.toBean(bo, CategoryPlatform.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改多平台类别
     */
    @Override
    public Boolean updateByBo(CategoryPlatformBo bo) {
        CategoryPlatform update = BeanUtil.toBean(bo, CategoryPlatform.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CategoryPlatform entity){
        //在新增或者修改操作时 如果有类别id 则将类别关联至平台类别下的商品中
        String categoryIds = entity.getCategoryIds();
        if (ObjectUtil.isNotEmpty(categoryIds)){
            //异步处理
            asyncService.categoryPlatformToCategory(entity.getId(),categoryIds);
        }
    }


    /**
     * 批量删除多平台类别
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        // 删除对应的关联信息
        categoryPlatformProductService.remove(new LambdaQueryWrapper<CategoryPlatformProduct>().in(CategoryPlatformProduct::getCategoryPlatformId, ids));
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
