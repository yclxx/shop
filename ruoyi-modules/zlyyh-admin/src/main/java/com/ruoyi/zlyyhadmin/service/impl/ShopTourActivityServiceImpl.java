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
import com.ruoyi.zlyyh.domain.bo.ShopTourActivityBo;
import com.ruoyi.zlyyh.domain.vo.ShopTourActivityVo;
import com.ruoyi.zlyyh.domain.ShopTourActivity;
import com.ruoyi.zlyyh.mapper.ShopTourActivityMapper;
import com.ruoyi.zlyyhadmin.service.IShopTourActivityService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 巡检活动Service业务层处理
 *
 * @author yzg
 * @date 2024-03-01
 */
@RequiredArgsConstructor
@Service
public class ShopTourActivityServiceImpl implements IShopTourActivityService {

    private final ShopTourActivityMapper baseMapper;

    /**
     * 查询巡检活动
     */
    @Override
    public ShopTourActivityVo queryById(Long tourActivityId){
        return baseMapper.selectVoById(tourActivityId);
    }

    /**
     * 查询巡检活动列表
     */
    @Override
    public TableDataInfo<ShopTourActivityVo> queryPageList(ShopTourActivityBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopTourActivity> lqw = buildQueryWrapper(bo);
        Page<ShopTourActivityVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询巡检活动列表
     */
    @Override
    public List<ShopTourActivityVo> queryList(ShopTourActivityBo bo) {
        LambdaQueryWrapper<ShopTourActivity> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShopTourActivity> buildQueryWrapper(ShopTourActivityBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShopTourActivity> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTourActivityName()), ShopTourActivity::getTourActivityName, bo.getTourActivityName());
        lqw.eq(bo.getStartDate() != null, ShopTourActivity::getStartDate, bo.getStartDate());
        lqw.eq(bo.getEndDate() != null, ShopTourActivity::getEndDate, bo.getEndDate());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ShopTourActivity::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增巡检活动
     */
    @Override
    public Boolean insertByBo(ShopTourActivityBo bo) {
        ShopTourActivity add = BeanUtil.toBean(bo, ShopTourActivity.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTourActivityId(add.getTourActivityId());
        }
        return flag;
    }

    /**
     * 修改巡检活动
     */
    @Override
    public Boolean updateByBo(ShopTourActivityBo bo) {
        ShopTourActivity update = BeanUtil.toBean(bo, ShopTourActivity.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShopTourActivity entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除巡检活动
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
