package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.bo.UserBo;
import com.ruoyi.zlyyh.domain.vo.MerchantVo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyhadmin.service.IHistoryOrderService;
import com.ruoyi.zlyyhadmin.service.IMerchantService;
import com.ruoyi.zlyyhadmin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.HistoryOrderBo;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderVo;
import com.ruoyi.zlyyh.domain.HistoryOrder;
import com.ruoyi.zlyyh.mapper.HistoryOrderMapper;


import java.util.*;
import java.util.stream.Collectors;

/**
 * 历史订单Service业务层处理
 *
 * @author yzg
 * @date 2023-08-01
 */
@RequiredArgsConstructor
@Service
public class HistoryOrderServiceImpl implements IHistoryOrderService {

    private final HistoryOrderMapper baseMapper;
    private final IUserService userService;
    private final IMerchantService merchantService;

    /**
     * 查询历史订单
     */
    @Override
    public HistoryOrderVo queryById(Long number){
        return baseMapper.selectVoById(number);
    }

    /**
     * 查询历史订单列表
     */
    @Override
    public TableDataInfo<HistoryOrderVo> queryPageList(HistoryOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HistoryOrder> lqw = buildQueryWrapper(bo);
        if (null != bo.getUserId()) {
            if (bo.getUserId().toString().length() == 11) {
                UserBo userBo = new UserBo();
                userBo.setMobile(bo.getUserId().toString());
                List<UserVo> userVos = userService.queryList(userBo);
                if (ObjectUtil.isEmpty(userVos)) {
                    return TableDataInfo.build(new ArrayList<>());
                }
                lqw.in(HistoryOrder::getUserId, userVos.stream().map(UserVo::getUserId).collect(Collectors.toSet()));
            } else {
                lqw.eq(HistoryOrder::getUserId, bo.getUserId());
            }
        }
        Page<HistoryOrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<HistoryOrderVo> dataInfo = TableDataInfo.build(result);
        Map<Long, MerchantVo> merchantVoMap = new HashMap<>(dataInfo.getRows().size());
        for (HistoryOrderVo row : dataInfo.getRows()) {
            if (null != row.getPayMerchant()) {
                MerchantVo merchantVo = merchantVoMap.get(row.getPayMerchant());
                if (null == merchantVo) {
                    merchantVo = merchantService.queryById(row.getPayMerchant());
                    merchantVoMap.put(row.getPayMerchant(), merchantVo);
                }
                row.setMerchantVo(merchantVo);
            }
        }
        return dataInfo;
    }

    /**
     * 查询历史订单列表
     */
    @Override
    public List<HistoryOrderVo> queryList(HistoryOrderBo bo) {
        LambdaQueryWrapper<HistoryOrder> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<HistoryOrder> buildQueryWrapper(HistoryOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<HistoryOrder> lqw = Wrappers.lambdaQuery();
        if(null != bo.getNumber()){
            lqw.and(lq -> lq.eq(HistoryOrder::getNumber, bo.getNumber()).or().eq(HistoryOrder::getParentNumber, bo.getNumber()));
        }
        if (StringUtils.isNumeric(bo.getProductName())) {
            bo.setProductId(Long.parseLong(bo.getProductName()));
            bo.setProductName("");
        }
        lqw.eq(bo.getProductId() != null, HistoryOrder::getProductId, bo.getProductId());
        lqw.eq(bo.getUserId() != null, HistoryOrder::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getPickupMethod()), HistoryOrder::getPickupMethod, bo.getPickupMethod());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderType()), HistoryOrder::getOrderType, bo.getOrderType());
        lqw.eq(bo.getTotalAmount() != null, HistoryOrder::getTotalAmount, bo.getTotalAmount());
        lqw.eq(bo.getReducedPrice() != null, HistoryOrder::getReducedPrice, bo.getReducedPrice());
        lqw.eq(bo.getWantAmount() != null, HistoryOrder::getWantAmount, bo.getWantAmount());
        lqw.eq(bo.getOutAmount() != null, HistoryOrder::getOutAmount, bo.getOutAmount());
        lqw.eq(bo.getPayTime() != null, HistoryOrder::getPayTime, bo.getPayTime());
        lqw.eq(bo.getExpireDate() != null, HistoryOrder::getExpireDate, bo.getExpireDate());
        lqw.eq(bo.getCount() != null, HistoryOrder::getCount, bo.getCount());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), HistoryOrder::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getAccount()), HistoryOrder::getAccount, bo.getAccount());
        lqw.eq(StringUtils.isNotBlank(bo.getSendStatus()), HistoryOrder::getSendStatus, bo.getSendStatus());
        lqw.eq(bo.getSendTime() != null, HistoryOrder::getSendTime, bo.getSendTime());
        lqw.eq(StringUtils.isNotBlank(bo.getCancelStatus()), HistoryOrder::getCancelStatus, bo.getCancelStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getExternalProductId()), HistoryOrder::getExternalProductId, bo.getExternalProductId());
        lqw.eq(bo.getExternalProductSendValue() != null, HistoryOrder::getExternalProductSendValue, bo.getExternalProductSendValue());
        lqw.eq(StringUtils.isNotBlank(bo.getExternalOrderNumber()), HistoryOrder::getExternalOrderNumber, bo.getExternalOrderNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getPushNumber()), HistoryOrder::getPushNumber, bo.getPushNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getFailReason()), HistoryOrder::getFailReason, bo.getFailReason());
        lqw.like(StringUtils.isNotBlank(bo.getOrderCityName()), HistoryOrder::getOrderCityName, bo.getOrderCityName());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderCityCode()), HistoryOrder::getOrderCityCode, bo.getOrderCityCode());
        lqw.eq(bo.getPlatformKey() != null, HistoryOrder::getPlatformKey, bo.getPlatformKey());
        lqw.eq(bo.getPayMerchant() != null, HistoryOrder::getPayMerchant, bo.getPayMerchant());
        lqw.eq(bo.getParentNumber() != null, HistoryOrder::getParentNumber, bo.getParentNumber());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            HistoryOrder::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增历史订单
     */
    @Override
    public Boolean insertByBo(HistoryOrderBo bo) {
        HistoryOrder add = BeanUtil.toBean(bo, HistoryOrder.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setNumber(add.getNumber());
        }
        return flag;
    }

    /**
     * 修改历史订单
     */
    @Override
    public Boolean updateByBo(HistoryOrderBo bo) {
        HistoryOrder update = BeanUtil.toBean(bo, HistoryOrder.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(HistoryOrder entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除历史订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
