package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Action;
import com.ruoyi.zlyyh.domain.bo.ActionBo;
import com.ruoyi.zlyyh.domain.vo.ActionVo;
import com.ruoyi.zlyyh.mapper.ActionMapper;
import com.ruoyi.zlyyhadmin.service.IActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券批次Service业务层处理
 *
 * @author yzg
 * @date 2023-10-12
 */
@RequiredArgsConstructor
@Service
public class ActionServiceImpl implements IActionService {

    private final ActionMapper baseMapper;

    /**
     * 查询优惠券批次
     */
    @Override
    public ActionVo queryById(Long actionId){
        return baseMapper.selectVoById(actionId);
    }

    /**
     * 查询优惠券批次列表
     */
    @Override
    public TableDataInfo<ActionVo> queryPageList(ActionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Action> lqw = buildQueryWrapper(bo);
        Page<ActionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询优惠券批次列表
     */
    @Override
    public List<ActionVo> queryList(ActionBo bo) {
        LambdaQueryWrapper<Action> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Action> buildQueryWrapper(ActionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Action> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getActionNo()), Action::getActionNo, bo.getActionNo());
        lqw.eq(StringUtils.isNotBlank(bo.getActionNote()), Action::getActionNote, bo.getActionNote());
        lqw.like(StringUtils.isNotBlank(bo.getCouponName()), Action::getCouponName, bo.getCouponName());
        lqw.eq(StringUtils.isNotBlank(bo.getCouponType()), Action::getCouponType, bo.getCouponType());
        lqw.eq(bo.getPeriodOfStart() != null, Action::getPeriodOfStart, bo.getPeriodOfStart());
        lqw.eq(bo.getPeriodOfValidity() != null, Action::getPeriodOfValidity, bo.getPeriodOfValidity());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Action::getStatus, bo.getStatus());
        lqw.eq(bo.getConversionStartDate() != null, Action::getConversionStartDate, bo.getConversionStartDate());
        lqw.eq(bo.getConversionEndDate() != null, Action::getConversionEndDate, bo.getConversionEndDate());
        lqw.eq(bo.getPlatformKey() != null, Action::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }

    /**
     * 新增优惠券批次
     */
    @Override
    public Boolean insertByBo(ActionBo bo) {
        Action add = BeanUtil.toBean(bo, Action.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setActionId(add.getActionId());
        }
        return flag;
    }

    /**
     * 修改优惠券批次
     */
    @Override
    public Boolean updateByBo(ActionBo bo) {
        Action update = BeanUtil.toBean(bo, Action.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Action entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除优惠券批次
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
