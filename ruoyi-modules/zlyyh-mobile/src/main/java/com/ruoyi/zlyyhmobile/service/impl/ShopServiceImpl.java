package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.mapper.ShopMapper;
import com.ruoyi.zlyyhmobile.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 门店Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class ShopServiceImpl implements IShopService {

    private final ShopMapper baseMapper;

    /**
     * 查询门店
     */
    @Cacheable(cacheNames = CacheNames.SHOP, key = "#shopId")
    @Override
    public ShopVo queryById(Long shopId) {
        return baseMapper.selectVoById(shopId);
    }

//    /**
//     * 查询门店列表
//     */
//    @Override
//    public TableDataInfo<ShopVo> queryPageList(ShopBo bo, PageQuery pageQuery) {
//        if (ObjectUtils.isEmpty(bo.getCommercialTenantId())) {
//            return TableDataInfo.build(new ArrayList<>());
//        }
//        bo.setCitycode(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
//        Page<ShopVo> result = baseMapper.selectShopList(pageQuery.build(), bo);
//        return TableDataInfo.build(result);
//    }

    /**
     * 查询门店列表
     */
    @Override
    public TableDataInfo<ShopVo> queryPageList(ShopBo bo, PageQuery pageQuery) {
        pageQuery.setOrderByColumn(null);
        pageQuery.setIsAsc(null);
        Page<ShopVo> result = baseMapper.selectShopList(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询门店列表
     */
    @Override
    public TableDataInfo<ShopVo> getShopListByProductId(ShopBo bo, PageQuery pageQuery) {
        pageQuery.setOrderByColumn(null);
        pageQuery.setIsAsc(null);
        Page<ShopVo> result = baseMapper.selectShopListByProductId(pageQuery.build(), bo);
        if (result.getTotal() == 0) {
            bo.setCitycode(null);
            result = baseMapper.selectShopListByProductId(pageQuery.build(), bo);
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询门店列表
     */
    @Override
    public TableDataInfo<ShopVo> selectShopListByCommercialTenantId(ShopBo bo, PageQuery pageQuery) {
        pageQuery.setOrderByColumn(null);
        pageQuery.setIsAsc(null);
        Page<ShopVo> result = baseMapper.selectShopListByCommercialTenantId(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商户门店
     *
     * @param commercialIds 商户ID
     * @return 门店集合
     */
    @Override
    public List<ShopVo> queryListByCommercialIds(List<Long> commercialIds, String cityCode) {
        if (ObjectUtil.isEmpty(commercialIds)) {
            return new ArrayList<>();
        }
        return baseMapper.selectVoList(new LambdaQueryWrapper<Shop>().in(Shop::getCommercialTenantId, commercialIds).eq(Shop::getCitycode, cityCode));
    }
}
