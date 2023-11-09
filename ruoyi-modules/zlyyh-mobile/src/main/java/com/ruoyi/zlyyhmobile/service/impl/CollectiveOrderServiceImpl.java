package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyhmobile.service.ICollectiveOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.CollectiveOrderBo;
import com.ruoyi.zlyyh.domain.vo.CollectiveOrderVo;
import com.ruoyi.zlyyh.mapper.CollectiveOrderMapper;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.*;

/**
 * 大订单Service业务层处理
 *
 * @author yzg
 * @date 2023-10-16
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CollectiveOrderServiceImpl implements ICollectiveOrderService {

    private final CollectiveOrderMapper baseMapper;
    private final OrderMapper orderMapper;

    /**
     * 查询大订单
     */
    @Override
    public CollectiveOrderVo queryById(Long collectiveNumber){
        CollectiveOrderVo collectiveOrderVo = baseMapper.selectVoById(collectiveNumber);
        List<OrderVo> orderVos = orderMapper.selectVoList(new LambdaQueryWrapper<Order>().eq(Order::getCollectiveNumber, collectiveOrderVo.getCollectiveNumber()));
        collectiveOrderVo.setOrderVos(orderVos);
        return collectiveOrderVo;
    }

    /**
     * 创建之前没有的大订单 该接口仅使用一次
     */
    @Override
    public void addCollectiveOrder(){
        //先查询没有大订单的小订单  数量很多分页查询
        Integer pageNum = 1;
        Integer pageSize = 200;
        long total = -1;
        log.info("添加大订单数据开始：{}", DateUtil.now());
        while (true) {
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(pageNum);
            pageQuery.setPageSize(pageSize);
            Page<Order> result = orderMapper.selectPage(pageQuery.build(), new LambdaQueryWrapper<Order>().isNull(Order::getCollectiveNumber));
            TableDataInfo<Order> tableDataInfo = TableDataInfo.build(result);
            if (total == -1) {
                total = tableDataInfo.getTotal();
            }
            log.info("添加大订单数据第{}页，共：{}条数据，时间：{}", pageNum, tableDataInfo.getRows().size(), DateUtil.now());
            for (Order row : tableDataInfo.getRows()) {
                //事务 一起成功一起失败
                try {
                    collectiveOrderSet(row);
                } catch (Exception e) {
                    log.error("大订单添加失败：", e);
                }
            }
            if (Integer.valueOf(pageNum * pageSize).longValue() >= total) {
                break;
            }
            pageNum++;
            //每天跑不超过40万条数据
            if (pageNum > 2000) {
                break;
            }
        }
        log.info("添加大订单数据结束：{}", DateUtil.now());

    }


    /**
     * 查询大订单列表
     */
    @Override
    public TableDataInfo<CollectiveOrderVo> queryPageList(CollectiveOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CollectiveOrder> lqw = buildQueryWrapper(bo);
        Page<CollectiveOrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<CollectiveOrderVo> build = TableDataInfo.build(result);
        List<CollectiveOrderVo> resultCol =  new ArrayList<>(build.getRows().size());
        for (CollectiveOrderVo collectiveOrderVo : build.getRows()) {
            //把小订单列表加上
            List<OrderVo> orderVos = orderMapper.selectVoList(new LambdaQueryWrapper<Order>().eq(Order::getCollectiveNumber, collectiveOrderVo.getCollectiveNumber()));
            if (ObjectUtil.isNotEmpty(orderVos)){
                collectiveOrderVo.setOrderVos(orderVos);
            }
            resultCol.add(collectiveOrderVo);
        }
        build.setRows(resultCol);
        return build;
    }


    @Transactional(rollbackFor = Exception.class)
    public void collectiveOrderSet(Order order) {
        //之前的小订单一个就生成一个大订单
        CollectiveOrder collectiveOrder = new CollectiveOrder();
        collectiveOrder.setCollectiveNumber(IdUtil.getSnowflakeNextId());
        collectiveOrder.setUserId(order.getUserId());
        collectiveOrder.setOrderCityCode(order.getOrderCityCode());
        collectiveOrder.setOrderCityName(order.getOrderCityName());
        collectiveOrder.setPlatformKey(order.getPlatformKey());
        collectiveOrder.setStatus(order.getStatus());
        collectiveOrder.setExpireDate(order.getExpireDate());
        collectiveOrder.setCount(order.getCount());
        collectiveOrder.setTotalAmount(order.getTotalAmount());
        collectiveOrder.setReducedPrice(order.getReducedPrice());
        collectiveOrder.setWantAmount(order.getWantAmount());
        collectiveOrder.setOutAmount(order.getOutAmount());
        collectiveOrder.setSysUserId(order.getSysUserId());
        collectiveOrder.setSysDeptId(order.getSysDeptId());
        collectiveOrder.setPayMerchant(order.getPayMerchant());
        order.setCollectiveNumber(collectiveOrder.getCollectiveNumber());
        baseMapper.insert(collectiveOrder);
        orderMapper.updateById(order);

    }



    private LambdaQueryWrapper<CollectiveOrder> buildQueryWrapper(CollectiveOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<CollectiveOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, CollectiveOrder::getUserId, bo.getUserId());
        if (StringUtils.isNotBlank(bo.getStatus())) {
            lqw.in(CollectiveOrder::getStatus, bo.getStatus().split(","));
        }
        return lqw;
    }









}
