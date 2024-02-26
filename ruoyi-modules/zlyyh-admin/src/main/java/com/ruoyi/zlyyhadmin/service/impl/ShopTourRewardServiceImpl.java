package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.ShopTourRewardBo;
import com.ruoyi.zlyyh.domain.vo.ShopTourRewardVo;
import com.ruoyi.zlyyh.domain.ShopTourReward;
import com.ruoyi.zlyyh.mapper.ShopTourRewardMapper;
import com.ruoyi.zlyyhadmin.service.IShopTourRewardService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 巡检奖励Service业务层处理
 *
 * @author yzg
 * @date 2024-01-28
 */
@RequiredArgsConstructor
@Service
public class ShopTourRewardServiceImpl implements IShopTourRewardService {

    private final ShopTourRewardMapper baseMapper;

    /**
     * 查询巡检奖励
     */
    @Override
    public ShopTourRewardVo queryById(Long tourRewardId){
        return baseMapper.selectVoById(tourRewardId);
    }

    /**
     * 查询巡检奖励列表
     */
    @Override
    public TableDataInfo<ShopTourRewardVo> queryPageList(ShopTourRewardBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopTourReward> lqw = buildQueryWrapper(bo);
        Page<ShopTourRewardVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询巡检奖励列表
     */
    @Override
    public List<ShopTourRewardVo> queryList(ShopTourRewardBo bo) {
        LambdaQueryWrapper<ShopTourReward> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShopTourReward> buildQueryWrapper(ShopTourRewardBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShopTourReward> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getVerifierId() != null, ShopTourReward::getVerifierId, bo.getVerifierId());
        lqw.eq(bo.getCount() != null, ShopTourReward::getCount, bo.getCount());
        lqw.eq(bo.getAmount() != null, ShopTourReward::getAmount, bo.getAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ShopTourReward::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增巡检奖励
     */
    @Override
    public Boolean insertByBo(ShopTourRewardBo bo) {
        ShopTourReward add = BeanUtil.toBean(bo, ShopTourReward.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTourRewardId(add.getTourRewardId());
        }
        return flag;
    }

    /**
     * 修改巡检奖励
     */
    @Override
    public Boolean updateByBo(ShopTourRewardBo bo) {
        ShopTourReward update = BeanUtil.toBean(bo, ShopTourReward.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShopTourReward entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除巡检奖励
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
