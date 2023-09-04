package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ShopGroup;
import com.ruoyi.zlyyh.domain.ShopGroupRelevance;
import com.ruoyi.zlyyh.domain.bo.ShopGroupBo;
import com.ruoyi.zlyyh.domain.bo.ShopGroupRelevanceBo;
import com.ruoyi.zlyyh.domain.vo.ShopGroupRelevanceVo;
import com.ruoyi.zlyyh.domain.vo.ShopGroupVo;
import com.ruoyi.zlyyh.mapper.ShopGroupMapper;
import com.ruoyi.zlyyh.mapper.ShopGroupRelevanceMapper;
import com.ruoyi.zlyyhadmin.service.IShopGroupRelevanceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.IShopGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.ShopGroupBo;
import com.ruoyi.zlyyh.domain.vo.ShopGroupVo;
import com.ruoyi.zlyyh.domain.ShopGroup;
import com.ruoyi.zlyyh.mapper.ShopGroupMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 门店组配置Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class ShopGroupServiceImpl implements IShopGroupService {

    private final ShopGroupMapper baseMapper;
    private final ShopGroupRelevanceMapper shopGroupRelevanceMapper;
    private final IShopGroupRelevanceService shopGroupRelevanceService;

    /**
     * 查询门店组配置
     */
    @Override
    public ShopGroupVo queryById(Long shopGroupId){
        ShopGroupVo shopGroupVo = baseMapper.selectVoById(shopGroupId);
        ShopGroupRelevanceBo shopGroupRelevanceBo = new ShopGroupRelevanceBo();
        shopGroupRelevanceBo.setShopGroupId(shopGroupId);
        shopGroupVo.setShopIds(shopGroupRelevanceService.queryList(shopGroupRelevanceBo).stream().map(ShopGroupRelevanceVo::getShopId).toArray(Long[]::new));
        return shopGroupVo;
    }

    /**
     * 查询门店组配置列表
     */
    @Override
    public TableDataInfo<ShopGroupVo> queryPageList(ShopGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopGroup> lqw = buildQueryWrapper(bo);
        Page<ShopGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询门店组配置列表
     */
    @Override
    public List<ShopGroupVo> queryList(ShopGroupBo bo) {
        LambdaQueryWrapper<ShopGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShopGroup> buildQueryWrapper(ShopGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShopGroup> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getShopGroupName()), ShopGroup::getShopGroupName, bo.getShopGroupName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ShopGroup::getStatus, bo.getStatus());
        lqw.eq(bo.getPlatformKey() != null, ShopGroup::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }

    /**
     * 新增门店组配置
     */
    @Override
    public Boolean insertByBo(ShopGroupBo bo) {
        ShopGroup add = BeanUtil.toBean(bo, ShopGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setShopGroupId(add.getShopGroupId());
        }
        if (ObjectUtil.isNotEmpty(bo.getShopIds())){
            for (Long shop : bo.getShopIds()) {
                ShopGroupRelevance shopGroupRelevance = new ShopGroupRelevance();
                shopGroupRelevance.setShopGroupId(add.getShopGroupId());
                shopGroupRelevance.setShopId(shop);
                shopGroupRelevanceMapper.insert(shopGroupRelevance);
            }
        }
        return flag;
    }

    /**
     * 修改门店组配置
     */
    @Override
    public Boolean updateByBo(ShopGroupBo bo) {
        ShopGroup update = BeanUtil.toBean(bo, ShopGroup.class);
        if (ObjectUtil.isNotEmpty(bo.getShopIds())) {
            shopGroupRelevanceService.remove(new LambdaQueryWrapper<ShopGroupRelevance>().eq(ShopGroupRelevance::getShopGroupId, bo.getShopGroupId()));
            Long[] shopIds = bo.getShopIds();
            for (Long shopId : shopIds) {
                ShopGroupRelevance shopGroupRelevance = new ShopGroupRelevance();
                shopGroupRelevance.setShopGroupId(bo.getShopGroupId());
                shopGroupRelevance.setShopId(shopId);
                shopGroupRelevanceMapper.insert(shopGroupRelevance);
            }
        }
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShopGroup entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除门店组配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
