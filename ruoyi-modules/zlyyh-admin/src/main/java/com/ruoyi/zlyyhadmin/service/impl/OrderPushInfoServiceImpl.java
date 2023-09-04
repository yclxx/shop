package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.IOrderPushInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.OrderPushInfoBo;
import com.ruoyi.zlyyh.domain.vo.OrderPushInfoVo;
import com.ruoyi.zlyyh.domain.OrderPushInfo;
import com.ruoyi.zlyyh.mapper.OrderPushInfoMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 订单取码记录Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class OrderPushInfoServiceImpl implements IOrderPushInfoService {

    private final OrderPushInfoMapper baseMapper;

    /**
     * 查询订单取码记录
     */
    @Override
    public OrderPushInfoVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询订单取码记录列表
     */
    @Override
    public TableDataInfo<OrderPushInfoVo> queryPageList(OrderPushInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderPushInfo> lqw = buildQueryWrapper(bo);
        Page<OrderPushInfoVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询订单取码记录列表
     */
    @Override
    public List<OrderPushInfoVo> queryList(OrderPushInfoBo bo) {
        LambdaQueryWrapper<OrderPushInfo> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderPushInfo> buildQueryWrapper(OrderPushInfoBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderPushInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getNumber() != null, OrderPushInfo::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getPushNumber()), OrderPushInfo::getPushNumber, bo.getPushNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getExternalOrderNumber()), OrderPushInfo::getExternalOrderNumber, bo.getExternalOrderNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getExternalProductId()), OrderPushInfo::getExternalProductId, bo.getExternalProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), OrderPushInfo::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增订单取码记录
     */
    @Override
    public Boolean insertByBo(OrderPushInfoBo bo) {
        OrderPushInfo add = BeanUtil.toBean(bo, OrderPushInfo.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改订单取码记录
     */
    @Override
    public Boolean updateByBo(OrderPushInfoBo bo) {
        OrderPushInfo update = BeanUtil.toBean(bo, OrderPushInfo.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderPushInfo entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除订单取码记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
