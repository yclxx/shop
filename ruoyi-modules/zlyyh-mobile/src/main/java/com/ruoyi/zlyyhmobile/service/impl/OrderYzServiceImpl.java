package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderYz;
import com.ruoyi.zlyyh.domain.vo.OrderYzVo;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.mapper.OrderYzMapper;
import com.ruoyi.zlyyhmobile.service.IOrderYzService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 优惠券批次Service业务层处理
 *
 * @author yzg
 * @date 2023-10-12
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderYzServiceImpl implements IOrderYzService {
    private final OrderYzMapper orderYzMapper;
    private final IUserService userService;
    private final OrderMapper orderMapper;
    @Autowired
    private LockTemplate lockTemplate;

    @Transactional
    @Async
    @Override
    public void qy(Long platformKey) {
        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        int pageNum = 1;
        int pageSize = 500;
        TimeInterval timer = DateUtil.timer();
        LambdaQueryWrapper<OrderYz> lqw = Wrappers.lambdaQuery();
        lqw.isNull(OrderYz::getOrderNumber);
        lqw.eq(OrderYz::getDelFlag, "0");
        Long total = orderYzMapper.selectCount(lqw);
        log.info("开始执行迁移有赞订单,共：{}条数据", total);
        while (true) {
            List<Future<?>> futures = new ArrayList<>();
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(pageNum);
            pageQuery.setPageSize(pageSize);
            IPage<OrderYzVo> orderYzVoIPage = orderYzMapper.selectVoPage(pageQuery.build(), lqw);
            List<OrderYzVo> records = orderYzVoIPage.getRecords();
            log.info("迁移有赞订单，共：{}条数据,正在处理第：{}页，当前页数据：{}条", total, pageNum, records.size());
            for (OrderYzVo record : records) {
                try {
                    futures.add(executorService.submit(() -> {
                        handleRecord(record, platformKey);
                    }));
                } catch (Exception e) {
                    log.info("迁移有赞订单异常，异常订单信息：{}", record);
                    log.error("迁移有赞订单异常，异常信息：", e);
                }
            }
            // 等待所有子线程执行完毕
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    log.error("迁移有赞订单线程执行异常，异常信息：", e);
                }
            }
            int i = pageNum * pageSize;
            if (i >= total || ObjectUtil.isEmpty(records)) {
                log.info("迁移有赞订单完成，共：{}条数据，共：{}页，最后一页数据：{}条，总耗时：{}秒", total, pageNum, records.size(), timer.intervalSecond());
                break;
            }
            pageNum++;
        }
        executorService.shutdown();
    }

    @Transactional
    public void handleRecord(OrderYzVo orderYzVo, Long platformKey) {
        String mobile = orderYzVo.getAccount();
        if (StringUtils.isBlank(mobile)) {
            mobile = orderYzVo.getMobile();
        }
        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException("迁移有赞订单失败,订单信息缺少用户手机号");
        }
        mobile = mobile.trim();
        String key = "yzOrderMobile:" + mobile;
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 1000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            log.info("当前用户信息获取中，{}", mobile);
            return;
        }
        Long userId;
        // 获取锁成功，处理业务
        try {
            // 查询用户是否存在，不存在则新增用户
            userId = userService.getUserIdByMobile(platformKey, mobile);
            if (null == userId) {
                // 新增用户
                boolean b = userService.insertUserByMobile(platformKey, mobile);
                if (!b) {
                    throw new ServiceException("迁移有赞订单失败,创建用户失败");
                }
                userId = userService.getUserIdByMobile(platformKey, mobile);
                if (null == userId) {
                    throw new ServiceException("迁移有赞订单失败,未获取到用户ID");
                }
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
        Order order = new Order();
        order.setNumber(IdUtil.getSnowflakeNextId());
        order.setProductId(1000001L);
        order.setUserId(userId);
        order.setProductName(orderYzVo.getProductName());
        order.setTotalAmount(orderYzVo.getTotalAmount());
        order.setReducedPrice(orderYzVo.getReducedPrice());
        order.setWantAmount(orderYzVo.getWantAmount());
        order.setOutAmount(orderYzVo.getOutAmount());
        order.setPickupMethod(order.getWantAmount().signum() > 0 ? "1" : "0");
        if (StringUtils.isNotBlank(orderYzVo.getSuccessOrderTime())) {
            order.setPayTime(DateUtil.parse(orderYzVo.getSuccessOrderTime()));
        }
        order.setAccount(mobile);
        order.setSendStatus("2");
        order.setPlatformKey(platformKey);
        order.setCreateTime(DateUtil.parse(orderYzVo.getCreateOrderTime()));
        String status = "3";
        if (StringUtils.isNotBlank(orderYzVo.getRefundStatus()) && orderYzVo.getRefundStatus().contains("退款成功")) {
            status = "5";
        } else {
            if (!orderYzVo.getStatus().contains("交易关闭")) {
                status = "2";
            }
        }
        order.setStatus(status);
        int insert = orderMapper.insert(order);
        if (insert < 1) {
            throw new ServiceException("迁移有赞订单失败,新增订单失败");
        }
        OrderYz orderYz = new OrderYz();
        orderYz.setNumber(orderYzVo.getNumber());
        orderYz.setOrderNumber(order.getNumber());
        orderYz.setDelFlag("2");
        int i = orderYzMapper.updateById(orderYz);
        if (i < 1) {
            throw new ServiceException("迁移有赞订单失败,更新迁移订单异常");
        }
    }

}
