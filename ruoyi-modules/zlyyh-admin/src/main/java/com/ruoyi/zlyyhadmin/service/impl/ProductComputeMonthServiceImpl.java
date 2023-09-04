package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ProductComputeMonth;
import com.ruoyi.zlyyh.domain.bo.ProductComputeMonthBo;
import com.ruoyi.zlyyh.domain.vo.ProductComputeMonthVo;
import com.ruoyi.zlyyh.mapper.ProductComputeMonthMapper;
import com.ruoyi.zlyyhadmin.service.IProductComputeMonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 订单数据统计（月份）Service业务层处理
 *
 * @author yzg
 * @date 2023-07-12
 */
@RequiredArgsConstructor
@Service
public class ProductComputeMonthServiceImpl implements IProductComputeMonthService {

    private final ProductComputeMonthMapper baseMapper;

    /**
     * 查询订单数据统计（月份）
     */
    @Override
    public ProductComputeMonthVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询订单数据统计（月份）列表
     */
    @Override
    public TableDataInfo<ProductComputeMonthVo> queryPageList(ProductComputeMonthBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProductComputeMonth> lqw = buildQueryWrapper(bo);
        Page<ProductComputeMonthVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询订单数据统计（月份）列表
     */
    @Override
    public List<ProductComputeMonthVo> queryList(ProductComputeMonthBo bo) {
        LambdaQueryWrapper<ProductComputeMonth> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProductComputeMonth> buildQueryWrapper(ProductComputeMonthBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProductComputeMonth> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getMonth()), ProductComputeMonth::getMonth, bo.getMonth());
        lqw.eq(bo.getProductId() != null, ProductComputeMonth::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getCityCode()), ProductComputeMonth::getCityCode, bo.getCityCode());
        lqw.like(StringUtils.isNotBlank(bo.getCityName()), ProductComputeMonth::getCityName, bo.getCityName());
        return lqw;
    }

    /**
     * 新增订单数据统计（月份）
     */
    @Override
    public Boolean insertByBo(ProductComputeMonthBo bo) {
        ProductComputeMonth add = BeanUtil.toBean(bo, ProductComputeMonth.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改订单数据统计（月份）
     */
    @Override
    public Boolean updateByBo(ProductComputeMonthBo bo) {
        ProductComputeMonth update = BeanUtil.toBean(bo, ProductComputeMonth.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductComputeMonth entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除订单数据统计（月份）
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
