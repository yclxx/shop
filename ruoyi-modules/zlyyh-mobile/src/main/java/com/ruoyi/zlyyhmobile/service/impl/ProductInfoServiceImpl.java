package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ProductInfo;
import com.ruoyi.zlyyh.domain.bo.ProductInfoBo;
import com.ruoyi.zlyyh.domain.vo.ProductInfoVo;
import com.ruoyi.zlyyh.mapper.ProductInfoMapper;
import com.ruoyi.zlyyhmobile.service.IProductInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商品拓展Service业务层处理
 *
 * @author yzg
 * @date 2023-05-15
 */
@RequiredArgsConstructor
@Service
public class ProductInfoServiceImpl implements IProductInfoService {

    private final ProductInfoMapper baseMapper;

    /**
     * 查询商品拓展
     */
    @Override
    public ProductInfoVo queryById(Long productId) {
        return baseMapper.selectVoById(productId);
    }

    /**
     * 查询商品拓展列表
     */
    @Override
    public TableDataInfo<ProductInfoVo> queryPageList(ProductInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProductInfo> lqw = buildQueryWrapper(bo);
        Page<ProductInfoVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商品拓展列表
     */
    @Override
    public List<ProductInfoVo> queryList(ProductInfoBo bo) {
        LambdaQueryWrapper<ProductInfo> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProductInfo> buildQueryWrapper(ProductInfoBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProductInfo> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getBrandName()), ProductInfo::getBrandName, bo.getBrandName());
        return lqw;
    }

    /**
     * 新增商品拓展
     */
    @Override
    public Boolean insertByBo(ProductInfoBo bo) {
        ProductInfo add = BeanUtil.toBean(bo, ProductInfo.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProductId(add.getProductId());
        }
        return flag;
    }

    /**
     * 修改商品拓展
     */
    @Override
    public Boolean updateByBo(ProductInfoBo bo) {
        ProductInfo update = BeanUtil.toBean(bo, ProductInfo.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductInfo entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除商品拓展
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
