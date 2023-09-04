package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.OrderDownloadLog;
import com.ruoyi.zlyyh.domain.bo.OrderDownloadLogBo;
import com.ruoyi.zlyyh.domain.vo.OrderDownloadLogVo;
import com.ruoyi.zlyyh.mapper.OrderDownloadLogMapper;
import com.ruoyi.zlyyhadmin.service.IOrderDownloadLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 订单下载记录Service业务层处理
 *
 * @author yzg
 * @date 2023-04-01
 */
@RequiredArgsConstructor
@Service
public class OrderDownloadLogServiceImpl implements IOrderDownloadLogService {

    private final OrderDownloadLogMapper baseMapper;

    /**
     * 查询订单下载记录
     */
    @Override
    public OrderDownloadLogVo queryById(Long tOrderDownloadId){
        return baseMapper.selectVoById(tOrderDownloadId);
    }

    /**
     * 查询订单下载记录列表
     */
    @Override
    public TableDataInfo<OrderDownloadLogVo> queryPageList(OrderDownloadLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderDownloadLog> lqw = buildQueryWrapper(bo);
        Page<OrderDownloadLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询订单下载记录列表
     */
    @Override
    public List<OrderDownloadLogVo> queryList(OrderDownloadLogBo bo) {
        LambdaQueryWrapper<OrderDownloadLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderDownloadLog> buildQueryWrapper(OrderDownloadLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderDownloadLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getFileUrl()), OrderDownloadLog::getFileUrl, bo.getFileUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), OrderDownloadLog::getStatus, bo.getStatus());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            OrderDownloadLog::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增订单下载记录
     */
    @Override
    public Boolean insertByBo(OrderDownloadLogBo bo) {
        OrderDownloadLog add = BeanUtil.toBean(bo, OrderDownloadLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTOrderDownloadId(add.getTOrderDownloadId());
        }
        return flag;
    }

    /**
     * 修改订单下载记录
     */
    @Override
    public Boolean updateByBo(OrderDownloadLogBo bo) {
        OrderDownloadLog update = BeanUtil.toBean(bo, OrderDownloadLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderDownloadLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除订单下载记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
