package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhmobile.service.IProductGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.ProductGroupBo;
import com.ruoyi.zlyyh.domain.vo.ProductGroupVo;
import com.ruoyi.zlyyh.domain.ProductGroup;
import com.ruoyi.zlyyh.mapper.ProductGroupMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 商品组规则配置Service业务层处理
 *
 * @author yzg
 * @date 2024-01-16
 */
@RequiredArgsConstructor
@Service
public class ProductGroupServiceImpl implements IProductGroupService {

    private final ProductGroupMapper baseMapper;

    /**
     * 查询商品组规则配置
     */
    @Override
    public ProductGroupVo queryById(Long productGroupId){
        return baseMapper.selectVoById(productGroupId);
    }

    /**
     * 查询商品组规则配置列表
     */
    @Override
    public TableDataInfo<ProductGroupVo> queryPageList(ProductGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProductGroup> lqw = buildQueryWrapper(bo);
        Page<ProductGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商品组规则配置列表
     */
    @Override
    public List<ProductGroupVo> queryList(ProductGroupBo bo) {
        LambdaQueryWrapper<ProductGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProductGroup> buildQueryWrapper(ProductGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProductGroup> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getProductGroupName()), ProductGroup::getProductGroupName, bo.getProductGroupName());
        lqw.eq(StringUtils.isNotBlank(bo.getProductGroupTip()), ProductGroup::getProductGroupTip, bo.getProductGroupTip());
        lqw.eq(bo.getDayUserCount() != null, ProductGroup::getDayUserCount, bo.getDayUserCount());
        lqw.eq(bo.getWeekUserCount() != null, ProductGroup::getWeekUserCount, bo.getWeekUserCount());
        lqw.eq(bo.getMonthUserCount() != null, ProductGroup::getMonthUserCount, bo.getMonthUserCount());
        lqw.eq(bo.getTotalUserCount() != null, ProductGroup::getTotalUserCount, bo.getTotalUserCount());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ProductGroup::getStatus, bo.getStatus());
        return lqw;
    }



}
