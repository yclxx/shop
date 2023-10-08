package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ShopProduct;
import com.ruoyi.zlyyh.domain.bo.ShopProductBo;
import com.ruoyi.zlyyh.domain.vo.ShopProductVo;
import com.ruoyi.zlyyh.mapper.ShopProductMapper;
import com.ruoyi.zlyyhadmin.service.IShopProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品门店关联Service业务层处理
 *
 * @author yzg
 * @date 2023-05-16
 */
@RequiredArgsConstructor
@Service
public class ShopProductServiceImpl implements IShopProductService {

    private final ShopProductMapper baseMapper;

    /**
     * 查询商品门店关联
     */
    @Override
    public ShopProductVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询商品门店关联列表
     */
    @Override
    public TableDataInfo<ShopProductVo> queryPageList(ShopProductBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopProduct> lqw = buildQueryWrapper(bo);
        Page<ShopProductVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商品门店关联列表
     */
    @Override
    public List<ShopProductVo> queryList(ShopProductBo bo) {
        LambdaQueryWrapper<ShopProduct> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShopProduct> buildQueryWrapper(ShopProductBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShopProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getShopId() != null, ShopProduct::getShopId, bo.getShopId());
        lqw.eq(bo.getProductId() != null, ShopProduct::getProductId, bo.getProductId());
        lqw.eq(bo.getSort() != null, ShopProduct::getSort, bo.getSort());
        return lqw;
    }

    /**
     * 新增商品门店关联
     */
    @Override
    public Boolean insertByBo(ShopProductBo bo) {
        ShopProduct add = BeanUtil.toBean(bo, ShopProduct.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商品门店关联
     */
    @Override
    public Boolean updateByBo(ShopProductBo bo) {
        ShopProduct update = BeanUtil.toBean(bo, ShopProduct.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShopProduct entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除商品门店关联
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Integer deleteWithValidByShopId(Long shopId) {
        return baseMapper.delete(new LambdaQueryWrapper<ShopProduct>().eq(ShopProduct::getShopId, shopId));
    }

    @Override
    public List<ShopProductVo> queryByShopId(Long shopId) {
        return baseMapper.selectVoList(new LambdaQueryWrapper<ShopProduct>().eq(ShopProduct::getShopId, shopId));
    }

    @Override
    public Integer deleteByProductId(Long productId) {
        return baseMapper.delete(new LambdaQueryWrapper<ShopProduct>().eq(ShopProduct::getProductId, productId));
    }

    @Override
    public List<Long> queryByProductId(Long productId) {
        LambdaQueryWrapper<ShopProduct> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ShopProduct::getProductId, productId);
        return baseMapper.selectList(queryWrapper).stream().map(ShopProduct::getShopId).collect(Collectors.toList());
    }

    @Override
    public String queryCityCode(Long productId) {
        List<String> result = baseMapper.queryCityCode(productId);
        if(ObjectUtil.isEmpty(result)){
            return null;
        }
        return CollUtil.join(result,",");
    }
}
