package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.Category;
import com.ruoyi.zlyyh.domain.bo.CategoryBo;
import com.ruoyi.zlyyh.domain.vo.CategoryVo;
import com.ruoyi.zlyyh.mapper.CategoryMapper;
import com.ruoyi.zlyyhmobile.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 栏目Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryMapper baseMapper;

    /**
     * 查询栏目
     */
    @Cacheable(cacheNames = CacheNames.CATEGORY, key = "#categoryId")
    @Override
    public CategoryVo queryById(Long categoryId) {
        return baseMapper.selectVoById(categoryId);
    }

    /**
     * 查询栏目列表
     */
    @Cacheable(cacheNames = CacheNames.CATEGORY_LIST, key = "#bo.getPlatformKey()+'-'+#bo.getSupportChannel()+'-'+#bo.getParentId()+'-'+#bo.getShowCity()+'-'+#bo.getWeekDate()+'-'+#bo.getShowIndex()")
    @Override
    public List<CategoryVo> queryList(CategoryBo bo) {
        LambdaQueryWrapper<Category> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Category> buildQueryWrapper(CategoryBo bo) {
        LambdaQueryWrapper<Category> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getParentId() != null, Category::getParentId, bo.getParentId());
        lqw.eq(Category::getStatus, "0");
        lqw.eq(bo.getPlatformKey() != null, Category::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getShowIndex()), Category::getShowIndex, bo.getShowIndex());
        if (StringUtils.isNotBlank(bo.getShowCity())) {
            lqw.and(lm -> {
                lm.eq(Category::getShowCity, "ALL").or().like(Category::getShowCity, bo.getShowCity());
            });
        }
        if (StringUtils.isNotBlank(bo.getSupportChannel())) {
            lqw.and(lm -> {
                lm.eq(Category::getSupportChannel, "ALL").or().like(Category::getSupportChannel, bo.getSupportChannel());
            });
        }
        if (StringUtils.isNotBlank(bo.getWeekDate())) {
            lqw.and(lm -> {
                lm.eq(Category::getAssignDate, "0").or().like(Category::getWeekDate, bo.getWeekDate());
            });
        }
        lqw.last("order by sort asc");
        return lqw;
    }

}
