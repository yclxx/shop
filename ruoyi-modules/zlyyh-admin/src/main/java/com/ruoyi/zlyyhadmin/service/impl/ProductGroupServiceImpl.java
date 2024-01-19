package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.ProductAction;
import com.ruoyi.zlyyh.domain.ProductGroupConnect;
import com.ruoyi.zlyyh.mapper.ProductGroupConnectMapper;
import com.ruoyi.zlyyhadmin.service.IProductGroupService;
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
    private final ProductGroupConnectMapper productGroupConnectMapper;

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

    /**
     * 新增商品组规则配置
     */
    @Override
    public Boolean insertByBo(ProductGroupBo bo) {
        ProductGroup add = BeanUtil.toBean(bo, ProductGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProductGroupId(add.getProductGroupId());
        }
        return flag;
    }

    /**
     * 修改商品组规则配置
     */
    @Override
    public Boolean updateByBo(ProductGroupBo bo) {
        ProductGroup update = BeanUtil.toBean(bo, ProductGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 关联商品商品组
     * @param productIds
     * @param productGroupId
     * @param type
     * @return
     */
    @Override
    public int updateGroupProduct(List<Long> productIds, Long productGroupId, Integer type) {
        if (ObjectUtil.isEmpty(productIds)) throw new ServiceException("商品信息无效");
        if (ObjectUtil.isEmpty(productGroupId)) throw new ServiceException("此商品组信息无效");

        if (type.equals(1)) {
            //查询商品id是否已经存在已有组中若存在  抛出异常 删除商品时不管了
            List<ProductGroupConnect> productGroupConnects = productGroupConnectMapper.selectList(new LambdaQueryWrapper<ProductGroupConnect>().in(ProductGroupConnect::getProductId, productIds));
            if (ObjectUtil.isNotEmpty(productGroupConnects)){
                throw new ServiceException("选择商品已添加过其他商品组规则，请删除重试");
            }
            for (Long productId : productIds) {
                ProductGroupConnect productGroupConnect = new ProductGroupConnect();
                productGroupConnect.setProductId(productId);
                productGroupConnect.setProductGroupId(productGroupId);
                productGroupConnectMapper.insert(productGroupConnect);
            }
        } else if (type.equals(0)) {
            for (Long productId : productIds) {
                LambdaQueryWrapper<ProductGroupConnect> wrapper = Wrappers.lambdaQuery();
                wrapper.eq(ProductGroupConnect::getProductGroupId, productGroupId);
                wrapper.eq(ProductGroupConnect::getProductId, productId);
                productGroupConnectMapper.delete(wrapper);
            }
        }
        return productIds.size();
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductGroup entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除商品组规则配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
