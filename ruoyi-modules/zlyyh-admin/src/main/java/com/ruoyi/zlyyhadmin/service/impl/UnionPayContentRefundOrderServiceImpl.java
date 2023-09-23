package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.UnionPayContentRefundOrder;
import com.ruoyi.zlyyh.domain.bo.UnionPayContentRefundOrderBo;
import com.ruoyi.zlyyh.domain.vo.UnionPayContentRefundOrderVo;
import com.ruoyi.zlyyh.mapper.UnionPayContentRefundOrderMapper;
import com.ruoyi.zlyyhadmin.service.IUnionPayContentRefundOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 内容分销内容方退券订单Service业务层处理
 *
 * @author yzg
 * @date 2023-09-23
 */
@RequiredArgsConstructor
@Service
public class UnionPayContentRefundOrderServiceImpl implements IUnionPayContentRefundOrderService {

    private final UnionPayContentRefundOrderMapper baseMapper;

    /**
     * 查询内容分销内容方退券订单
     */
    @Override
    public UnionPayContentRefundOrderVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询内容分销内容方退券订单列表
     */
    @Override
    public TableDataInfo<UnionPayContentRefundOrderVo> queryPageList(UnionPayContentRefundOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<UnionPayContentRefundOrder> lqw = buildQueryWrapper(bo);
        Page<UnionPayContentRefundOrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询内容分销内容方退券订单列表
     */
    @Override
    public List<UnionPayContentRefundOrderVo> queryList(UnionPayContentRefundOrderBo bo) {
        LambdaQueryWrapper<UnionPayContentRefundOrder> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<UnionPayContentRefundOrder> buildQueryWrapper(UnionPayContentRefundOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<UnionPayContentRefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getUnionPayAppId()), UnionPayContentRefundOrder::getUnionPayAppId, bo.getUnionPayAppId());
        lqw.eq(StringUtils.isNotBlank(bo.getUnionPayOrderId()), UnionPayContentRefundOrder::getUnionPayOrderId, bo.getUnionPayOrderId());
        lqw.eq(StringUtils.isNotBlank(bo.getUnionPayProdId()), UnionPayContentRefundOrder::getUnionPayProdId, bo.getUnionPayProdId());
        lqw.eq(StringUtils.isNotBlank(bo.getUnionPayTxnTime()), UnionPayContentRefundOrder::getUnionPayTxnTime, bo.getUnionPayTxnTime());
        lqw.eq(StringUtils.isNotBlank(bo.getUnionPayResultStatus()), UnionPayContentRefundOrder::getUnionPayResultStatus, bo.getUnionPayResultStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getUnionPayBondNo()), UnionPayContentRefundOrder::getUnionPayBondNo, bo.getUnionPayBondNo());
        return lqw;
    }

    /**
     * 新增内容分销内容方退券订单
     */
    @Override
    public Boolean insertByBo(UnionPayContentRefundOrderBo bo) {
        UnionPayContentRefundOrder add = BeanUtil.toBean(bo, UnionPayContentRefundOrder.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改内容分销内容方退券订单
     */
    @Override
    public Boolean updateByBo(UnionPayContentRefundOrderBo bo) {
        UnionPayContentRefundOrder update = BeanUtil.toBean(bo, UnionPayContentRefundOrder.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(UnionPayContentRefundOrder entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除内容分销内容方退券订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
