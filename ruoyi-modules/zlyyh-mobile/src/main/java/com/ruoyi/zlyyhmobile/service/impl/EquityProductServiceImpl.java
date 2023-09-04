package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.EquityProduct;
import com.ruoyi.zlyyh.domain.bo.EquityProductBo;
import com.ruoyi.zlyyh.domain.vo.EquityProductVo;
import com.ruoyi.zlyyh.mapper.EquityProductMapper;
import com.ruoyi.zlyyhmobile.service.IEquityProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Cacheable(cacheNames = CacheNames.EQUITY_PRODUCT_LIST, key = "#bo.getEquityId()")
    @Override
    public List<EquityProductVo> queryList(EquityProductBo bo) {
        LambdaQueryWrapper<EquityProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getEquityId() != null, EquityProduct::getEquityId, bo.getEquityId());
        return baseMapper.selectVoList(lqw);
    }
}
