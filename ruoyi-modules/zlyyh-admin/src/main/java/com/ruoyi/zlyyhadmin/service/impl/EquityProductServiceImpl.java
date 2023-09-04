package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.EquityProduct;
import com.ruoyi.zlyyh.domain.bo.EquityProductBo;
import com.ruoyi.zlyyh.domain.vo.EquityProductVo;
import com.ruoyi.zlyyh.mapper.EquityProductMapper;
import com.ruoyi.zlyyhadmin.service.IEquityProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 权益包商品Service业务层处理
 *
 * @author yzg
 * @date 2023-06-06
 */
@RequiredArgsConstructor
@Service
public class EquityProductServiceImpl implements IEquityProductService {

    private final EquityProductMapper baseMapper;

    /**
     * 查询权益包商品
     */
    @Override
    public EquityProductVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询权益包商品列表
     */
    @Override
    public TableDataInfo<EquityProductVo> queryPageList(EquityProductBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<EquityProduct> lqw = buildQueryWrapper(bo);
        Page<EquityProductVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询权益包商品列表
     */
    @Override
    public List<EquityProductVo> queryList(EquityProductBo bo) {
        LambdaQueryWrapper<EquityProduct> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<EquityProduct> buildQueryWrapper(EquityProductBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<EquityProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getEquityId() != null, EquityProduct::getEquityId, bo.getEquityId());
        lqw.eq(bo.getProductId() != null, EquityProduct::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getEquityType()), EquityProduct::getEquityType, bo.getEquityType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), EquityProduct::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增权益包商品
     */
    @CacheEvict(cacheNames = CacheNames.EQUITY_PRODUCT_LIST, key = "#bo.getEquityId()")
    @Override
    public Boolean insertByBo(EquityProductBo bo) {
        EquityProduct add = BeanUtil.toBean(bo, EquityProduct.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改权益包商品
     */
    @CacheEvict(cacheNames = CacheNames.EQUITY_PRODUCT_LIST, key = "#bo.getEquityId()")
    @Override
    public Boolean updateByBo(EquityProductBo bo) {
        EquityProduct update = BeanUtil.toBean(bo, EquityProduct.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(EquityProduct entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除权益包商品
     */
    @CacheEvict(cacheNames = CacheNames.EQUITY_PRODUCT_LIST, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
