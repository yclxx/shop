package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.bo.UserBo;
import com.ruoyi.zlyyh.domain.vo.MerchantVo;
import com.ruoyi.zlyyh.domain.vo.OrderAndUserNumber;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyhadmin.service.IMerchantService;
import com.ruoyi.zlyyhadmin.service.IOrderService;
import com.ruoyi.zlyyhadmin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements IOrderService {

    private final OrderMapper baseMapper;
    private final IUserService userService;
    private final IMerchantService merchantService;

    /**
     * 查询订单
     */
    @Override
    public OrderVo queryById(Long number) {
        return baseMapper.selectVoById(number);
    }

    /**
     * 查询订单列表
     */
    @Override
    public TableDataInfo<OrderVo> queryPageList(OrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Order> lqw = buildQueryWrapper(bo);
        if (null != bo.getUserId()) {
            if (bo.getUserId().toString().length() == 11) {
                UserBo userBo = new UserBo();
                userBo.setMobile(bo.getUserId().toString());
                List<UserVo> userVos = userService.queryList(userBo);
                if (ObjectUtil.isEmpty(userVos)) {
                    return TableDataInfo.build(new ArrayList<>());
                }
                lqw.in(Order::getUserId, userVos.stream().map(UserVo::getUserId).collect(Collectors.toSet()));
            } else {
                lqw.eq(Order::getUserId, bo.getUserId());
            }
        }
        Page<OrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<OrderVo> dataInfo = TableDataInfo.build(result);
        Map<Long, MerchantVo> merchantVoMap = new HashMap<>(dataInfo.getRows().size());
        for (OrderVo row : dataInfo.getRows()) {
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
     * 查询订单列表
     */
    @Override
    public List<OrderVo> queryList(OrderBo bo) {
        LambdaQueryWrapper<Order> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Order> buildQueryWrapper(OrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        if(null != bo.getNumber()){
            lqw.and(lq -> lq.eq(Order::getNumber, bo.getNumber()).or().eq(Order::getParentNumber, bo.getNumber()).or().eq(Order::getCollectiveNumber,bo.getNumber()));
        }
        if (StringUtils.isNumeric(bo.getProductName())) {
            bo.setProductId(Long.parseLong(bo.getProductName()));
            bo.setProductName("");
        }
        lqw.eq(bo.getProductId() != null, Order::getProductId, bo.getProductId());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), Order::getProductName, bo.getProductName());
        lqw.eq(StringUtils.isNotBlank(bo.getProductImg()), Order::getProductImg, bo.getProductImg());
        lqw.eq(StringUtils.isNotBlank(bo.getPickupMethod()), Order::getPickupMethod, bo.getPickupMethod());
        lqw.eq(bo.getTotalAmount() != null, Order::getTotalAmount, bo.getTotalAmount());
        lqw.eq(bo.getReducedPrice() != null, Order::getReducedPrice, bo.getReducedPrice());
        lqw.eq(bo.getWantAmount() != null, Order::getWantAmount, bo.getWantAmount());
        lqw.eq(bo.getOutAmount() != null, Order::getOutAmount, bo.getOutAmount());
        lqw.eq(bo.getPayTime() != null, Order::getPayTime, bo.getPayTime());
        lqw.eq(bo.getExpireDate() != null, Order::getExpireDate, bo.getExpireDate());
        lqw.eq(bo.getCount() != null, Order::getCount, bo.getCount());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Order::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderType()), Order::getOrderType, bo.getOrderType());
        lqw.eq(StringUtils.isNotBlank(bo.getAccount()), Order::getAccount, bo.getAccount());
        lqw.eq(StringUtils.isNotBlank(bo.getSendStatus()), Order::getSendStatus, bo.getSendStatus());
        lqw.eq(bo.getSendTime() != null, Order::getSendTime, bo.getSendTime());
        lqw.eq(StringUtils.isNotBlank(bo.getExternalProductId()), Order::getExternalProductId, bo.getExternalProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getExternalOrderNumber()), Order::getExternalOrderNumber, bo.getExternalOrderNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getPushNumber()), Order::getPushNumber, bo.getPushNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getFailReason()), Order::getFailReason, bo.getFailReason());
        lqw.like(StringUtils.isNotBlank(bo.getOrderCityName()), Order::getOrderCityName, bo.getOrderCityName());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderCityCode()), Order::getOrderCityCode, bo.getOrderCityCode());
        lqw.eq(bo.getParentNumber() != null, Order::getParentNumber, bo.getParentNumber());
        lqw.eq(bo.getPlatformKey() != null, Order::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getVerificationStatus()),Order::getVerificationStatus,bo.getVerificationStatus());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            Order::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 修改订单
     */
    @Override
    public Boolean updateByBo(OrderBo bo) {
        Order update = BeanUtil.toBean(bo, Order.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Order entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public List<OrderAndUserNumber> queryUserAndOrderNum(Date startDateTime, Date endDateTime, Integer indexNum, Integer indexPage) {
        return baseMapper.queryUserAndOrderNum(startDateTime, endDateTime, indexNum, indexPage);
    }
}
