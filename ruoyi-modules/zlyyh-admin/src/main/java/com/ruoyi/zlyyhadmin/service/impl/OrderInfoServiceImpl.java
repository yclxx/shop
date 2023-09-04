package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.IOrderInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.OrderInfoBo;
import com.ruoyi.zlyyh.domain.vo.OrderInfoVo;
import com.ruoyi.zlyyh.domain.OrderInfo;
import com.ruoyi.zlyyh.mapper.OrderInfoMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 订单扩展信息Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class OrderInfoServiceImpl implements IOrderInfoService {

    private final OrderInfoMapper baseMapper;

    /**
     * 查询订单扩展信息
     */
    @Override
    public OrderInfoVo queryById(Long number) {
        return baseMapper.selectVoById(number);
    }

    /**
     * 查询订单扩展信息列表
     */
    @Override
    public TableDataInfo<OrderInfoVo> queryPageList(OrderInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderInfo> lqw = buildQueryWrapper(bo);
        Page<OrderInfoVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询订单扩展信息列表
     */
    @Override
    public List<OrderInfoVo> queryList(OrderInfoBo bo) {
        LambdaQueryWrapper<OrderInfo> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderInfo> buildQueryWrapper(OrderInfoBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(null != bo.getNumber(), OrderInfo::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getVip62Status()), OrderInfo::getVip62Status, bo.getVip62Status());
        lqw.eq(StringUtils.isNotBlank(bo.getVip62MemberType()), OrderInfo::getVip62MemberType, bo.getVip62MemberType());
        lqw.eq(StringUtils.isNotBlank(bo.getVip62EndTime()), OrderInfo::getVip62EndTime, bo.getVip62EndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getVip62BeginTime()), OrderInfo::getVip62BeginTime, bo.getVip62BeginTime());
        lqw.eq(StringUtils.isNotBlank(bo.getTxnTime()), OrderInfo::getTxnTime, bo.getTxnTime());
        lqw.eq(StringUtils.isNotBlank(bo.getQueryId()), OrderInfo::getQueryId, bo.getQueryId());
        lqw.eq(StringUtils.isNotBlank(bo.getTraceTime()), OrderInfo::getTraceTime, bo.getTraceTime());
        lqw.eq(StringUtils.isNotBlank(bo.getTraceNo()), OrderInfo::getTraceNo, bo.getTraceNo());
        lqw.eq(bo.getTxnAmt() != null, OrderInfo::getTxnAmt, bo.getTxnAmt());
        lqw.eq(StringUtils.isNotBlank(bo.getIssAddnData()), OrderInfo::getIssAddnData, bo.getIssAddnData());
        lqw.eq(StringUtils.isNotBlank(bo.getCommodityJson()), OrderInfo::getCommodityJson, bo.getCommodityJson());
        return lqw;
    }

    /**
     * 新增订单扩展信息
     */
    @Override
    public Boolean insertByBo(OrderInfoBo bo) {
        OrderInfo add = BeanUtil.toBean(bo, OrderInfo.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setNumber(add.getNumber());
        }
        return flag;
    }

    /**
     * 修改订单扩展信息
     */
    @Override
    public Boolean updateByBo(OrderInfoBo bo) {
        OrderInfo update = BeanUtil.toBean(bo, OrderInfo.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderInfo entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除订单扩展信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
