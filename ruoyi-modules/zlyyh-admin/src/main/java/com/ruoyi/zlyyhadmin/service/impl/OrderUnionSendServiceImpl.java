package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.OrderUnionSend;
import com.ruoyi.zlyyh.domain.bo.OrderUnionSendBo;
import com.ruoyi.zlyyh.domain.vo.OrderUnionSendVo;
import com.ruoyi.zlyyh.mapper.OrderUnionSendMapper;
import com.ruoyi.zlyyhadmin.service.IOrderUnionSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 银联分销订单卡券Service业务层处理
 *
 * @author yzg
 * @date 2023-08-22
 */
@RequiredArgsConstructor
@Service
public class OrderUnionSendServiceImpl implements IOrderUnionSendService {

    private final OrderUnionSendMapper baseMapper;

    /**
     * 查询银联分销订单卡券
     */
    @Override
    public OrderUnionSendVo queryById(Long number){
        return baseMapper.selectVoById(number);
    }

    /**
     * 查询银联分销订单卡券列表
     */
    @Override
    public TableDataInfo<OrderUnionSendVo> queryPageList(OrderUnionSendBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderUnionSend> lqw = buildQueryWrapper(bo);
        Page<OrderUnionSendVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询银联分销订单卡券列表
     */
    @Override
    public List<OrderUnionSendVo> queryList(OrderUnionSendBo bo) {
        LambdaQueryWrapper<OrderUnionSend> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderUnionSend> buildQueryWrapper(OrderUnionSendBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderUnionSend> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getNumber() != null, OrderUnionSend::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getProdTn()), OrderUnionSend::getProdTn, bo.getProdTn());
        lqw.eq(StringUtils.isNotBlank(bo.getBondSerlNo()), OrderUnionSend::getBondSerlNo, bo.getBondSerlNo());
        return lqw;
    }

    /**
     * 新增银联分销订单卡券
     */
    @Override
    public Boolean insertByBo(OrderUnionSendBo bo) {
        OrderUnionSend add = BeanUtil.toBean(bo, OrderUnionSend.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setNumber(add.getNumber());
        }
        return flag;
    }

    /**
     * 修改银联分销订单卡券
     */
    @Override
    public Boolean updateByBo(OrderUnionSendBo bo) {
        OrderUnionSend update = BeanUtil.toBean(bo, OrderUnionSend.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderUnionSend entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除银联分销订单卡券
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
