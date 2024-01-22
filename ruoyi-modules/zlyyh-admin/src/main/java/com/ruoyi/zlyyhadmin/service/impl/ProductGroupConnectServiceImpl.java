package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.IProductGroupConnectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.ProductGroupConnectBo;
import com.ruoyi.zlyyh.domain.vo.ProductGroupConnectVo;
import com.ruoyi.zlyyh.domain.ProductGroupConnect;
import com.ruoyi.zlyyh.mapper.ProductGroupConnectMapper;


import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 商品商品组关联Service业务层处理
 *
 * @author yzg
 * @date 2024-01-16
 */
@RequiredArgsConstructor
@Service
public class ProductGroupConnectServiceImpl implements IProductGroupConnectService {

    private final ProductGroupConnectMapper baseMapper;

    /**
     * 查询商品商品组关联
     */
    @Override
    public ProductGroupConnectVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询商品商品组关联列表
     */
    @Override
    public TableDataInfo<ProductGroupConnectVo> queryPageList(ProductGroupConnectBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProductGroupConnect> lqw = buildQueryWrapper(bo);
        Page<ProductGroupConnectVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商品商品组关联列表
     */
    @Override
    public List<ProductGroupConnectVo> queryList(ProductGroupConnectBo bo) {
        LambdaQueryWrapper<ProductGroupConnect> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProductGroupConnect> buildQueryWrapper(ProductGroupConnectBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProductGroupConnect> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductGroupId() != null, ProductGroupConnect::getProductGroupId, bo.getProductGroupId());
        lqw.eq(bo.getProductId() != null, ProductGroupConnect::getProductId, bo.getProductId());
        return lqw;
    }

    /**
     * 新增商品商品组关联
     */
    @Override
    public Boolean insertByBo(ProductGroupConnectBo bo) {
        ProductGroupConnect add = BeanUtil.toBean(bo, ProductGroupConnect.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商品商品组关联
     */
    @Override
    public Boolean updateByBo(ProductGroupConnectBo bo) {
        ProductGroupConnect update = BeanUtil.toBean(bo, ProductGroupConnect.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductGroupConnect entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除商品商品组关联
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
