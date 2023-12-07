package com.ruoyi.zlyyhmobile.dubbo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteAppOrderService;
import com.ruoyi.zlyyh.domain.HistoryOrder;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderUnionSend;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderVo;
import com.ruoyi.zlyyh.domain.vo.OrderUnionPayVo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;
import com.ruoyi.zlyyh.enumd.UnionPay.UnionPayParams;
import com.ruoyi.zlyyh.mapper.OrderUnionSendMapper;
import com.ruoyi.zlyyh.properties.CtripConfig;
import com.ruoyi.zlyyh.properties.YsfFoodProperties;
import com.ruoyi.zlyyh.properties.utils.YsfDistributionPropertiesUtils;
import com.ruoyi.zlyyh.utils.CtripUtils;
import com.ruoyi.zlyyh.utils.YsfFoodUtils;
import com.ruoyi.zlyyh.utils.redis.OrderCacheUtils;
import com.ruoyi.zlyyh.utils.sdk.UnionPayDistributionUtil;
import com.ruoyi.zlyyhmobile.event.ShareOrderEvent;
import com.ruoyi.zlyyhmobile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

/**
 * 订单服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteAppOrderServiceImpl implements RemoteAppOrderService {
    private static final YsfFoodProperties YSF_FOOD_PROPERTIES = SpringUtils.getBean(YsfFoodProperties.class);
    private static final CtripConfig ctripConfig = SpringUtils.getBean(CtripConfig.class);
    private final LockTemplate lockTemplate;
    private final IOrderService orderService;
    private final IHistoryOrderService historyOrderService;
    private final IMissionUserRecordService missionUserRecordService;
    private final IOrderUnionPayService orderUnionPayService;
    private final ICollectiveOrderService collectiveOrderService;
    private final OrderUnionSendMapper orderUnionSendMapper;
    private final IShareUserRecordService userRecordService;

    @Async
    @Override
    public void queryOrderHandler() {
        orderService.queryOrderHandler();
    }

    @Override
    public void sendCoupon(Long number) {
        String key = "sendCoupon:" + number;
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        // 获取锁成功，处理业务
        try {
            String orderKey = "sendCouponOrderKey:" + number;
            String time = RedisUtils.getCacheObject(orderKey);
            if (StringUtils.isNotBlank(time)) {
                return;
            }
            time = DateUtil.now();
            RedisUtils.setCacheObject(orderKey, time, Duration.ofMinutes(1));
            orderService.sendCoupon(number);
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    @Override
    public void cancelFoodOrder(Long number) {
        //请求美食退款订单接口
        OrderVo orderVo = orderService.queryById(number);
        String s = refundOrder(orderVo.getOrderType(), orderVo.getCancelStatus(), orderVo.getExternalOrderNumber());
        if ((ObjectUtil.isNotEmpty(s) && "5".equals(orderVo.getOrderType())) || "0".equals(s)) {
            //订单设置为退款种状态 等待回调
            Order order = new Order();
            order.setCancelStatus("0");
            orderService.updateOrder(order);
        }
    }

    @Override
    public void cancelHistoryFoodOrder(Long number) {
        //请求美食退款订单接口
        HistoryOrderVo orderVo = historyOrderService.queryById(number);
        String s = refundOrder(orderVo.getOrderType(), orderVo.getCancelStatus(), orderVo.getExternalOrderNumber());
        if ((ObjectUtil.isNotEmpty(s) && "5".equals(orderVo.getOrderType())) || "0".equals(s)) {
            //订单设置为退款种状态 等待回调
            HistoryOrder order = new HistoryOrder();
            order.setCancelStatus("0");
            historyOrderService.updateOrder(order);
        }
    }

    private String refundOrder(String orderType, String cancelOrderStatus, String externalOrderNumber) {
        if (!"1".equals(orderType) && !"5".equals(orderType) && !"15".equals(orderType)) {
            log.info("美食订单申请退款：{}", orderType);
            throw new ServiceException("非美食订单，无法向供应商申请退款");
        }
        if ("1".equals(cancelOrderStatus)) {
            throw new ServiceException("该订单已收到供应商退款,不可重复申请");
        }
        //根据订单类型请求不同供应商接口
        if ("5".equals(orderType)) {
            //口碑类型商品
            return YsfFoodUtils.cancelOrder(YSF_FOOD_PROPERTIES.getAppId(), externalOrderNumber, YSF_FOOD_PROPERTIES.getRsaPrivateKey(), YSF_FOOD_PROPERTIES.getRefundUrl());
        } else if ("15".equals(orderType)) {
            String accessToken = CtripUtils.getAccessToken();
            String refundCtripUrl = ctripConfig.getUrl() + "?AID=" + ctripConfig.getAid() + "&SID=" + ctripConfig.getSid() +
                "&ICODE=" + ctripConfig.getCancelOrderCode() + "&Token=" + accessToken;
            return CtripUtils.cancelOrder(externalOrderNumber, ctripConfig.getPartnerType(), refundCtripUrl);
        }
        return null;
    }

    @Override
    public void queryOrderSendStatus(String pushNumber) {
        orderService.queryOrderSendStatus(pushNumber);
    }

    @Async
    @Override
    public void orderCacheSaveToMySql() {
        String key = "queryOrderSendStatus";
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        // 获取锁成功，处理业务
        try {
            String time = RedisUtils.getCacheObject(key);
            if (StringUtils.isNotBlank(time)) {
                return;
            }
            time = DateUtil.now();
            RedisUtils.setCacheObject(key, time, Duration.ofMinutes(2));
            // 处理缓存订单
            List<Long> numberList = OrderCacheUtils.getOrderCache(0, 2000);
            if (ObjectUtil.isNotEmpty(numberList)) {
                for (Long number : numberList) {
                    try {
                        orderService.saveOrderToMySql(number);
                    } catch (Exception e) {
                        log.error("订单保存异常：", e);
                    }
                }
            }

        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    @Async
    @Override
    public void cancelOrder() {
        orderService.cancelOrder();
    }

    /**
     * 订单补发
     */
    @Async
    @Override
    public void reloadOrder() {
        log.info("执行了订单补发");
        //查询三天内发放状态 0未发放 1发放中
        try {
            List<OrderVo> orderVos = orderService.sendStatusOrder();
            if (ObjectUtil.isNotEmpty(orderVos)) {
                for (OrderVo orderVo : orderVos) {
                    if ("0".equals(orderVo.getSendStatus())) {
                        // 开始发券
                        sendCoupon(orderVo.getNumber());
                    } else {
                        String value = CacheUtils.get(CacheNames.reloadOrderNumbers, orderVo.getNumber());
                        if (StringUtils.isBlank(value)) {
                            CacheUtils.put(CacheNames.reloadOrderNumbers, orderVo.getNumber(), DateUtil.now());
                            // 开始发券
                            sendCoupon(orderVo.getNumber());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("订单补发异常：", e);
        }
        try {
            missionUserRecordService.sendStatusOrder();
        } catch (Exception e) {
            log.error("订单补发异常：", e);
        }
    }

    @Override
    public void sendDraw(Long missionUserRecordId) {
        String key = "sendDrawLock:" + missionUserRecordId;
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        // 获取锁成功，处理业务
        try {
            String cacheKey = "draw" + key;
            String time = RedisUtils.getCacheObject(cacheKey);
            if (StringUtils.isNotBlank(time)) {
                return;
            }
            time = DateUtil.now();
            RedisUtils.setCacheObject(cacheKey, time, Duration.ofMinutes(2));
            missionUserRecordService.sendDraw(missionUserRecordId);
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 查询任务完成进度
     */
    @Async
    @Override
    public void queryMission() {
        missionUserRecordService.queryMission();
    }

    @Override
    public R<Void> couponRefundOrder(Long number) {
        OrderVo order = orderService.queryById(number);
        if (order.getOrderType().equals("11") || order.getOrderType().equals("12")) {
            OrderUnionPayVo orderUnionPayVo = orderUnionPayService.queryById(number);
            if (orderUnionPayVo == null) {
                return R.fail("无此银联分销订单详情。");
            }
            OrderUnionSend send = orderUnionSendMapper.selectById(number);
            if (null == send) {
                return R.fail("无此银联分销卡券订单详情。");
            }
            String orderRefundCouponStr;
            if (order.getOrderType().equals("12")) {
                orderRefundCouponStr = UnionPayDistributionUtil.orderRefundCoupon(order.getNumber(), order.getExternalProductId(), send.getProdTn(), send.getBondSerlNo(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(order.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJD(order.getPlatformKey()));
            } else {
                orderRefundCouponStr = UnionPayDistributionUtil.orderRefundCoupon(order.getNumber(), order.getExternalProductId(), send.getProdTn(), send.getBondSerlNo(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJCAppId(order.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJC(order.getPlatformKey()));
            }
            JSONObject orderRefundCoupon = JSONObject.parseObject(orderRefundCouponStr);
            if (orderRefundCoupon.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
                send.setRfdTn(orderRefundCoupon.getString("rfdTn"));
                send.setBondSt(orderRefundCoupon.getString("bondSt"));
                orderUnionSendMapper.updateById(send);
                return R.ok();
            } else {
                String subMsg = orderRefundCoupon.getString("subMsg");
                if (StringUtils.isNotEmpty(subMsg)) {
                    return R.fail(subMsg);
                } else {
                    return R.fail(subMsg);
                }
            }
        }
        return R.ok();
    }

    /**
     * 订单数据迁移至历史数据表
     */
    @Async
    @Override
    public void orderToHistory() {
        historyOrderService.orderToHistory();
    }

    /**
     * 添加大订单数据
     */
    @Async
    @Override
    public void addCollectiveOrder() {
        collectiveOrderService.addCollectiveOrder();
    }

    /**
     * 查询订单核销状态
     */
    @Async
    @Override
    public void queryOrderUsedStatus() {
        List<String> orderTypeList = new ArrayList<>();
        // 银联开放平台票券
        orderTypeList.add("18");
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageSize(100);
        pageQuery.setOrderByColumn("number");
        pageQuery.setIsAsc("asc");

        Integer pageNum = 1;
        while (true) {
            pageQuery.setPageNum(pageNum);
            TableDataInfo<OrderVo> orderVoTableDataInfo = orderService.queryOrderByOrderTypeList(orderTypeList, null, pageQuery);
            for (OrderVo orderVo : orderVoTableDataInfo.getRows()) {
                try {
                    orderService.queryOrderUsedStatus(orderVo);
                } catch (Exception e) {
                    log.error("查询订单核销状态异常：", e);
                }
            }
            if ((long) pageNum * pageQuery.getPageSize() >= orderVoTableDataInfo.getTotal()) {
                break;
            }
            pageNum++;
        }
    }

    /**
     * 查询订单分享状态
     */
    @Async
    @Override
    public void queryShareStatus() {
        try {
            ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
            shareUserRecordBo.setInviteeStatus("0,1");
            Map<String, Object> params = new HashMap<>();
            params.put("beginCreateTime", DateUtil.offsetDay(new Date(), -90));
            params.put("endCreateTime", new Date());
            shareUserRecordBo.setParams(params);
            // 开始查询
            queryUsedStatus(shareUserRecordBo);
        } catch (Exception e) {
            log.info("查询分销订单核销状态异常：", e);
        }
        try {
            ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
            shareUserRecordBo.setInviteeStatus("2");
            Map<String, Object> params = new HashMap<>();
            params.put("beginOrderUsedTime", DateUtil.offsetDay(new Date(), -7));
            params.put("endOrderUsedTime", new Date());
            shareUserRecordBo.setParams(params);
            // 开始查询
            queryUsedStatus(shareUserRecordBo);
        } catch (Exception e) {
            log.info("查询分销已核销订单状态异常：", e);
        }
    }

    /**
     * 查询分销订单已核销 二次确认，7天内的
     */
    private void queryUsedStatus(ShareUserRecordBo shareUserRecordBo) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageSize(100);
        pageQuery.setOrderByColumn("number");
        pageQuery.setIsAsc("asc");

        Integer pageNum = 1;
        while (true) {
            pageQuery.setPageNum(pageNum);
            TableDataInfo<ShareUserRecordVo> shareUserRecordVoTableDataInfo = userRecordService.queryPageList(shareUserRecordBo, pageQuery);
            for (ShareUserRecordVo shareUserRecordVo : shareUserRecordVoTableDataInfo.getRows()) {
                try {
                    OrderVo orderVo = orderService.queryById(shareUserRecordVo.getNumber());
                    if (null == orderVo) {
                        log.error("分销订单不存在，分销信息：{}", shareUserRecordVo);
                        continue;
                    }
                    SpringUtils.context().publishEvent(new ShareOrderEvent(null, orderVo.getNumber()));
                } catch (Exception e) {
                    log.error("分销订单处理异常：", e);
                }
            }
            if ((long) pageNum * pageQuery.getPageSize() >= shareUserRecordVoTableDataInfo.getTotal()) {
                break;
            }
            pageNum++;
        }
    }

    /**
     * 查询订单分享奖励状态
     */
    @Async
    @Override
    public void queryShareAwardStatus() {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageSize(100);
        pageQuery.setOrderByColumn("number");
        pageQuery.setIsAsc("asc");

        Integer pageNum = 1;
        ShareUserRecordBo shareUserRecordBo = new ShareUserRecordBo();
        shareUserRecordBo.setInviteeStatus("2");
        shareUserRecordBo.setAwardStatus("0,1,3");
        Map<String, Object> params = new HashMap<>();
        params.put("beginCreateTime", DateUtil.offsetDay(new Date(), -90));
        params.put("endCreateTime", new Date());
        shareUserRecordBo.setParams(params);
        while (true) {
            pageQuery.setPageNum(pageNum);
            TableDataInfo<ShareUserRecordVo> shareUserRecordVoTableDataInfo = userRecordService.queryPageList(shareUserRecordBo, pageQuery);
            for (ShareUserRecordVo shareUserRecordVo : shareUserRecordVoTableDataInfo.getRows()) {
                try {

                    if ("0".equals(shareUserRecordVo.getAwardStatus()) || "3".equals(shareUserRecordVo.getAwardStatus())) {
                        // 重新发放
                        userRecordService.sendAward(shareUserRecordVo.getRecordId());
                    } else if ("1".equals(shareUserRecordVo.getAwardStatus())) {
                        // 查询发放状态
                        userRecordService.querySendAwardStatus(shareUserRecordVo.getRecordId());
                    }
                } catch (Exception e) {
                    log.error("分销订单处理异常：", e);
                }
            }
            if ((long) pageNum * pageQuery.getPageSize() >= shareUserRecordVoTableDataInfo.getTotal()) {
                break;
            }
            pageNum++;
        }
    }
}
