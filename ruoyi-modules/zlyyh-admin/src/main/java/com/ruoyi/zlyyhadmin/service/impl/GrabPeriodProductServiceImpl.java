package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.GrabPeriodProduct;
import com.ruoyi.zlyyh.domain.bo.GrabPeriodProductBo;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodProductVo;
import com.ruoyi.zlyyh.mapper.GrabPeriodProductMapper;
import com.ruoyi.zlyyhadmin.service.IGrabPeriodProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 秒杀商品配置Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class GrabPeriodProductServiceImpl implements IGrabPeriodProductService {

    private final GrabPeriodProductMapper baseMapper;

    /**
     * 查询秒杀商品配置
     */
    @Override
    public GrabPeriodProductVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询秒杀商品配置列表
     */
    @Override
    public TableDataInfo<GrabPeriodProductVo> queryPageList(GrabPeriodProductBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<GrabPeriodProduct> lqw = buildQueryWrapper(bo);
        Page<GrabPeriodProductVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询秒杀商品配置列表
     */
    @Override
    public List<GrabPeriodProductVo> queryList(GrabPeriodProductBo bo) {
        LambdaQueryWrapper<GrabPeriodProduct> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<GrabPeriodProduct> buildQueryWrapper(GrabPeriodProductBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<GrabPeriodProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getGrabPeriodId() != null, GrabPeriodProduct::getGrabPeriodId, bo.getGrabPeriodId());
        lqw.eq(bo.getProductId() != null, GrabPeriodProduct::getProductId, bo.getProductId());
        lqw.eq(bo.getSort() != null, GrabPeriodProduct::getSort, bo.getSort());
        return lqw;
    }

    /**
     * 新增秒杀商品配置
     */
    @CacheEvict(cacheNames = CacheNames.GRAB_PERIOD_PRODUCT, key = "#bo.getGrabPeriodId()")
    @Override
    public Boolean insertByBo(GrabPeriodProductBo bo) {
        String productIds = bo.getProductIds();
        String[] split = productIds.split(",");
        //查询秒杀
        for (String s : split) {
            LambdaQueryWrapper<GrabPeriodProduct> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(GrabPeriodProduct::getGrabPeriodId, bo.getGrabPeriodId());
            wrapper.eq(GrabPeriodProduct::getProductId, s);
            wrapper.last("limit 1");
            GrabPeriodProduct grabPeriodProduct = baseMapper.selectOne(wrapper);
            if (null != grabPeriodProduct) {
                continue;
            }
            grabPeriodProduct = new GrabPeriodProduct();
            grabPeriodProduct.setGrabPeriodId(bo.getGrabPeriodId());
            grabPeriodProduct.setProductId(Long.parseLong(s));
            grabPeriodProduct.setSort(bo.getSort());
            baseMapper.insert(grabPeriodProduct);
        }
        return true;
    }

    /**
     * 修改秒杀商品配置
     */
    @CacheEvict(cacheNames = CacheNames.GRAB_PERIOD_PRODUCT, key = "#bo.getGrabPeriodId()")
    @Override
    public Boolean updateByBo(GrabPeriodProductBo bo) {
        GrabPeriodProduct grabPeriodProduct = baseMapper.selectById(bo.getId());
        if (!Objects.equals(grabPeriodProduct.getGrabPeriodId(), bo.getGrabPeriodId()) || !Objects.equals(grabPeriodProduct.getProductId(), bo.getProductId())) {
            LambdaQueryWrapper<GrabPeriodProduct> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(GrabPeriodProduct::getGrabPeriodId, bo.getGrabPeriodId());
            wrapper.eq(GrabPeriodProduct::getProductId, bo.getProductId());
            Long count = baseMapper.selectCount(wrapper);
            if (count > 0) {
                throw new ServiceException("该商品已存在于秒杀活动中");
            }
        }
        GrabPeriodProduct update = BeanUtil.toBean(bo, GrabPeriodProduct.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除秒杀商品配置
     */
    @CacheEvict(cacheNames = CacheNames.GRAB_PERIOD_PRODUCT, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
