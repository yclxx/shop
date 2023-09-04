package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ProductPackage;
import com.ruoyi.zlyyh.domain.bo.ProductPackageBo;
import com.ruoyi.zlyyh.domain.vo.ProductPackageVo;
import com.ruoyi.zlyyh.mapper.ProductPackageMapper;
import com.ruoyi.zlyyhadmin.service.IProductPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商品券包Service业务层处理
 *
 * @author yzg
 * @date 2023-06-30
 */
@RequiredArgsConstructor
@Service
public class ProductPackageServiceImpl implements IProductPackageService {

    private final ProductPackageMapper baseMapper;

    /**
     * 查询商品券包
     */
    @Override
    public ProductPackageVo queryById(Long packageId) {
        return baseMapper.selectVoById(packageId);
    }

    /**
     * 查询商品券包列表
     */
    @Override
    public TableDataInfo<ProductPackageVo> queryPageList(ProductPackageBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProductPackage> lqw = buildQueryWrapper(bo);
        Page<ProductPackageVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商品券包列表
     */
    @Override
    public List<ProductPackageVo> queryList(ProductPackageBo bo) {
        LambdaQueryWrapper<ProductPackage> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProductPackage> buildQueryWrapper(ProductPackageBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProductPackage> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, ProductPackage::getProductId, bo.getProductId());
        lqw.eq(bo.getExtProductId() != null, ProductPackage::getExtProductId, bo.getExtProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ProductPackage::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增商品券包
     */
    @CacheEvict(cacheNames = CacheNames.ProductPackage, key = "#bo.getProductId()")
    @Override
    public Boolean insertByBo(ProductPackageBo bo) {
        ProductPackage add = BeanUtil.toBean(bo, ProductPackage.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setPackageId(add.getPackageId());
        }
        return flag;
    }

    /**
     * 修改商品券包
     */
    @CacheEvict(cacheNames = CacheNames.ProductPackage, key = "#bo.getProductId()")
    @Override
    public Boolean updateByBo(ProductPackageBo bo) {
        ProductPackage update = BeanUtil.toBean(bo, ProductPackage.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductPackage entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除商品券包
     */
    @CacheEvict(cacheNames = CacheNames.ProductPackage, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
