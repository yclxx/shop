package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.user.UserException;
import com.ruoyi.common.core.utils.*;
import com.ruoyi.common.core.utils.reflect.ReflectUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteOrderService;
import com.ruoyi.zlyyh.constant.YsfUpConstants;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.AppWxPayCallbackParams;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.bo.ProductAmountBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.enumd.DateType;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.enumd.UnionPay.UnionPayBizMethod;
import com.ruoyi.zlyyh.enumd.UnionPay.UnionPayParams;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyh.param.LianLianParam;
import com.ruoyi.zlyyh.properties.CtripConfig;
import com.ruoyi.zlyyh.properties.WxProperties;
import com.ruoyi.zlyyh.properties.YsfFoodProperties;
import com.ruoyi.zlyyh.properties.utils.YsfDistributionPropertiesUtils;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.*;
import com.ruoyi.zlyyh.utils.sdk.LogUtil;
import com.ruoyi.zlyyh.utils.sdk.PayUtils;
import com.ruoyi.zlyyh.utils.sdk.UnionPayDistributionUtil;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderBo;
import com.ruoyi.zlyyhmobile.domain.bo.OrderProductBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.domain.vo.PayResultVo;
import com.ruoyi.zlyyhmobile.event.SendCouponEvent;
import com.ruoyi.zlyyhmobile.event.ShareOrderEvent;
import com.ruoyi.zlyyhmobile.service.*;
import com.ruoyi.zlyyhmobile.utils.AliasMethod;
import com.ruoyi.zlyyh.utils.redis.ProductUtils;
import com.ruoyi.zlyyh.utils.redis.OrderCacheUtils;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import jodd.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单Service业务处理层
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements IOrderService {
    private static final YsfFoodProperties YSF_FOOD_PROPERTIES = SpringUtils.getBean(YsfFoodProperties.class);
    private static final CtripConfig CtripConfig = SpringUtils.getBean(CtripConfig.class);
    private final YsfConfigService ysfConfigService;
    private final OrderMapper baseMapper;
    private final IPlatformService platformService;
    private final IProductService productService;
    private final ProductMapper productMapper;
    private final IUserService userService;
    private final OrderInfoMapper orderInfoMapper;
    private final OrderFoodInfoMapper orderFoodInfoMapper;
    private final OrderPushInfoMapper orderPushInfoMapper;
    private final OrderUnionPayMapper orderUnionPayMapper;
    private final OrderUnionSendService orderUnionSendService;
    private final IMerchantService merchantService;
    private final ICityMerchantService cityMerchantService;
    private final IProductInfoService productInfoService;
    private final IOrderCardService orderCardService;
    private final IProductPackageService productPackageService;
    private final IProductAmountService productAmountService;
    private final HistoryOrderMapper historyOrderMapper;
    private final OrderBackTransMapper orderBackTransMapper;
    private final RefundMapper refundMapper;
    private final OrderTicketMapper orderTicketMapper;
    private final IUnionPayChannelService unionPayChannelService;
    private final ICodeService codeService;
    private final CollectiveOrderMapper collectiveOrderMapper;
    private final CouponMapper couponMapper;
    private final ProductCouponMapper productCouponMapper;
    private final WxProperties wxProperties;
    private final ICartService cartService;
    private final IMissionUserDrawService missionUserDrawService;
    @DubboReference(retries = 0, timeout = 5000)
    private RemoteOrderService remoteOrderService;
    @Autowired
    private LockTemplate lockTemplate;

    /**
     * 新增订单 新增完成之后重新查询一次，解决二次加密问题
     *
     * @param order 订单信息
     */
    public Order insertOrder(Order order) {
        if (null != order.getProductId()) {
            ProductVo productVo = productService.queryById(order.getProductId());
            if (null != productVo) {
                order.setSysDeptId(productVo.getSysDeptId());
                order.setSysUserId(productVo.getSysUserId());
            }
        }
        int insert = baseMapper.insert(order);
        if (insert <= 0) {
            throw new ServiceException("保存订单失败");
        }
        return baseMapper.selectById(order.getNumber());
    }

    /**
     * 修改订单 修改完成之后重新查询一次，解决二次加密问题
     *
     * @param order 订单信息
     */
    @Override
    public Order updateOrder(Order order) {
        int insert = baseMapper.updateById(order);
        if (insert <= 0) {
            throw new ServiceException("更新订单失败");
        }
        SpringUtils.context().publishEvent(new ShareOrderEvent(null, order.getNumber()));
        return baseMapper.selectById(order.getNumber());
    }

    /**
     * 保存订单至数据库
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrderToMySql(Long number) {
        Order order = OrderCacheUtils.getOrderCache(number);
        if (null == order) {
            String key = "del" + number;
            long l = RedisUtils.incrAtomicValue(key);
            if (l > 10) {
                // 超过10次订单不存在，删除订单相关缓存
                OrderCacheUtils.delOrderCache(number, null);
                RedisUtils.deleteObject(key);
            }
            return;
        }
        // 24小时以上的订单，直接保存至数据库中
        if (DateUtils.getDatePoorHour(order.getCreateTime(), new Date()) < 24) {
            if ("0".equals(order.getStatus()) || "1".equals(order.getStatus())) {
                // 判断订单是否有效
                if (DateUtils.compare(order.getExpireDate()) > 0) {
                    return;
                }
            } else if ("2".equals(order.getStatus())) {
                if ("0".equals(order.getSendStatus())) {
                    // 未发放 发券
                    sendCoupon(order.getNumber());
                    return;
                } else if ("1".equals(order.getSendStatus())) {
                    // 查询订单发放记录
                    List<String> orderPushListCache = OrderCacheUtils.getOrderPushListCache(order.getNumber());
                    if (ObjectUtil.isNotEmpty(orderPushListCache)) {
                        for (String s : orderPushListCache) {
                            // 发放中
                            queryOrderSendStatus(s);
                        }
                    } else {
                        // 一个发券记录都没有，设置成发券中
                        order.setSendStatus("0");
                        OrderCacheUtils.setOrderCache(order);
                    }
                    return;
                }
            }
        }
        // 保存订单
        order = insertOrder(order);
        OrderInfo orderInfoCache = OrderCacheUtils.getOrderInfoCache(number);
        if (null != orderInfoCache) {
            int insert1 = orderInfoMapper.insert(orderInfoCache);
            if (insert1 <= 0) {
                log.error("订单扩展信息保存失败，订单扩展信息：{}", orderInfoCache);
                throw new ServiceException("订单扩展信息新增失败");
            }
        }
        List<String> orderPushListCache = OrderCacheUtils.getOrderPushListCache(number);
        if (ObjectUtil.isNotEmpty(orderPushListCache)) {
            List<OrderPushInfo> orderPushInfos = new ArrayList<>(orderPushListCache.size());
            for (String pushNumber : orderPushListCache) {
                OrderPushInfo orderPushCache = OrderCacheUtils.getOrderPushCache(pushNumber);
                if (null != orderPushCache) {
                    orderPushInfos.add(orderPushCache);
                }
            }
            orderPushInfoMapper.insertBatch(orderPushInfos);
        }
        // 删除订单缓存
        OrderCacheUtils.delOrderCache(order.getNumber(), order.getUserId());
    }

    /**
     * 订单发券
     *
     * @param number 订单号
     */
    @Override
    public void sendCoupon(Long number) {
        if (null == number || number < 1) {
            log.error("订单发券,订单号错误：{}", number);
            return;
        }
        String key = "sendOrderCoupon:" + number;
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        String lockKey = "sendCouponNumbers:" + number;
        // 获取锁成功，处理业务
        try {
            String cacheObject = RedisUtils.getCacheObject(lockKey);
            if (StringUtils.isNotBlank(cacheObject)) {
                return;
            }
            RedisUtils.setCacheObject(lockKey, DateUtil.now(), Duration.ofHours(12));
            Order order = OrderCacheUtils.getOrderCache(number);
            boolean cache = true;
            if (null == order) {
                cache = false;
                order = baseMapper.selectById(number);
            }
            if (null == order || !"2".equals(order.getStatus())) {
                ThreadUtil.sleep(3000);
                order = baseMapper.selectById(number);
            }
            if (null == order || !"2".equals(order.getStatus())) {
                log.error("订单发券,订单不存在或未支付：{}", number);
                RedisUtils.deleteObject(lockKey);
                return;
            }
            if (!"0".equals(order.getSendStatus()) && !"3".equals(order.getSendStatus())) {
                log.error("订单发券中：{}", number);
                return;
            }
            List<OrderPushInfo> orderPushInfos = orderPushInfoMapper.selectList(new LambdaQueryWrapper<OrderPushInfo>().eq(OrderPushInfo::getNumber, order.getNumber()).ne(OrderPushInfo::getStatus, "2"));
            if (ObjectUtil.isNotEmpty(orderPushInfos)) {
                for (OrderPushInfo orderPushInfo : orderPushInfos) {
                    if ("1".equals(orderPushInfo.getStatus())) {
                        order.setPushNumber(orderPushInfo.getPushNumber());
                        order.setSendStatus("2");
                        if (cache) {
                            // 重新缓存
                            OrderCacheUtils.setOrderCache(order);
                        } else {
                            // 修改订单信息
                            order = updateOrder(order);
                        }
                        break;
                    }
                }
                log.info("订单发券中或发放成功number：{}", number);
                return;
            }
            // 新增发券记录
            OrderPushInfo orderPushInfo = new OrderPushInfo();
            orderPushInfo.setNumber(order.getNumber());
            orderPushInfo.setPushNumber(IdUtil.getSnowflakeNextIdStr());
            orderPushInfo.setExternalProductId(order.getExternalProductId());
            orderPushInfo.setStatus("0");
            orderPushInfo.setExternalProductSendValue(order.getExternalProductSendValue());
            // 修改订单状态发券中
            order.setSendStatus("1");
            order.setSendTime(new Date());
            order.setPushNumber(orderPushInfo.getPushNumber());
            String sendAccountType = "0";
            ProductVo productVo = productService.queryById(order.getProductId());
            UserVo userVo = userService.queryById(order.getUserId(), order.getSupportChannel());
            if (null != productVo && null != userVo) {
                sendAccountType = productVo.getSendAccountType();
                if ("0".equals(productVo.getSendAccountType()) && StringUtils.isNotBlank(userVo.getMobile())) {
                    order.setAccount(userVo.getMobile());
                } else if ("1".equals(productVo.getSendAccountType()) && StringUtils.isNotBlank(userVo.getOpenId())) {
                    order.setAccount(userVo.getOpenId());
                }
            }
            if (cache) {
                // 缓存发券订单信息
                OrderCacheUtils.setOrderPushCache(orderPushInfo);
                // 重新缓存
                OrderCacheUtils.setOrderCache(order);
            } else {
                orderPushInfoMapper.insert(orderPushInfo);
                // 修改订单信息
                order = updateOrder(order);
            }
            if (!"16".equals(order.getOrderType())) {
                if (StringUtils.isBlank(order.getExternalProductId()) || (("3".equals(order.getOrderType()) || "10".equals(order.getOrderType()) || "4".equals(order.getOrderType())) && null == order.getExternalProductSendValue())) {
                    if (null == productVo || (!"9".equals(productVo.getProductType()) && StringUtils.isBlank(productVo.getExternalProductId()))) {
                        log.error("订单发券错误，缺少供应商产品ID：{}", number);
                        // 处理结果
                        sendResult(R.fail("缺少供应商产品ID"), orderPushInfo, order, cache, false);
                        return;
                    }
                    if (("3".equals(order.getOrderType()) || "10".equals(order.getOrderType()) || "4".equals(order.getOrderType())) && null == order.getExternalProductSendValue() && null == productVo.getExternalProductSendValue()) {
                        log.error("订单发券错误，缺少发放金额：{}", number);
                        sendResult(R.fail("缺少发放金额"), orderPushInfo, order, cache, false);
                        return;
                    }
                    order.setExternalProductId(productVo.getExternalProductId());
                    order.setExternalProductSendValue(productVo.getExternalProductSendValue());
                    orderPushInfo.setExternalProductId(order.getExternalProductId());
                    orderPushInfo.setExternalProductSendValue(order.getExternalProductSendValue());
                }
            }
            // 通过银联分销分账，生成code
            if ("1".equals(order.getUnionPay()) && !"12".equals(order.getOrderType())) {
                Boolean b = false;
                try {
                    b = codeService.insertByOrder(order.getNumber());
                } catch (Exception e) {
                    log.error("生成核销码异常：", e);
                }
                if (!b) {
                    log.error("订单发券错误，生成核销码失败：{}", number);
                    sendResult(R.fail("订单发券错误，生成核销码失败"), orderPushInfo, order, cache, false);
                    return;
                }
                // 请求银联发券接口
                unionPayChannelService.orderSend(order);
                order = updateOrder(order);
            }
            if ("0".equals(order.getOrderType())) {
                // 请求接口发券
                R<JSONObject> result = YsfUtils.sendCoupon(order.getNumber(), orderPushInfo.getPushNumber(), orderPushInfo.getExternalProductId(), order.getAccount(), order.getCount(), sendAccountType, order.getPlatformKey());
                // 处理结果
                sendResult(result, orderPushInfo, order, cache, true);
            } else if ("1".equals(order.getOrderType()) || "5".equals(order.getOrderType())) {
                //如果是美食套餐订单重新走支付接口
                payFoodOrder(order.getExternalOrderNumber());
            } else if ("3".equals(order.getOrderType()) || "10".equals(order.getOrderType())) {
                // 1云闪付红包
                R<Void> result = YsfUtils.sendAcquire(order.getNumber(), orderPushInfo.getPushNumber(), order.getAccount(), sendAccountType, orderPushInfo.getExternalProductId(), orderPushInfo.getExternalProductSendValue(), order.getProductName(), order.getProductId(), order.getPlatformKey());
                sendResult(result, orderPushInfo, order, cache, false);
            } else if ("4".equals(order.getOrderType())) {
                // 2云闪付积点
                R<Void> result = YsfUtils.memberPointAcquire(order.getNumber(), orderPushInfo.getPushNumber(), orderPushInfo.getExternalProductId(), orderPushInfo.getExternalProductSendValue().longValue(), "0", order.getProductName(), order.getAccount(), sendAccountType, order.getPlatformKey());
                sendResult(result, orderPushInfo, order, cache, false);
            } else if ("6".equals(order.getOrderType()) || "7".equals(order.getOrderType()) || "8".equals(order.getOrderType())) {
                R<Void> result = CloudRechargeUtils.doPostCreateOrder(order.getNumber(), orderPushInfo.getExternalProductId(), order.getAccount(), 1, orderPushInfo.getPushNumber(), null);
                sendResult(result, orderPushInfo, order, cache, false);
            } else if ("9".equals(order.getOrderType())) {
                // 券包
                R<Void> result = productPackageHandle(order.getNumber(), order.getProductId(), order.getUserId(), order.getOrderCityCode(), order.getOrderCityName(), order.getPlatformKey(), order.getSupportChannel());
                sendResult(result, orderPushInfo, order, cache, false);
            } else if ("11".equals(order.getOrderType())) {
                // 代销（银联分销）
                String sendStr = UnionPayDistributionUtil.send(order.getNumber(), orderPushInfo.getExternalProductId(), "1", "0", order.getAccount(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJCAppId(order.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJC(order.getPlatformKey()));
                sendResult(R.ok(sendStr), orderPushInfo, order, cache, false);
            } else if ("12".equals(order.getOrderType())) {
                unionPayChannelService.orderSend(order);
                order = updateOrder(order);
            } else if ("13".equals(order.getOrderType())) {
                // 演出票订单，创建核销码
                codeService.insertByOrder(order.getNumber());
                // 记录联联订单的发码数据
            } else if ("14".equals(order.getOrderType())) {
                OrderFoodInfo lianFoodInfo = orderFoodInfoMapper.selectById(order.getNumber());
                // 没有联联订单记录，创建订单
                if (ObjectUtil.isEmpty(lianFoodInfo)) {
                    lianlianCreateOrder(order, productVo, userVo);
                } else {
                    lianLianOrderCode(order);
                }
            } else if ("15".equals(order.getOrderType())) {
                payCtripFoodOrder(order.getExternalOrderNumber());
            } else if ("16".equals(order.getOrderType())) {
                try {
                    String externalOrderNumber = missionUserDrawService.sendDrawCount(order.getUserId(), order.getProductId(), order.getExternalProductId());
                    order.setExternalOrderNumber(externalOrderNumber);
                    sendResult(R.ok("发放成功"), orderPushInfo, order, cache, false);
                } catch (Exception e) {
                    sendResult(R.fail(e.getMessage()), orderPushInfo, order, cache, false);
                }
            } else if ("18".equals(order.getOrderType())) {
                String chnlId = ysfConfigService.queryValueByKey(order.getPlatformKey(), YsfUpConstants.up_chnlId);
                String appId = ysfConfigService.queryValueByKey(order.getPlatformKey(), YsfUpConstants.up_appId);
                String sm4Key = ysfConfigService.queryValueByKey(order.getPlatformKey(), YsfUpConstants.up_sm4Key);
                String rsaPrivateKey = ysfConfigService.queryValueByKey(order.getPlatformKey(), YsfUpConstants.up_rsaPrivateKey);
                String entityTp = ysfConfigService.queryValueByKey(order.getPlatformKey(), YsfUpConstants.up_entityTp);
                // 银联开放平台发券
                R<JSONObject> result = YsfUtils.couponAcquire(orderPushInfo.getPushNumber(), order.getExternalProductId(), order.getAccount(), order.getCount().toString(), entityTp, chnlId, appId, rsaPrivateKey, sm4Key);
                // 处理结果
                sendResult(result, orderPushInfo, order, cache, true);
            } else {
                sendResult(R.fail("订单类型无处理方式，请联系技术人员"), orderPushInfo, order, cache, false);
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
            RedisUtils.decrAtomicValue(ZlyyhConstants.SEND_COUPON_NUMBER);
            RedisUtils.deleteObject(lockKey);
        }
    }

    @Override
    public void queryOrderSendStatus(String pushNumber) {
        if (StringUtils.isBlank(pushNumber)) {
            return;
        }
        OrderPushInfo orderPushCache = OrderCacheUtils.getOrderPushCache(pushNumber);
        boolean cache = true;
        if (null == orderPushCache) {
            cache = false;
            orderPushCache = orderPushInfoMapper.selectOne(new LambdaQueryWrapper<OrderPushInfo>().eq(OrderPushInfo::getPushNumber, pushNumber));
        }
        if (null == orderPushCache) {
            log.error("发券订单号{}，不存在发券订单", pushNumber);
            return;
        }
        Order order;
        if (cache) {
            order = OrderCacheUtils.getOrderCache(orderPushCache.getNumber());
        } else {
            order = baseMapper.selectById(orderPushCache.getNumber());
        }
        if (null == order) {
            log.error("发券订单号{}，不存在订单", pushNumber);
            return;
        }
        if (!"0".equals(orderPushCache.getStatus())) {
            log.error("发券订单号{}，订单已有最终状态，不做查询处理", pushNumber);
            if ("1".equals(orderPushCache.getStatus())) {
                sendResult(R.ok("订单已发券"), orderPushCache, order, cache, false);
            } else if ("2".equals(orderPushCache.getStatus())) {
                sendResult(R.fail(orderPushCache.getRemark()), orderPushCache, order, cache, false);
            }
            return;
        }
        if ("0".equals(order.getOrderType())) {
            R<JSONObject> result = YsfUtils.queryCoupon(orderPushCache.getNumber(), orderPushCache.getPushNumber(), orderPushCache.getCreateTime(), order.getPlatformKey());
            // 处理结果
            sendResult(result, orderPushCache, order, cache, false);
        } else if ("6".equals(order.getOrderType()) || "7".equals(order.getOrderType()) || "8".equals(order.getOrderType())) {
            R<JSONObject> result = CloudRechargeUtils.doPostQueryOrder(order.getNumber(), orderPushCache.getPushNumber());
            sendResult(result, orderPushCache, order, cache, false);
        } else if ("9".equals(order.getOrderType())) {
            R<Void> result = queryProductPackageHandle(order.getNumber(), order.getProductId());
            sendResult(result, orderPushCache, order, cache, false);
        } else if ("11".equals(order.getOrderType()) || "12".equals(order.getOrderType())) {
            JSONObject data = queryProductUnionSendHandle(order.getNumber());
            if (data != null) {
                rechargeUnionResult(data, orderPushCache, order);
                orderPushInfoMapper.updateById(orderPushCache);
                order = updateOrder(order);
            }
        } else if ("18".equals(order.getOrderType())) {
            String chnlId = ysfConfigService.queryValueByKey(order.getPlatformKey(), YsfUpConstants.up_chnlId);
            String appId = ysfConfigService.queryValueByKey(order.getPlatformKey(), YsfUpConstants.up_appId);
            String rsaPrivateKey = ysfConfigService.queryValueByKey(order.getPlatformKey(), YsfUpConstants.up_rsaPrivateKey);
            R<JSONObject> result = YsfUtils.couponAcqQuery(orderPushCache.getPushNumber(), DateUtil.format(orderPushCache.getCreateTime(), DatePattern.PURE_DATE_PATTERN), chnlId, appId, rsaPrivateKey);
            // 处理结果
            sendResult(result, orderPushCache, order, cache, false);
        }
    }

    private void sendResult(R result, OrderPushInfo orderPushCache, Order order, boolean cache, boolean warnQueryCoupon) {
        if (R.isSuccess(result)) {
            if ("6".equals(order.getOrderType()) || "7".equals(order.getOrderType()) || "8".equals(order.getOrderType())) {
                JSONObject data = (JSONObject) result.getData();
                rechargeResult(data, orderPushCache, order);
            } else if ("11".equals(order.getOrderType()) || "12".equals(order.getOrderType())) {
                JSONObject send = JSONObject.parseObject(result.getMsg());
                if (send.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
                    ProductVo productVo = productService.queryById(order.getProductId());
                    OrderUnionPay orderUnionPay = orderUnionPayMapper.selectById(order.getNumber());
                    this.orderUnionPaySendHand(productVo.getExternalProductId(), order, orderUnionPay, send);
                    rechargeUnionResult(send, orderPushCache, order);
                } else {
                    order.setSendStatus("3");
                    log.info("银联分销支付异常。{}", send.getString("msg"));
                    log.info("银联分销支付异常详情。{}", send.getString("subMsg"));
                }
            } else {
                // 发放成功
                orderPushCache.setStatus("1");
                order.setSendStatus("2");
                if (null != result.getData()) {
                    if ("18".equals(order.getOrderType())) {
                        try {
                            JSONObject data = (JSONObject) result.getData();
                            order.setExternalOrderNumber(data.getString("couponCd"));
                            orderPushCache.setExternalOrderNumber(order.getExternalOrderNumber());
                            order.setUsedStartTime(DateUtil.parse(data.getString("couponBeginTs")));
                            order.setUsedEndTime(DateUtil.parse(data.getString("couponEndTs")));
                        } catch (Exception ignored) {
                        }
                    }
                    orderPushCache.setRemark(JSONObject.toJSONString(result.getData()));
                } else {
                    orderPushCache.setRemark(result.getMsg());
                }
                if (StringUtils.isNotBlank(orderPushCache.getRemark()) && orderPushCache.getRemark().length() >= 5000) {
                    orderPushCache.setRemark(orderPushCache.getRemark().substring(0, 4900));
                }
            }
        } else {
            OrderVo orderVo = baseMapper.selectVoById(order.getNumber());
            if (null == orderVo || "2".equals(orderVo.getSendStatus())) {
                return;
            }
            if (R.FAIL == result.getCode()) {
                // 发放失败
                orderPushCache.setStatus("2");
                orderPushCache.setRemark(result.getMsg());
                if (StringUtils.isNotBlank(orderPushCache.getRemark()) && orderPushCache.getRemark().length() >= 5000) {
                    orderPushCache.setRemark(orderPushCache.getRemark().substring(0, 4900));
                }
                try {
                    // 设置发送失败次数,到达上限发送云闪付服务消息.
                    if (orderPushCache.getNumber() != null) {
                        Object cacheObject = RedisUtils.getCacheObject(ZlyyhConstants.ysfOrderErrorNum);
                        if (cacheObject != null) {
                            String errorCouponNumber = ysfConfigService.queryValueByKey(order.getPlatformKey(), "errorCouponNumber");
                            if (StringUtils.isNotBlank(errorCouponNumber)) {
                                Integer num = Integer.parseInt(errorCouponNumber);
                                int number = Integer.parseInt(cacheObject.toString());
                                // 满足条件,发送云闪付服务消息.
                                if (number >= num) {
                                    PlatformVo platformVo = platformService.queryById(order.getPlatformKey(), PlatformEnumd.MP_YSF.getChannel());
                                    if (null != platformVo) {
                                        String backendToken = YsfUtils.getBackendToken(platformVo.getAppId(), platformVo.getSecret(), false, platformVo.getPlatformKey());
                                        this.ysfForewarningMessage(order.getPlatformKey(), backendToken, "errorDescDetails", "errorTemplateValue");
                                    }
                                }
                                long timeToLive = RedisUtils.getTimeToLive(ZlyyhConstants.ysfOrderErrorNum);
                                RedisUtils.setCacheObject(ZlyyhConstants.ysfOrderErrorNum, Integer.valueOf(number + 1).toString(), Duration.ofMillis(timeToLive));
                            }
                        } else {
                            RedisUtils.setCacheObject(ZlyyhConstants.ysfOrderErrorNum, "1", Duration.ofMinutes(30));
                        }
                    }
                } catch (Exception e) {
                    log.info("推送订单失败信息至云闪付异常：", e);
                }
                order.setSendStatus("3");
                order.setFailReason(orderPushCache.getRemark());
            } else {
                // 请求接口查询发放状态
                if (warnQueryCoupon) {
                    queryOrderSendStatus(orderPushCache.getPushNumber());
                }
                return;
            }
        }
        if (cache) {
            // 缓存发券订单信息
            OrderCacheUtils.setOrderPushCache(orderPushCache);
            // 重新缓存
            OrderCacheUtils.setOrderCache(order);
        } else {
            orderPushInfoMapper.updateById(orderPushCache);
            // 修改订单信息
            order = updateOrder(order);
        }
    }

    /**
     * 创建订单
     *
     * @return 返回结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CreateOrderResult createOrder(CreateOrderBo bo, boolean system) {
        PlatformVo platformVo = platformService.queryById(bo.getPlatformKey(), bo.getChannel());
        if (null == platformVo) {
            throw new ServiceException("请求失败，请退出重试");
        }
//        // 校验城市
//        if (StringUtils.isBlank(bo.getCityCode())) {
//            throw new ServiceException("未获取到您的位置信息,请确认是否开启定位服务");
//        }
//        if (StringUtils.isNotBlank(platformVo.getPlatformCity()) && !"ALL".equalsIgnoreCase(platformVo.getPlatformCity()) && !platformVo.getPlatformCity().contains(bo.getCityCode())) {
//            throw new ServiceException("您当前所在位置不在活动参与范围!");
//        }
        // 加锁，防止并发抢购问题
        String key = "createOrder:" + platformVo.getPlatformKey() + ":" + bo.getUserId() + ":" + bo.getProductId();
        final LockInfo lockInfo = lockTemplate.lock(key, 30000L, 1000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            throw new ServiceException("操作频繁，请稍后重试");
        }
        // 获取锁成功，处理业务
        try {
            // 校验是否有订单，有订单直接返回
            String cacheObject = RedisUtils.getCacheObject(OrderCacheUtils.getUsreOrderOneCacheKey(platformVo.getPlatformKey(), bo.getUserId(), bo.getProductId()));
            if (StringUtils.isNotBlank(cacheObject)) {
                Order order = RedisUtils.getCacheObject(cacheObject);
                if (null != order && "0".equals(order.getStatus())) {
                    return new CreateOrderResult(order.getCollectiveNumber(), order.getNumber(), "1");
                }
            }
            UserVo userVo = userService.queryById(bo.getUserId(), bo.getChannel());
            if (null == userVo || "0".equals(userVo.getReloadUser()) || StringUtils.isBlank(userVo.getMobile())) {
                throw new ServiceException("登录超时，请退出重试[user]", HttpStatus.HTTP_UNAUTHORIZED);
            }
            if (!platformVo.getPlatformKey().equals(userVo.getPlatformKey())) {
                throw new ServiceException("登录超时，请退出重试[platform]", HttpStatus.HTTP_UNAUTHORIZED);
            }
            if ("1".equals(userVo.getStatus())) {
                throw new UserException("user.blocked", userVo.getMobile());
            }
            if (ObjectUtil.isEmpty(bo.getProductId())) {
                //此处下单环节商品id不能为空
                throw new ServiceException("商品不存在或已下架");
            }
            // 查询商品信息
            ProductVo productVo = productService.queryById(bo.getProductId());
            if (null == productVo || !"0".equals(productVo.getStatus())) {
                throw new ServiceException("商品不存在或已下架!");
            }
            if (null != productVo.getPlatformKey() && productVo.getPlatformKey() > 1 && !platformVo.getPlatformKey().equals(productVo.getPlatformKey())) {
                throw new ServiceException("商品错误!");
            }
            if ("1".equals(productVo.getProductAffiliation())) {
                throw new ServiceException("商品不可购买");
            }
            if (!system && !"0".equals(productVo.getSearch())) {
                throw new ServiceException("商品不可购买[请求校验不通过]");
            }
            // 校验产品状态 名额
            R<ProductVo> checkProductCountResult = ProductUtils.checkProduct(productVo, bo.getCityCode());
            if (R.isError(checkProductCountResult)) {
                throw new ServiceException(checkProductCountResult.getMsg());
            }
            // 校验用户是否达到参与上限
            ProductUtils.checkUserCount(productVo, userVo.getUserId());
            //此处先生成大订单(此处下单只有一个商品 只需记录价格等信息)
            CollectiveOrder collectiveOrder = new CollectiveOrder();
            collectiveOrder.setCollectiveNumber(IdUtil.getSnowflakeNextId());
            collectiveOrder.setUserId(bo.getUserId());
            collectiveOrder.setOrderCityCode(bo.getAdcode());
            collectiveOrder.setOrderCityName(bo.getCityName());
            collectiveOrder.setPlatformKey(platformVo.getPlatformKey());
            collectiveOrder.setStatus("0");
            collectiveOrder.setExpireDate(DateUtil.offsetMinute(new Date(), 15).toJdkDate());
            // 生成订单
            Order order = new Order();
            order.setCollectiveNumber(collectiveOrder.getCollectiveNumber());
            order.setNumber(IdUtil.getSnowflakeNextId());
            order.setProductId(productVo.getProductId());
            order.setCusRefund(productVo.getCusRefund());
            order.setAutoRefund(productVo.getAutoRefund());
            order.setUserId(bo.getUserId());
            order.setProductName(productVo.getProductName());
            order.setProductImg(productVo.getProductImg());
            order.setPickupMethod(productVo.getPickupMethod());
            order.setSupportChannel(bo.getChannel());
            order.setExpireDate(collectiveOrder.getExpireDate());
            if (null != bo.getPayCount() && bo.getPayCount() > 0) {
                if (bo.getPayCount() > 10) {
                    throw new ServiceException("单次购买数量不能超过10");
                }
                order.setCount(bo.getPayCount());
                collectiveOrder.setCount(bo.getPayCount());
            } else {
                order.setCount(1L);
                collectiveOrder.setCount(1L);
            }
            order.setStatus("0");
            if ("1".equals(productVo.getSendAccountType())) {
                order.setAccount(userVo.getOpenId());
            } else {
                order.setAccount(userVo.getMobile());
            }
            order.setExternalProductId(productVo.getExternalProductId());
            order.setOrderCityCode(bo.getAdcode());
            order.setOrderCityName(bo.getCityName());
            order.setPlatformKey(platformVo.getPlatformKey());
            order.setExternalProductSendValue(productVo.getExternalProductSendValue());
            order.setOrderType(productVo.getProductType());
            order.setParentNumber(bo.getParentNumber());
            order.setUsedStartTime(productVo.getUsedStartTime());
            order.setUsedEndTime(productVo.getUsedEndTime());
            order.setUnionPay(productVo.getUnionPay());
            order.setUnionProductId(productVo.getUnionProductId());

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setNumber(order.getNumber());
            orderInfo.setCommodityJson(JsonUtils.toJsonString(productVo));
            // 查询是否是62会员 先查缓存
            boolean vipCache = !"8".equals(productVo.getProductType());
            MemberVipBalanceVo user62VipInfo = userService.getUser62VipInfo(vipCache, userVo.getUserId());
            if (null != user62VipInfo) {
                if ("01".equals(user62VipInfo.getStatus()) || "03".equals(user62VipInfo.getStatus())) {
                    if ("8".equals(productVo.getProductType())) {
                        Date endDate = DateUtil.parse(user62VipInfo.getEndTime());
                        if (null != endDate && DateUtil.between(new Date(), endDate, DateUnit.DAY) > 365) {
                            throw new ServiceException("已达续费上限");
                        }
                    }
                } else {
                    if ("1".equals(productVo.getPayUser())) {
                        // 不是会员，再次查询，防止用户开通之后，我方缓存未更新问题
                        user62VipInfo = userService.getUser62VipInfo(false, userVo.getUserId());
                        if (!"01".equals(user62VipInfo.getStatus()) && !"03".equals(user62VipInfo.getStatus())) {
                            throw new ServiceException("请先开通62会员");
                        }
                    }
                }
                orderInfo.setVip62Status(user62VipInfo.getStatus());
                orderInfo.setVip62MemberType(user62VipInfo.getMemberType());
                orderInfo.setVip62BeginTime(user62VipInfo.getBeginTime());
                orderInfo.setVip62EndTime(user62VipInfo.getEndTime());
            } else {
                if ("1".equals(productVo.getPayUser()) || "8".equals(productVo.getProductType())) {
                    throw new ServiceException("查询62会员状态失败,请重新授权手机号重试");
                }
            }
            //如果是供应商美食订单走这里
            addFoodOrder(productVo, order, userVo, platformVo);

            // 设置领取缓存
            this.setOrderCountCache(platformVo.getPlatformKey(), bo.getUserId(), productVo.getProductId(), productVo.getSellEndDate(), order.getCount());
            try {
                if ("0".equals(productVo.getPickupMethod())) {
                    order.setStatus("2");
                    order.setPayTime(new Date());
                    // 保存订单 后续如需改动成缓存订单，需注释
                    order = insertOrder(order);
                    collectiveOrder.setStatus("2");

                    collectiveOrderMapper.insert(collectiveOrder);
                    orderInfoMapper.insert(orderInfo);
                    // 缓存订单 暂时先不用 需要改动地方太多
                    //            OrderCacheUtils.setOrderCache(order);
                    //            OrderCacheUtils.setOrderInfoCache(orderInfo);
                    // 发券
                    //发券之前判断是否为发放金额为随机的商品
                    if ("10".equals(order.getOrderType())) {
                        checkRandomProduct(order);
                    }
                    order = baseMapper.selectById(order.getNumber());
                    collectiveOrder = getCollectiveOrder(collectiveOrder.getCollectiveNumber());
                    SpringUtils.context().publishEvent(new SendCouponEvent(order.getNumber(), order.getPlatformKey()));
                    // 分销处理
                    SpringUtils.context().publishEvent(new ShareOrderEvent(bo.getShareUserId(), order.getNumber()));
                    return new CreateOrderResult(collectiveOrder.getCollectiveNumber(), order.getNumber(), "0");
                }
                // 需支付
                BigDecimal amount = productVo.getSellAmount();
                BigDecimal reducedPrice = new BigDecimal("0");
                // 62会员
                if (null != user62VipInfo) {
                    if ("01".equals(user62VipInfo.getStatus()) || "03".equals(user62VipInfo.getStatus())) {
                        if (null != productVo.getVipUpAmount() && productVo.getVipUpAmount().signum() > 0) {
                            reducedPrice = amount.subtract(productVo.getVipUpAmount());
                        }
                    }
                }
                // 权益会员
                if ("1".equals(userVo.getVipUser())) {
                    if (null != productVo.getVipAmount() && productVo.getVipAmount().signum() > 0) {
                        reducedPrice = amount.subtract(productVo.getVipAmount());
                    }
                }
                boolean couponFlag = false;
                // 如果使用了优惠券
                if (ObjectUtil.isNotEmpty(bo.getCouponId())) {
                    order.setCouponId(bo.getCouponId());
                    collectiveOrder.setCouponId(bo.getCouponId());
                    // 查询优惠券
                    Coupon coupon = couponMapper.selectById(bo.getCouponId());
                    if (ObjectUtil.isEmpty(coupon)) {
                        throw new ServiceException("请求异常，请稍后重试");
                    }
                    // 验证优惠券状态，以及使用时间
                    if (!"1".equals(coupon.getUseStatus())
                        || (ObjectUtil.isNotEmpty(coupon.getPeriodOfStart()) && !DateUtils.validTime(coupon.getPeriodOfStart(), 1))
                        || (ObjectUtil.isNotEmpty(coupon.getPeriodOfValidity()) && DateUtils.validTime(coupon.getPeriodOfValidity(), 1))) {
                        throw new ServiceException("优惠券不可用！");
                    }

                    // 最低使用金额
                    if (amount.compareTo(coupon.getMinAmount()) <= 0) {
                        throw new ServiceException("优惠券需订单金额超过" + coupon.getMinAmount() + "元才可用！");
                    }
                    //判断是否为专属商品|优惠券判断购买商品id是否存在于优惠券商品关联表中
                    List<ProductCoupon> productCoupons = productCouponMapper.selectList(new LambdaQueryWrapper<ProductCoupon>().eq(ProductCoupon::getCouponId, bo.getCouponId()));
                    if ((coupon.getCouponType().equals("1") || coupon.getCouponType().equals("3")) && ObjectUtil.isEmpty(productCoupons)) {
                        throw new ServiceException("该优惠券指定商品可用！");
                    }
                    if (ObjectUtil.isNotEmpty(productCoupons)) {
                        //关联表不为空判断购买的商品id是否存在 不存在抛异常
                        List<Long> productIds = productCoupons.stream().map(ProductCoupon::getProductId).collect(Collectors.toList());
                        boolean couponProduct = productIds.contains(bo.getProductId());
                        if (!couponProduct) {
                            throw new ServiceException("优惠券指定商品可用！");
                        }
                    }

                    if (coupon.getCouponType().equals("1")) {
                        couponFlag = true;
                        // 优惠券状态改变成已使用 兑了相当于已使用
                        coupon.setUseStatus("3");
                        coupon.setUseTime(new Date());
                        coupon.setNumber(order.getNumber().toString());
                        couponMapper.updateById(coupon);
                        reducedPrice = coupon.getCouponAmount().add(reducedPrice);

                    } else if (coupon.getCouponType().equals("2") || coupon.getCouponType().equals("3")) {
                        //全场通用券或者指定商品立减券

                        // 优惠券状态改变成已绑定
                        coupon.setUseStatus("2");
                        coupon.setUseTime(new Date());
                        coupon.setNumber(order.getNumber().toString());
                        couponMapper.updateById(coupon);
                        reducedPrice = coupon.getCouponAmount().add(reducedPrice);
                    }

                }

                order.setTotalAmount(amount.multiply(new BigDecimal(order.getCount())));
                order.setReducedPrice(reducedPrice.multiply(new BigDecimal(order.getCount())));
                order.setWantAmount(order.getTotalAmount().subtract(order.getReducedPrice()));
                if (order.getWantAmount().signum() < 1) {
                    order.setWantAmount(new BigDecimal("0.01"));
                }
                //添加大订单价格
                collectiveOrder.setTotalAmount(amount.multiply(new BigDecimal(order.getCount())));
                collectiveOrder.setReducedPrice(reducedPrice.multiply(new BigDecimal(order.getCount())));
                collectiveOrder.setWantAmount(order.getTotalAmount().subtract(order.getReducedPrice()));
                if (collectiveOrder.getWantAmount().signum() < 1) {
                    collectiveOrder.setWantAmount(new BigDecimal("0.01"));
                }
                if ("12".equals(productVo.getProductType()) || "1".equals(productVo.getUnionPay())) {
                    String externalProductId = "1".equals(productVo.getUnionPay()) ? productVo.getUnionProductId() : productVo.getExternalProductId();
                    if (StringUtils.isEmpty(externalProductId)) {
                        throw new ServiceException("抱歉，商品配置错误[expid]");
                    }
                    unionPayChannelService.createUnionPayOrder(externalProductId, order);
                }
                if (couponFlag) {
                    //如果是通兑券 直接发放奖品
                    order.setStatus("2");
                    order.setPayTime(new Date());
                    // 保存订单 后续如需改动成缓存订单，需注释
                    order = insertOrder(order);
                    orderInfoMapper.insert(orderInfo);
                    collectiveOrder.setStatus("2");
                    collectiveOrder.setSysUserId(order.getSysUserId());
                    collectiveOrder.setSysDeptId(order.getSysDeptId());
                    collectiveOrderMapper.insert(collectiveOrder);
                    if ("10".equals(order.getOrderType())) {
                        checkRandomProduct(order);
                    }
                    order = baseMapper.selectById(order.getNumber());
                    collectiveOrder = getCollectiveOrder(collectiveOrder.getCollectiveNumber());
                    SpringUtils.context().publishEvent(new SendCouponEvent(order.getNumber(), order.getPlatformKey()));
                    // 分销处理
                    SpringUtils.context().publishEvent(new ShareOrderEvent(bo.getShareUserId(), order.getNumber()));
                    return new CreateOrderResult(collectiveOrder.getCollectiveNumber(), order.getNumber(), "0");
                }
                // 保存订单 后续如需改动成缓存订单，需注释
                order = insertOrder(order);
                collectiveOrder.setSysUserId(order.getSysUserId());
                collectiveOrder.setSysDeptId(order.getSysDeptId());
                collectiveOrderMapper.insert(collectiveOrder);
                orderInfoMapper.insert(orderInfo);
                collectiveOrder = getCollectiveOrder(collectiveOrder.getCollectiveNumber());

                // 缓存订单 暂时先不用 需要改动地方太多
//            OrderCacheUtils.setOrderCache(order);
//            OrderCacheUtils.setOrderInfoCache(orderInfo);
                //方法里加入小订单
                cacheOrder(order);
                // 分销处理
                SpringUtils.context().publishEvent(new ShareOrderEvent(bo.getShareUserId(), order.getNumber()));
                return new CreateOrderResult(collectiveOrder.getCollectiveNumber(), order.getNumber(), "1");
            } catch (Exception e) {
                // 如果发生异常 回退名额
                callbackOrderCountCache(order.getPlatformKey(), order.getUserId(), order.getProductId(), null, order.getCount());
                throw e;
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 购物车创建订单
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CreateOrderResult createCarOrder(CreateOrderBo bo, boolean system) {
        PlatformVo platformVo = platformService.queryById(bo.getPlatformKey(), bo.getChannel());
        if (null == platformVo) {
            throw new ServiceException("请求失败，请退出重试");
        }
        UserVo userVo = userService.queryById(bo.getUserId(), bo.getChannel());
        if (null == userVo || "0".equals(userVo.getReloadUser()) || StringUtils.isBlank(userVo.getMobile())) {
            throw new ServiceException("登录超时，请退出重试[user]", HttpStatus.HTTP_UNAUTHORIZED);
        }
        if (!platformVo.getPlatformKey().equals(userVo.getPlatformKey())) {
            throw new ServiceException("登录超时，请退出重试[platform]", HttpStatus.HTTP_UNAUTHORIZED);
        }
        if ("1".equals(userVo.getStatus())) {
            throw new UserException("user.blocked", userVo.getMobile());
        }
        // 订单优惠总金额
        BigDecimal reducedPrice = new BigDecimal("0");
        //计算的商品总金额
        BigDecimal cAmount = new BigDecimal("0");
        //优惠券金额
        BigDecimal couponAmount = new BigDecimal("0");
        // 商品总数量
        Long count = 0L;
        //此处先生成大订单
        CollectiveOrder collectiveOrder = new CollectiveOrder();
        collectiveOrder.setCollectiveNumber(IdUtil.getSnowflakeNextId());
        collectiveOrder.setUserId(bo.getUserId());
        collectiveOrder.setOrderCityCode(bo.getAdcode());
        collectiveOrder.setOrderCityName(bo.getCityName());
        collectiveOrder.setPlatformKey(platformVo.getPlatformKey());
        collectiveOrder.setStatus("0");
        collectiveOrder.setExpireDate(DateUtil.offsetMinute(new Date(), 15).toJdkDate());
        // 查询是否是62会员 先查缓存
        MemberVipBalanceVo user62VipInfo = userService.getUser62VipInfo(true, userVo.getUserId());
        //查出来购物车中的商品 分为子订单
        if (ObjectUtil.isEmpty(bo.getOrderProductBos())) {
            //此处下单环节商品列表不能为空
            throw new ServiceException("购物车无商品或已下架");
        }
        List<Long> productIds = bo.getOrderProductBos().stream().map(OrderProductBo::getProductId).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(productIds)) {
            throw new ServiceException("请求错误,请退出重试");
        }
        // 查询商品列表
        List<ProductVo> productVoList = productMapper.selectVoBatchIds(productIds);
        // list 转map
        Map<Long, ProductVo> productVoMap = productVoList.stream().collect(Collectors.toMap(ProductVo::getProductId, productVo -> productVo));
        for (OrderProductBo orderProductBo : bo.getOrderProductBos()) {
            //这个循环判断购物车是否能够下单/计算商品金额
            //此处判断商品名额状态并且获取金额
            if (orderProductBo.getQuantity() < 1) {
                throw new ServiceException("购买数量非法");
            }
            //判断商品是否可以购买
            ProductVo productVo = productVoMap.get(orderProductBo.getProductId());
            if (ObjectUtil.isNotEmpty(productVo.getLineUpperLimit())) {
                if (orderProductBo.getQuantity() > productVo.getLineUpperLimit()) {
                    throw new ServiceException(productVo.getProductName() + "单次购买数量不能超过" + productVo.getLineUpperLimit() + "次");
                }
            }
            // 校验产品状态 名额
            R<ProductVo> checkProductCountResult = ProductUtils.checkProduct(productVo, bo.getCityCode());
            if (R.isError(checkProductCountResult)) {
                throw new ServiceException(checkProductCountResult.getMsg());
            }
            // 校验用户是否达到参与上限
            ProductUtils.checkUserCount(productVo, userVo.getUserId());
            // 购物车不走免费商品/内容分销商品/积点商品
            if (productVo.getProductType().equals("10") || productVo.getProductType().equals("11") || productVo.getProductType().equals("12") || productVo.getPickupMethod().equals("0") || productVo.getPickupMethod().equals("2")) {
                throw new ServiceException(productVo.getProductName() + "该商品请在商品详情页直接点击购买");
            }
            if (null != user62VipInfo) {
                if ("01".equals(user62VipInfo.getStatus()) || "03".equals(user62VipInfo.getStatus())) {
                    if ("8".equals(productVo.getProductType())) {
                        Date endDate = DateUtil.parse(user62VipInfo.getEndTime());
                        if (null != endDate && DateUtil.between(new Date(), endDate, DateUnit.DAY) > 365) {
                            throw new ServiceException("已达续费上限");
                        }
                    }
                } else {
                    if ("1".equals(productVo.getPayUser())) {
                        // 不是会员，再次查询，防止用户开通之后，我方缓存未更新问题
                        user62VipInfo = userService.getUser62VipInfo(false, userVo.getUserId());
                        if (!"01".equals(user62VipInfo.getStatus()) && !"03".equals(user62VipInfo.getStatus())) {
                            throw new ServiceException("请先开通62会员");
                        }
                    }
                }
            } else {
                if ("1".equals(productVo.getPayUser()) || "8".equals(productVo.getProductType())) {
                    throw new ServiceException("查询62会员状态失败,请重新授权手机号重试");
                }
            }
            //计算商品金额
            //62会员价
            if (null != user62VipInfo) {
                if ("01".equals(user62VipInfo.getStatus()) || "03".equals(user62VipInfo.getStatus())) {
                    if (null != productVo.getVipUpAmount() && productVo.getVipUpAmount().signum() > 0) {
                        cAmount = cAmount.add(productVo.getVipUpAmount());
                        continue;
                    }
                }
            }
            // 权益会员
            if ("1".equals(userVo.getVipUser())) {
                if (null != productVo.getVipAmount() && productVo.getVipAmount().signum() > 0) {
                    cAmount = cAmount.add(productVo.getVipAmount());
                    continue;
                }
            }
            cAmount = cAmount.add(productVo.getSellAmount());
        }
        // 如果使用了优惠券
        if (ObjectUtil.isNotEmpty(bo.getCouponId())) {
            collectiveOrder.setCouponId(bo.getCouponId());
            // 查询优惠券
            Coupon coupon = couponMapper.selectById(bo.getCouponId());
            if (ObjectUtil.isEmpty(coupon)) {
                throw new ServiceException("请求异常，请稍后重试");
            }
            // 验证优惠券状态，以及使用时间
            if (!"1".equals(coupon.getUseStatus())
                || (ObjectUtil.isNotEmpty(coupon.getPeriodOfStart()) && !DateUtils.validTime(coupon.getPeriodOfStart(), 1))
                || (ObjectUtil.isNotEmpty(coupon.getPeriodOfValidity()) && DateUtils.validTime(coupon.getPeriodOfValidity(), 1))) {
                throw new ServiceException("优惠券不可用！");
            }
            // 最低使用金额
            if (cAmount.compareTo(coupon.getMinAmount()) <= 0) {
                throw new ServiceException("优惠券需订单金额超过" + coupon.getMinAmount() + "元才可用！");
            }
            if (coupon.getCouponType().equals("1")) {
                throw new ServiceException(coupon.getCouponName() + "请直接在商品详情下单使用");
            }
            if (coupon.getCouponType().equals("3")) {
                //如果是指定商品用券 先查表
                //判断是否为专属商品|优惠券判断购买商品id是否存在于优惠券商品关联表中
                List<ProductCoupon> productCoupons = productCouponMapper.selectList(new LambdaQueryWrapper<ProductCoupon>().eq(ProductCoupon::getCouponId, bo.getCouponId()));
                if (ObjectUtil.isNotEmpty(productCoupons)) {
                    //关联表不为空判断购买的商品id是否存在 不存在抛异常
                    List<Long> productIds1 = productCoupons.stream().map(ProductCoupon::getProductId).collect(Collectors.toList());
                    Boolean couponProduct = isProductCoupon(productIds1, productIds);
                    if (!couponProduct) {
                        throw new ServiceException("优惠券指定商品可用！");
                    }
                } else {
                    throw new ServiceException("优惠券指定商品可用！");
                }
            }
            if (coupon.getCouponType().equals("2") || coupon.getCouponType().equals("3")) {
                //全场通用券或者指定商品立减券
                // 优惠券状态改变成已绑定
                coupon.setUseStatus("2");
                coupon.setUseTime(new Date());
                coupon.setNumber(collectiveOrder.getCollectiveNumber().toString());
                couponMapper.updateById(coupon);
                reducedPrice = coupon.getCouponAmount().add(reducedPrice);
                couponAmount = reducedPrice;
            }
        }
        // 商品总金额
        BigDecimal amount = new BigDecimal("0");
        // 优惠券已分至子订单金额
        BigDecimal couponShareOrderAmount = new BigDecimal("0");
        int orderCount = 0;
        //循环添加子订单
        for (OrderProductBo orderProductBo : bo.getOrderProductBos()) {
            //这个循环主要做订单的新增/价格的计算/购物车的删减
            ProductVo productVo = productVoMap.get(orderProductBo.getProductId());
            count = count + orderProductBo.getQuantity();
            if (ObjectUtil.isNotEmpty(orderProductBo.getCartId())) {
                CartVo cartVo = cartService.queryById(orderProductBo.getCartId());
                if (ObjectUtil.isNotEmpty(cartVo)) {
                    if (!cartVo.getUserId().equals(bo.getUserId())) {
                        throw new ServiceException("登录超时,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
                    }
                    // 删除购物车产品
                    List<Long> id = new ArrayList<>();
                    id.add(cartVo.getId());
                    cartService.deleteWithValidByIds(id, bo.getUserId(), true);
                }
            }
            // 生成订单
            Order order = new Order();
            order.setCollectiveNumber(collectiveOrder.getCollectiveNumber());
            order.setNumber(IdUtil.getSnowflakeNextId());
            order.setProductId(productVo.getProductId());
            order.setCusRefund(productVo.getCusRefund());
            order.setUserId(bo.getUserId());
            order.setProductName(productVo.getProductName());
            order.setProductImg(productVo.getProductImg());
            order.setAutoRefund(productVo.getAutoRefund());
            order.setPickupMethod(productVo.getPickupMethod());
            order.setSupportChannel(bo.getChannel());
            order.setExpireDate(collectiveOrder.getExpireDate());
            if (null != orderProductBo.getQuantity() && orderProductBo.getQuantity() > 0) {
                if (orderProductBo.getQuantity() > 99) {
                    throw new ServiceException("单次购买数量不能超过99");
                }
                order.setCount(orderProductBo.getQuantity());
            } else {
                order.setCount(1L);
            }
            order.setStatus("0");
            if ("1".equals(productVo.getSendAccountType())) {
                order.setAccount(userVo.getOpenId());
            } else {
                order.setAccount(userVo.getMobile());
            }
            order.setExternalProductId(productVo.getExternalProductId());
            order.setOrderCityCode(bo.getAdcode());
            order.setOrderCityName(bo.getCityName());
            order.setPlatformKey(platformVo.getPlatformKey());
            order.setExternalProductSendValue(productVo.getExternalProductSendValue());
            order.setOrderType(productVo.getProductType());
            order.setParentNumber(bo.getParentNumber());
            order.setUsedStartTime(productVo.getUsedStartTime());
            order.setUsedEndTime(productVo.getUsedEndTime());
            order.setUnionPay(productVo.getUnionPay());
            order.setUnionProductId(productVo.getUnionProductId());

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setNumber(order.getNumber());
            orderInfo.setCommodityJson(JsonUtils.toJsonString(productVo));
            if (null != user62VipInfo) {
                orderInfo.setVip62Status(user62VipInfo.getStatus());
                orderInfo.setVip62MemberType(user62VipInfo.getMemberType());
                orderInfo.setVip62BeginTime(user62VipInfo.getBeginTime());
                orderInfo.setVip62EndTime(user62VipInfo.getEndTime());
            }
            //如果是供应商美食订单走这里
            addFoodOrder(productVo, order, userVo, platformVo);
            // 小订单金额，单个商品销售价
            BigDecimal amountSmall = productVo.getSellAmount();
            // 会员优惠金额
            BigDecimal reducedPriceSmall = new BigDecimal("0");
            // 小订单实际优惠金额
            BigDecimal reducedPriceOrder;
            // 62会员
            if (null != user62VipInfo) {
                if ("01".equals(user62VipInfo.getStatus()) || "03".equals(user62VipInfo.getStatus())) {
                    if (null != productVo.getVipUpAmount() && productVo.getVipUpAmount().signum() > 0) {
                        reducedPriceSmall = amount.subtract(productVo.getVipUpAmount());
                    }
                }
            }
            // 权益会员
            if ("1".equals(userVo.getVipUser())) {
                if (null != productVo.getVipAmount() && productVo.getVipAmount().signum() > 0) {
                    reducedPriceSmall = amount.subtract(productVo.getVipAmount());
                }
            }

            amount = amount.add(amountSmall.multiply(new BigDecimal(orderProductBo.getQuantity())));
            // 扣减金额 = （商品售价-会员售价）*商品数量 + （商品售价*商品数量/全部商品总价）*优惠券优惠金额
            reducedPriceOrder = reducedPriceSmall.multiply(new BigDecimal(orderProductBo.getQuantity()));

            reducedPrice = reducedPrice.add(reducedPriceOrder);

            if (cAmount.compareTo(new BigDecimal("0")) > 0) {
                BigDecimal multiply = couponAmount.multiply(amountSmall.divide(cAmount, RoundingMode.DOWN));
                couponShareOrderAmount = couponShareOrderAmount.add(multiply);
                if (orderCount == (bo.getOrderProductBos().size() - 1)) {
                    reducedPriceOrder = reducedPriceOrder.add(couponAmount.subtract(couponShareOrderAmount));
                } else {
                    reducedPriceOrder = reducedPriceOrder.add(multiply);
                }
            }

            try {
                order.setTotalAmount(amountSmall.multiply(new BigDecimal(orderProductBo.getQuantity())));
                order.setReducedPrice(reducedPriceOrder);
                order.setWantAmount(order.getTotalAmount().subtract(order.getReducedPrice()));
                order = insertOrder(order);
                orderInfoMapper.insert(orderInfo);
            } catch (Exception e) {
                // 如果发生异常 回退名额
                callbackOrderCountCache(order.getPlatformKey(), order.getUserId(), order.getProductId(), null, order.getCount());
                log.error("保存订单异常：", e);
                throw new ServiceException("系统繁忙，请稍后重试");
            }
            // 分销处理
            SpringUtils.context().publishEvent(new ShareOrderEvent(bo.getShareUserId(), order.getNumber()));
            orderCount++;
        }

        //计算金额  添加大订单价格
        collectiveOrder.setCount(count);
        collectiveOrder.setTotalAmount(amount);
        collectiveOrder.setReducedPrice(reducedPrice);
        collectiveOrder.setWantAmount(amount.subtract(reducedPrice));
        if (collectiveOrder.getWantAmount().signum() < 1) {
            collectiveOrder.setWantAmount(new BigDecimal("0.01"));
        }
        collectiveOrderMapper.insert(collectiveOrder);
        collectiveOrder = getCollectiveOrder(collectiveOrder.getCollectiveNumber());
        return new CreateOrderResult(collectiveOrder.getCollectiveNumber(), null, "1");

    }

    private void addFoodOrder(ProductVo productVo, Order order, UserVo userVo, PlatformVo platformVo) {
        //美食订单在这里处理
        if ("5".equals(productVo.getProductType())) {
            //先查出美食商品详情
            ProductInfoVo productInfoVo = productInfoService.queryById(productVo.getProductId());
            if (ObjectUtil.isEmpty(productInfoVo)) {
                throw new ServiceException("商品异常");
            }
            OrderFoodInfo orderFoodInfo = new OrderFoodInfo();
            orderFoodInfo.setNumber(order.getNumber());
            //调用美食商城预下单接口获取三方number
            foodCreateOrder(order, orderFoodInfo, userVo.getMobile(), productVo.getExternalProductId(), productInfoVo.getItemId());
            // 保存订单 后续如需改动成缓存订单，需注释
            orderFoodInfo.setItemId(productVo.getExternalProductId());
            orderFoodInfo.setUserName("匿名");
            orderFoodInfoMapper.insert(orderFoodInfo);
        }
        // 联联订单处理
        if ("14".equals(productVo.getProductType())) {
            // 账户id
            String channelId = ysfConfigService.queryValueByKey(platformVo.getPlatformKey(), "LianLian.channelId");
            // 密钥
            String secret = ysfConfigService.queryValueByKey(platformVo.getPlatformKey(), "LianLian.secret");
            // 域名
            String basePath = ysfConfigService.queryValueByKey(platformVo.getPlatformKey(), "LianLian.basePath");
            // 创建订单访问权限地址
            String validToken = ysfConfigService.queryValueByKey(platformVo.getPlatformKey(), "LianLian.validToken");
            //先查商品详情
            ProductInfoVo productInfoVo = productInfoService.queryById(productVo.getProductId());
            // 验证订单创建条件
            JSONObject result = LianLianUtils.getValidToken(channelId, secret, basePath + validToken, order.getNumber().toString(),
                productInfoVo.getItemPrice(), productVo.getExternalProductId(), productInfoVo.getItemId(), userVo.getMobile());
            if (ObjectUtil.isEmpty(result)) {//请求失败
                log.error("联联创建第{}单时失败", order.getNumber());
                throw new ServiceException("该产品库存不足，请购买其他产品");
            } else {
                validToken = result.getString("validToken");
                RedisUtils.setCacheObject(order.getNumber().toString(), validToken, Duration.ofMinutes(2));
            }
        }
        //美食订单在这里处理
        if ("15".equals(productVo.getProductType())) {
            //先查出美食商品详情
            ProductInfoVo productInfoVo = productInfoService.queryById(productVo.getProductId());
            if (ObjectUtil.isEmpty(productInfoVo)) {
                throw new ServiceException("商品异常");
            }
            OrderFoodInfo orderFoodInfo = new OrderFoodInfo();
            orderFoodInfo.setNumber(order.getNumber());
            //调用美食商城预下单接口获取三方number
            ctripFoodCreateOrder(order, orderFoodInfo, userVo.getMobile(), productVo.getExternalProductId());
            // 保存订单 后续如需改动成缓存订单，需注释
            orderFoodInfo.setItemId(productVo.getExternalProductId());
            orderFoodInfo.setUserName("匿名");
            orderFoodInfoMapper.insert(orderFoodInfo);
        }

    }

    /**
     * 支付成功 删除用户未支付订单缓存
     *
     * @param collectiveNumber 订单号
     */
    private void delCacheOrder(Long collectiveNumber) {
        String orderCacheKey = getOrderCacheKey(collectiveNumber);
        Order order = RedisUtils.getCacheObject(orderCacheKey);
        if (null == order) {
            return;
        }
        String userOrderCacheKey = OrderCacheUtils.getUsreOrderOneCacheKey(order.getPlatformKey(), order.getUserId(), order.getProductId());
        RedisUtils.deleteObject(userOrderCacheKey);
        RedisUtils.deleteObject(orderCacheKey);
    }

    /**
     * 请求美食商城接口
     */
    private void foodCreateOrder(Order order, OrderFoodInfo orderFoodInfo, String mobile, String commodityId, String productId) {
        String appId = YSF_FOOD_PROPERTIES.getAppId();
        String rsaPrivateKey = YSF_FOOD_PROPERTIES.getRsaPrivateKey();
        String insertOrderUrl = YSF_FOOD_PROPERTIES.getInsertOrderUrl();
        String orderFood = YsfFoodUtils.createOrder(appId, commodityId, productId, mobile, rsaPrivateKey, insertOrderUrl);
        if (ObjectUtil.isNotEmpty(orderFood)) {
            //请求成功
            JSONObject jsonObject = JSONObject.parseObject(orderFood);
            String number = jsonObject.getString("number");
            if (ObjectUtil.isNotEmpty(number)) {
                //保存三方订单号
                order.setExternalOrderNumber(number);
                orderFoodInfo.setBizOrderId(number);
            } else {
                throw new ServiceException("创建订单失败");
            }

        }
    }

    /**
     * 请求携程预下单接口
     */
    private void ctripFoodCreateOrder(Order order, OrderFoodInfo orderFoodInfo, String mobile, String ctripProductId) {
        String accessToken = CtripUtils.getAccessToken();
        String url = CtripConfig.getUrl() + "?AID=" + CtripConfig.getAid() + "&SID=" + CtripConfig.getSid() + "&ICODE=" + CtripConfig.getCreateOrderCode() + "&Token=" + accessToken;
        JSONObject ctripOrder = CtripUtils.createCtripOrder(order, mobile, CtripConfig.getPartnerType(), ctripProductId, url);
        JSONObject resultJson = ctripOrder.getJSONObject("result");
        int code = resultJson.getIntValue("code");
        if (0 != code) {
            log.error("订单编号：{}，下单失败，失败信息：{}", order.getNumber(), resultJson.getString("message"));
            // 1004 该券不支持售卖
            if (code == 1004) {
                //下架商品
                Product product = new Product();
                product.setProductId(order.getProductId());
                product.setStatus("1");
                productMapper.updateById(product);
            }
            throw new ServiceException("系统繁忙，请稍后重试");
        }
        if (ObjectUtil.isNotEmpty(ctripOrder)) {
            //请求成功
            String orderId = ctripOrder.getString("orderId");
            if (ObjectUtil.isNotEmpty(orderId)) {
                //保存三方订单号
                order.setExternalOrderNumber(orderId);
                orderFoodInfo.setBizOrderId(orderId);
            } else {
                throw new ServiceException("创建订单失败");
            }

        }
    }

    /**
     * 口碑 订单进行处理
     *
     * @param order 订单信息
     */
    private void orderFoodProcessed(OrderVo order) {
        OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(order.getNumber());
        if (ObjectUtil.isNotEmpty(orderFoodInfoVo)) {
            //如果没有电子码
            if (("2".equals(order.getStatus()) || "4".equals(order.getStatus()) || "5".equals(order.getStatus())) && StringUtils.isEmpty(orderFoodInfoVo.getTicketCode()) && order.getOrderType().equals("5")) {
                // 查询口碑订单
                queryFoodOrder(order.getExternalOrderNumber());
                //防止重复加密再查询一遍
                orderFoodInfoVo = orderFoodInfoMapper.selectVoById(order.getNumber());
            }
        }
        order.setOrderFoodInfoVo(orderFoodInfoVo);
    }

    /**
     * 查询美食订单接口
     */
    private void queryFoodOrder(String externalOrderNumber) {
        String appId = YSF_FOOD_PROPERTIES.getAppId();
        String rsaPrivateKey = YSF_FOOD_PROPERTIES.getRsaPrivateKey();
        String queryOrderUrl = YSF_FOOD_PROPERTIES.getQueryOrderUrl();
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getExternalOrderNumber, externalOrderNumber));
        if (ObjectUtil.isEmpty(order)) {
            return;
        }
        OrderPushInfo orderPushInfo = orderPushInfoMapper.selectOne(new LambdaQueryWrapper<OrderPushInfo>().eq(OrderPushInfo::getNumber, order.getNumber()));
        OrderFoodInfo orderFoodInfo = orderFoodInfoMapper.selectById(order.getNumber());
        String orderQuery = YsfFoodUtils.queryOrder(appId, externalOrderNumber, rsaPrivateKey, queryOrderUrl);
        if (ObjectUtil.isNotEmpty(orderQuery)) {
            JSONObject orderObject = JSONObject.parseObject(orderQuery);
            //同步订单状态
            JSONObject orderKbProduct = orderObject.getJSONObject("orderKbProduct");
            order.setExternalOrderNumber(orderKbProduct.getString("number"));
            String ticketCode = orderKbProduct.getString("ticketCode");
            String voucherId = orderKbProduct.getString("voucherId");
            String voucherStatus = orderKbProduct.getString("voucherStatus");
            String effectTime = orderKbProduct.getString("effectTime");
            String expireTime = orderKbProduct.getString("expireTime");
            Integer totalAmount = orderKbProduct.getInteger("totalAmount");
            Integer usedAmount = orderKbProduct.getInteger("usedAmount");
            Integer refundAmount = orderKbProduct.getInteger("refundAmount");
            String orderStatus = orderKbProduct.getString("orderStatus");
            BigDecimal officialPrice = orderKbProduct.getBigDecimal("officialPrice");
            BigDecimal sellingPrice = orderKbProduct.getBigDecimal("sellingPrice");
            if (ObjectUtil.isNotEmpty(ticketCode)) {
                order.setSendStatus("2");
                if (ObjectUtil.isNotEmpty(orderPushInfo)) {
                    orderPushInfo.setStatus("1");
                    orderPushInfoMapper.updateById(orderPushInfo);
                }
            }
            orderFoodInfo.setVoucherId(voucherId);
            orderFoodInfo.setTicketCode(ticketCode);
            //根据票券状态更新订单的核销状态
            if ("EFFECTIVE".equals(voucherStatus)) {
                order.setVerificationStatus("0");
            } else if ("USED".equals(voucherStatus)) {
                order.setVerificationStatus("1");
            } else if ("CANCELED".equals(voucherStatus)) {
                order.setVerificationStatus("2");
            }
            orderFoodInfo.setVoucherStatus(voucherStatus);
            orderFoodInfo.setEffectTime(effectTime);
            orderFoodInfo.setExpireTime(expireTime);
            orderFoodInfo.setTotalAmount(totalAmount);
            orderFoodInfo.setUsedAmount(usedAmount);
            orderFoodInfo.setRefundAmount(refundAmount);
            orderFoodInfo.setOrderStatus(orderStatus);
            orderFoodInfo.setOfficialPrice(officialPrice);
            orderFoodInfo.setSellingPrice(sellingPrice);
            order = updateOrder(order);
            orderFoodInfoMapper.updateById(orderFoodInfo);
        }

    }

    /**
     * 支付美食订单接口
     * *
     */
    private void payFoodOrder(String externalOrderNumber) {
        String appId = YSF_FOOD_PROPERTIES.getAppId();
        String rsaPrivateKey = YSF_FOOD_PROPERTIES.getRsaPrivateKey();
        String payOrderUrl = YSF_FOOD_PROPERTIES.getPayOrderUrl();
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getExternalOrderNumber, externalOrderNumber));
        if (ObjectUtil.isEmpty(order)) {
            return;
        }
        OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(order.getNumber());
        if (ObjectUtil.isEmpty(orderFoodInfoVo)) {
            return;
        }
        //调用美食支付接口
        String s = YsfFoodUtils.payOrder(appId, externalOrderNumber, rsaPrivateKey, payOrderUrl);
        //支付完成后调用查询美食订单接口获取电子凭证
        if (ObjectUtil.isEmpty(s)) {
            queryFoodOrder(externalOrderNumber);
        }
    }

    private void lianlianCreateOrder(Order order, ProductVo productVo, UserVo userVo) {
        String validToken = RedisUtils.getCacheObject(order.getNumber().toString());
        // 账户id
        String channelId = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.channelId");
        // 密钥
        String secret = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.secret");
        // 域名
        String basePath = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.basePath");
        // 创建订单访问权限地址
        String validTokenUrl = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.validToken");
        // 创建订单地址
        String createOrder = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.createOrder");
        if (StringUtils.isNotEmpty(validToken)) {
            //先查商品详情
            ProductInfoVo productInfoVo = productInfoService.queryById(productVo.getProductId());
            // 创建联联订单
            JSONObject lianLianOrder = LianLianUtils.createOrder(channelId, secret, basePath + createOrder, order.getNumber().toString(), productInfoVo.getItemPrice(), validToken, productVo.getExternalProductId(), productInfoVo.getItemId(), userVo.getMobile());
            if (ObjectUtil.isNotEmpty(lianLianOrder)) {
                String channelOrderId = lianLianOrder.getString("channelOrderId");
                order.setExternalOrderNumber(channelOrderId);
                order.setSendStatus("2");
                lianLianOrderCode(order);
            } else {
                order.setSendStatus("3");
                lianLianOrderCode(order);
            }
        } else {
            //先查商品详情
            ProductInfoVo productInfoVo = productInfoService.queryById(productVo.getProductId());
            // 验证订单创建条件
            JSONObject result = LianLianUtils.getValidToken(channelId, secret, basePath + validTokenUrl, order.getNumber().toString(),
                productInfoVo.getItemPrice(), productVo.getExternalProductId(), productInfoVo.getItemId(), userVo.getMobile());
            if (ObjectUtil.isEmpty(result)) {
                // 请求失败
                log.error("联联创建第{}单时失败", order.getNumber());
                throw new ServiceException("该产品库存不足，请购买其他产品");
            } else {
                validToken = result.getString("validToken");
            }
            if (StringUtils.isNotEmpty(validToken)) {
                // 创建联联订单
                JSONObject lianLianOrder = LianLianUtils.createOrder(channelId, secret, basePath + createOrder, order.getNumber().toString(),
                    productInfoVo.getItemPrice(), validToken, productVo.getExternalProductId(), productInfoVo.getItemId(), userVo.getMobile());
                if (ObjectUtil.isNotEmpty(lianLianOrder)) {
                    String channelOrderId = lianLianOrder.getString("channelOrderId");
                    order.setExternalOrderNumber(channelOrderId);
                    order.setSendStatus("2");
                    lianLianOrderCode(order);
                } else {
                    order.setStatus("3");
                    lianLianOrderCode(order);
                }
            }
        }
        baseMapper.updateById(order);
    }

    /**
     * 联联订单发码记录
     */
    private void lianLianOrderCode(Order order) {
        // 账户id
        String channelId = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.channelId");
        // 密钥
        String secret = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.secret");
        // 域名
        String basePath = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.basePath");
        // 订单详情
        String orderDetails = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.orderDetails");
        ProductVo productVo = productMapper.selectVoById(order.getProductId());
        JSONObject details = LianLianUtils.getOrderDetails(channelId, secret, basePath + orderDetails, order.getNumber().toString(), order.getExternalOrderNumber());
        if (ObjectUtil.isNotEmpty(details)) {
            order.setExternalOrderNumber(details.getString("channelOrderId"));
            order.setSendStatus("2");//发放状态改为发放成功
            //创建或更新联联订单信息
            JSONArray orderList = details.getJSONArray("orderDetailList");
            List<OrderFoodInfo> orderFoodInfoList = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(orderList)) {
                for (int i = 0; i < orderList.size(); i++) {
                    JSONObject orderItem = orderList.getJSONObject(i);
                    OrderFoodInfo orderFoodInfo = new OrderFoodInfo();
                    orderFoodInfo.setNumber(order.getNumber());
                    // 第三方订单号
                    orderFoodInfo.setBizOrderId(orderItem.getString("orderId"));
                    String status = orderItem.getString("status");
                    if (StringUtils.isNotEmpty(status)) {
                        // 联联订单状态 110:支付中 111:已支付 210:已预约 310:已核销 311:核销异常
                        // 410:退款中 411:退款不退佣金 412:已退款 510:赔付中 511:赔付成功 610:已过期
                        orderFoodInfo.setOrderStatus(status);
                        if (status.equals("110") || status.equals("111") || status.equals("210")) {
                            orderFoodInfo.setVoucherStatus("EFFECTIVE");
                            order.setVerificationStatus("0");
                        } else if (status.equals("310")) {
                            orderFoodInfo.setVoucherStatus("USED");
                            order.setVerificationStatus("1");
                        } else {
                            orderFoodInfo.setRefundAmount(1);
                            orderFoodInfo.setVoucherStatus("CANCELED");
                            order.setVerificationStatus("2");
                        }
                    }
                    orderFoodInfo.setTotalAmount(1);
                    orderFoodInfo.setUsedAmount(0);
                    String code = orderItem.getString("code");
                    String qrCodeImgUrl = orderItem.getString("qrCodeImgUrl");
                    if (StringUtils.isNotEmpty(code) || StringUtils.isNotEmpty(qrCodeImgUrl)) {
                        orderFoodInfo.setTicketCode(code);
                        orderFoodInfo.setTicketCodeUrl(qrCodeImgUrl);
                    } else {
                        order.setSendStatus("1");
                    }
                    if (ObjectUtil.isNotEmpty(productVo)) {
                        if (ObjectUtil.isNotEmpty(productVo.getShowEndDate())) {
                            orderFoodInfo.setExpireTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, productVo.getShowEndDate()));
                        }
                    }
                    orderFoodInfo.setEffectTime(DateUtils.getTime());
                    orderFoodInfoList.add(orderFoodInfo);
                }
            }
            OrderPushInfo orderPushInfo = orderPushInfoMapper.selectOne(new LambdaQueryWrapper<OrderPushInfo>().eq(OrderPushInfo::getNumber, order.getNumber()));
            if (ObjectUtil.isNotEmpty(orderPushInfo)) {
                if (order.getSendStatus().equals("2")) {
                    orderPushInfo.setStatus("1");
                } else if (order.getSendStatus().equals("3")) {
                    orderPushInfo.setStatus("2");
                }
                orderPushInfoMapper.updateById(orderPushInfo);
            }
            orderFoodInfoMapper.insertOrUpdateBatch(orderFoodInfoList);
            baseMapper.updateById(order);
        }
    }

    /**
     * 订单回调通知处理
     *
     * @param number 订单接口
     */
    @Override
    public void lianOrderBack(JSONObject param, Integer number) {
        String security = ysfConfigService.queryValueByKeys("LianLian.secret");
        String decryptedData = LianLianUtils.aesDecryptBack(param, security);
        log.info("联联订单通知接收参数解密后：{}", decryptedData);
        switch (number) {
            // 订单自动发码回调
            case 1:
                if (StringUtils.isNotEmpty(decryptedData)) {
                    JSONObject jsonObject = JSONObject.parseObject(decryptedData);
                    if (ObjectUtil.isNotEmpty(jsonObject)) {
                        String externalOrderNumber = jsonObject.getString("channelOrderId");
                        OrderVo orderVo = this.queryByExternalOrderNumber(externalOrderNumber);
                        Order order = BeanCopyUtils.copy(orderVo, Order.class);
                        //更新订单 联联订单自动发码回调
                        this.lianLianOrderCode(order);
                    }
                }
                break;
            case 2:
                // 订单退款回调
                if (StringUtils.isNotEmpty(decryptedData)) {
                    LianLianParam.OrderReturnParam orderReturnParam =
                        JSONObject.parseObject(decryptedData, LianLianParam.OrderReturnParam.class);
                    this.lianOrderReturn(orderReturnParam);
                }
                break;
            case 3:
                // 订单核销
                if (StringUtils.isNotEmpty(decryptedData)) {
                    LianLianParam.OrderCheckParam orderCheckParam =
                        JSONObject.parseObject(decryptedData, LianLianParam.OrderCheckParam.class);
                    this.lianOrderCall(orderCheckParam);
                }
                break;
        }
    }

    /**
     * 联联订单退款通知
     */

    private void lianOrderReturn(LianLianParam.OrderReturnParam orderReturnParam) {
        Order order = baseMapper.selectById(orderReturnParam.getThirdOrderId());
        OrderFoodInfo orderFoodInfo = orderFoodInfoMapper.selectById(orderReturnParam.getThirdOrderId());
        order.setStatus("5");
        order.setVerificationStatus("2");
        orderFoodInfo.setVoucherStatus("CANCELED");
        orderFoodInfo.setOrderStatus("412");
        orderFoodInfoMapper.updateById(orderFoodInfo);
        baseMapper.updateById(order);
    }

    /**
     * 联联订单核销通知
     */
    private void lianOrderCall(LianLianParam.OrderCheckParam orderCheckParam) {
        //Order order = baseMapper.selectById(orderCheckParam.getThirdOrderId());
        //baseMapper.updateById(order);

        OrderFoodInfo orderFoodInfo = orderFoodInfoMapper.selectById(orderCheckParam.getThirdOrderId());
        Order order = baseMapper.selectById(orderCheckParam.getThirdOrderId());
        orderFoodInfo.setVoucherStatus("USED");
        orderFoodInfo.setOrderStatus("310");
        //订单表核销状态更改为已失效
        order.setVerificationStatus("1");
        baseMapper.updateById(order);
        orderFoodInfoMapper.updateById(orderFoodInfo);
        SpringUtils.context().publishEvent(new ShareOrderEvent(null, order.getNumber()));
    }

    /**
     * 支付携程订单接口
     * *
     */
    private void payCtripFoodOrder(String externalOrderNumber) {
        String accessToken = CtripUtils.getAccessToken();
        String url = CtripConfig.getUrl() + "?AID=" + CtripConfig.getAid() + "&SID=" + CtripConfig.getSid() + "&ICODE=" + CtripConfig.getConfirmOrderCode() + "&Token=" + accessToken;
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getExternalOrderNumber, externalOrderNumber));
        if (ObjectUtil.isEmpty(order)) {
            return;
        }
        OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(order.getNumber());
        if (ObjectUtil.isEmpty(orderFoodInfoVo)) {
            return;
        }
        //调用美食支付接口
        JSONObject jsonObject = CtripUtils.payCtripOrder(externalOrderNumber, CtripConfig.getPartnerType(), url);
        //支付完成后接入核销数据
        if (ObjectUtil.isNotEmpty(jsonObject)) {
            JSONObject resultJson = jsonObject.getJSONObject("result");
            int code = resultJson.getIntValue("code");
            if (0 != code) {
                //修改订单可补领状态
                String message = resultJson.getString("message");
                log.error("订单编号：{}，确认订单失败，失败原因：{}", order.getNumber(), message);
                throw new ServiceException("系统繁忙，请稍后重试！失败原因：" + message);
            }
            OrderFoodInfo orderFoodInfo = new OrderFoodInfo();
            orderFoodInfo.setNumber(order.getNumber());
            JSONArray codes = jsonObject.getJSONArray("codes");
            if (!org.springframework.util.CollectionUtils.isEmpty(codes)) {
                orderFoodInfo.setTicketCode(codes.getJSONObject(0).getString("code"));
                orderFoodInfo.setVoucherId(codes.getJSONObject(0).getString("couponCodeId"));
                orderFoodInfo.setVoucherStatus("EFFECTIVE");
            }
            String reservationUrl = jsonObject.getString("reservationUrl");
            if (ObjectUtil.isNotEmpty(reservationUrl)) {
                orderFoodInfo.setProductName(reservationUrl);
            }
            if (ObjectUtil.isNotEmpty(orderFoodInfo.getTicketCode()) || ObjectUtil.isNotEmpty(orderFoodInfo.getVoucherId()) || StringUtils.isNotEmpty(orderFoodInfo.getProductName())) {
                orderFoodInfoMapper.updateById(orderFoodInfo);
            }

        }
    }

    /**
     * 缓存用户未支付订单
     *
     * @param order 订单信息
     */
    private void cacheOrder(Order order) {
        String orderCacheKey = getOrderCacheKey(order.getNumber());
        long datePoorMinutes = 30;
        if (null != order.getExpireDate()) {
            datePoorMinutes = DateUtils.getDatePoorMinutes(order.getExpireDate(), new Date());
            datePoorMinutes = datePoorMinutes + 20;
        }
        Duration duration = Duration.ofMinutes(datePoorMinutes);
        RedisUtils.setCacheObject(orderCacheKey, order, duration);
        String userOrderCacheKey = OrderCacheUtils.getUsreOrderOneCacheKey(order.getPlatformKey(), order.getUserId(), order.getProductId());
        RedisUtils.setCacheObject(userOrderCacheKey, orderCacheKey, duration);
    }

    /**
     * 获取订单缓存redis key
     *
     * @param number 订单号
     * @return 结果
     */
    private String getOrderCacheKey(Long number) {
        return "orders:" + number;
    }

    /**
     * 设置商品购买数量缓存
     *
     * @param platformKey 平台标识
     * @param userId      用户ID
     * @param productId   产品ID
     */
    private void setOrderCountCache(Long platformKey, Long userId, Long productId, Date cacheTime, Long count) {
        if (null == count || count < 1) {
            count = 1L;
        }
        Duration duration = null;
        if (null != cacheTime) {
            long datePoorHour = DateUtils.getDatePoorDay(cacheTime, new Date());
            if (datePoorHour > 0) {
                duration = Duration.ofDays(datePoorHour + 7);
            }
        }
        DateType[] values = DateType.values();
        for (DateType value : values) {
            String productCacheKey = ProductUtils.countByProductIdRedisKey(platformKey, productId, value);
            String userCacheKey = ProductUtils.countByUserIdAndProductIdRedisKey(platformKey, userId, productId, value);
            for (int i = 0; i < count; i++) {
                // 循环递增
                RedisUtils.incrAtomicValue(productCacheKey);
                RedisUtils.incrAtomicValue(userCacheKey);
            }
            duration = ZlyyhUtils.getDurationByDateTypeAndDefault(value, duration);
            // 设置失效时间
            RedisUtils.expire(productCacheKey, duration);
            RedisUtils.expire(userCacheKey, duration);
        }
    }

    /**
     * 订单取消 回退用户购买名额
     *
     * @param platformKey 平台标识
     * @param userId      用户ID
     * @param productId   产品ID
     */
    private void callbackOrderCountCache(Long platformKey, Long userId, Long productId, Date createTime, Long count) {
        if (null == count || count < 1) {
            count = 1L;
        }
        TimeInterval timer = DateUtil.timer();
        DateType[] values = DateType.values();
        for (DateType value : values) {
            String productCacheKey = ProductUtils.countByProductIdRedisKey(platformKey, productId, value);
            if (StringUtils.isBlank(productCacheKey)) {
                // 如果不存在，快速失败，无需再循环
                return;
            }
            if (null != createTime) {
                // 检查是否当天
                if (value == DateType.DAY) {
                    String formatCreateTime = DateUtil.format(createTime, DatePattern.NORM_DATE_PATTERN);
                    String formatNow = DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN);
                    if (!formatCreateTime.equals(formatNow)) {
                        // 不是当天跳过
                        continue;
                    }
                }
                // 检查是否本周
                if (value == DateType.WEEK) {
                    int formatCreateTime = DateUtil.weekOfYear(createTime);
                    int formatNow = DateUtil.weekOfYear(new Date());
                    if (formatCreateTime != formatNow) {
                        // 不是本周跳过
                        continue;
                    }
                }
                // 检查是否当月
                if (value == DateType.MONTH) {
                    String formatCreateTime = DateUtil.format(createTime, DatePattern.NORM_MONTH_PATTERN);
                    String formatNow = DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN);
                    if (!formatCreateTime.equals(formatNow)) {
                        // 不是当月跳过
                        continue;
                    }
                }
            }
            String userCacheKey = ProductUtils.countByUserIdAndProductIdRedisKey(platformKey, userId, productId, value);
            for (int i = 0; i < count; i++) {
                RedisUtils.decrAtomicValue(productCacheKey);
                RedisUtils.decrAtomicValue(userCacheKey);
            }
        }
        log.info("用户：{}，回退名额耗时：{}毫秒", userId, timer.interval());
    }

    /**
     * 查询订单
     */
    @Override
    public OrderVo queryById(Long number) {
        OrderVo orderVo = baseMapper.selectVoById(number);
        try {
            if ("2".equals(orderVo.getStatus()) && "1".equals(orderVo.getSendStatus()) && null != orderVo.getPayTime() && DateUtils.getDatePoorMinutes(new Date(), orderVo.getPayTime()) > 30) {
                queryOrderSendStatus(orderVo.getPushNumber());
            }
        } catch (Exception e) {
            log.info("查询订单发放状态异常：", e);
        }
        //订单为美食订单加上info
        if ("1".equals(orderVo.getOrderType()) || "5".equals(orderVo.getOrderType())) {
            //调用接口查询美食订单
            orderFoodProcessed(orderVo);
        } else if ("7".equals(orderVo.getOrderType())) {
            if ("2".equals(orderVo.getStatus())) {
                List<OrderCardVo> orderCardVos = orderCardService.queryListByNumber(number);
                orderVo.setOrderCardVos(orderCardVos);
            }
        } else if ("11".equals(orderVo.getOrderType()) || "12".equals(orderVo.getOrderType())) {
            List<OrderUnionSendVo> orderUnionSendVos = orderUnionSendService.queryListByNumber(number);
            orderVo.setOrderUnionSendVos(orderUnionSendVos);
        } else if ("14".equals(orderVo.getOrderType())) {
            // 联联
            OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(orderVo.getNumber());
            if (null != orderFoodInfoVo && StringUtils.isEmpty(orderFoodInfoVo.getTicketCode())) {
                Order order = BeanCopyUtils.copy(orderVo, Order.class);
                if (ObjectUtil.isNotEmpty(order)) {
                    lianLianOrderCode(order);
                    orderFoodInfoVo = orderFoodInfoMapper.selectVoById(orderVo.getNumber());
                }
            }
            orderVo.setOrderFoodInfoVo(orderFoodInfoVo);
        } else if ("15".equals(orderVo.getOrderType())) {
            // 携程商品
            OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(orderVo.getNumber());
            orderVo.setOrderFoodInfoVo(orderFoodInfoVo);
        }
        return orderVo;
    }

    /**
     * 查询订单详情
     *
     * @param number 订单number
     * @return 用户信息
     */
    public OrderVo queryBaseOrderById(Long number) {
        return baseMapper.selectVoById(number);
    }

    /**
     * 查询订单列表
     */
    @Override
    public TableDataInfo<OrderVo> queryPageList(OrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Order> lqw = buildQueryWrapper(bo);
        Page<OrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 订单支付未支付数量
     */
    @Override
    public long queryUserOrderCount(Long userId) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getUserId, userId);
        lqw.in(Order::getStatus, "0", "1");
        return baseMapper.selectCount(lqw);
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Long userId, Boolean isValid) {
        // 查询订单
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getUserId, userId);
        lqw.in(Order::getNumber, ids);
        List<OrderVo> orderVos = baseMapper.selectVoList(lqw);
        if (ObjectUtil.isEmpty(orderVos)) {
            return false;
        }
        for (OrderVo orderVo : orderVos) {
            if (!"3".equals(orderVo.getStatus())) {
                throw new ServiceException("当前订单不可删除!");
            }
        }
        int delete = baseMapper.delete(lqw);
        orderTicketMapper.deleteBatchIds(ids);
        return delete > 0;
    }

    @Override
    public void cancel(Long collectiveNumber, Long userId) {
        CollectiveOrder collectiveOrder = getCollectiveOrder(collectiveNumber);
        if (null == collectiveOrder || !collectiveOrder.getUserId().equals(userId)) {
            throw new ServiceException("登录超时,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        List<Order> orders = baseMapper.selectList(new LambdaQueryWrapper<Order>().eq(Order::getCollectiveNumber, collectiveOrder.getCollectiveNumber()));
        if (ObjectUtil.isEmpty(orders)) {
            throw new ServiceException("订单不存在");
        }

        if ("0".equals(collectiveOrder.getStatus()) || "1".equals(collectiveOrder.getStatus())) {
            boolean b = updateOrderClose(collectiveOrder, orders);
            if (!b) {
                throw new ServiceException("订单不可取消！");
            }
        } else {
            throw new ServiceException("订单不可取消！");
        }
    }

    /**
     *订单自动退款
     * *
     */
    @Async
    @Override
    public void autoRefundOrder(){
        //通过商品自动退款字段 以及时间等条件查询可以自动退款的订单
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Order::getAutoRefund, "1");
        lqw.eq(Order::getStatus, "2");
        //自动退款目前只支持银联票券的退款  其他暂时不考虑
        lqw.eq(Order::getOrderType,"18");
        lqw.in(Order::getVerificationStatus,"0", "2");
        lqw.le(Order::getUsedEndTime, new Date());
        Long orderCount = baseMapper.selectCount(lqw);
        //分页查询
        int pageIndex = 1;
        int pageSize = 50;
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageSize(pageSize);
        //循环退款
        while (true){
            pageQuery.setPageNum(pageIndex);
            pageQuery.setPageSize(pageSize);
            Page<OrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
            List<OrderVo> orderVos = result.getRecords();
            if (CollectionUtils.isNotEmpty(orderVos)){
                //进行退款
                for (OrderVo orderVo : orderVos) {
                    try {
                        orderAutoRefund(orderVo);
                    }catch (Exception e){
                        log.error("订单自动退款失败，订单号：{}",orderVo.getNumber());
                    }

                }
            }
            int sum = pageIndex * pageSize;
            if (sum >= orderCount) {
                break;
            }
            pageIndex++;
        }

    }

    /**
     * @param orderVo 订单
     *
     */
    private void orderAutoRefund(OrderVo orderVo){
        Order order = baseMapper.selectById(orderVo.getNumber());
        //查询大订单
        CollectiveOrder collectiveOrder = getCollectiveOrder(orderVo.getCollectiveNumber());
        Refund refund = new Refund();
        refund.setNumber(orderVo.getNumber());
        refund.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        //退款申请人
        refund.setRefundApplicant(orderVo.getUserId().toString());
        refund.setRefundAmount(orderVo.getOutAmount());
        refund.setRefundRemark("银联票券过期自动退款");
        String orderType = orderVo.getOrderType();
        PermissionUtils.setDeptIdAndUserId(refund, order.getSysDeptId(), order.getSysUserId());
        if (!orderType.equals("18")){
            return;
        }
        refund.setStatus("1");
        refund.setRefundReviewer("系统");
        refundMapper.insert(refund);
        MerchantVo merchantVo = null;
        if (!"2".equals(orderVo.getPickupMethod())) {
            merchantVo = merchantService.queryById(orderVo.getPayMerchant());
            if (ObjectUtil.isEmpty(merchantVo)) {
                throw new ServiceException("商户不存在，请联系客服处理");
            }
        }
        OrderBackTrans orderBackTrans = new OrderBackTrans();
        orderBackTrans.setRefund(orderVo.getOutAmount());
        orderBackTrans.setThNumber(IdUtils.getSnowflakeNextIdStr("T"));
        orderBackTrans.setOrderBackTransState("0");
        orderBackTrans.setNumber(orderVo.getNumber());
        order.setStatus("4");
        // 退款检查
        try {
            checkOrderStatus(orderBackTrans, order.getOutAmount(), order.getStatus());
        } catch (Exception e) {
            // 有可能订单还没更新，休眠1秒
            ThreadUtil.sleep(3000);
            order = baseMapper.selectById(orderBackTrans.getNumber());
            try {
                checkOrderStatus(orderBackTrans, order.getOutAmount(), order.getStatus());
            } catch (Exception ex) {
                checkOrderStatus(orderBackTrans, order.getWantAmount(), order.getStatus());
            }
        }
        //1.付费领取
        if ("1".equals(order.getPickupMethod())) {
            if (ObjectUtil.isNotEmpty(merchantVo) && "0".equals(merchantVo.getMerchantType())) {
                // 云闪付退款
                ysfRefund(orderBackTrans, order, merchantVo);
            } else if (ObjectUtil.isNotEmpty(merchantVo) && "1".equals(merchantVo.getMerchantType())) {
                // 微信退款
                wxRefund(orderBackTrans, order, merchantVo);
            } else {
                throw new ServiceException("商户号错误");
            }
        } else if ("2".equals(order.getPickupMethod())) {
            //2.积点兑换
            UserVo userVo = userService.queryById(order.getUserId(), PlatformEnumd.MP_YSF.getChannel());
            if (ObjectUtil.isEmpty(userVo)) {
                throw new ServiceException("用户不存在，请联系客服处理");
            }
            R<Void> result = YsfUtils.memberPointAcquire(orderVo.getNumber(), Integer.toUnsignedLong(orderBackTrans.getRefund().intValue()), "1", orderVo.getProductName() + "退款", userVo.getOpenId(), "1", orderVo.getPlatformKey());
            if (result.getCode() == 200) {
                orderBackTrans.setPickupMethod("2");
                orderBackTrans.setSuccessTime(DateUtils.getNowDate());
                orderBackTrans.setOrderBackTransState("2");
                order.setStatus("5");
                //全退完了
                collectiveOrder.setStatus("5");
                collectiveOrder.setCancelAmount(collectiveOrder.getCancelAmount().add(order.getWantAmount()));
                collectiveOrder.setCancelStatus("1");
            } else {
                orderBackTrans.setOrderBackTransState("1");
                order.setStatus("6");
                //退款失败
                collectiveOrder.setStatus("6");
                collectiveOrder.setCancelAmount(collectiveOrder.getCancelAmount().add(order.getWantAmount()));
                collectiveOrder.setCancelStatus("2");
            }
        }
        collectiveOrderMapper.updateById(collectiveOrder);
        order = updateOrder(order);

        PermissionUtils.setDeptIdAndUserId(orderBackTrans, order.getSysDeptId(), order.getSysUserId());
        orderBackTransMapper.insert(orderBackTrans);

    }


    /**
     * 校验订单退款状态
     *
     * @param bo        退款信息
     * @param outAmount 退款金额
     * @param status    订单状态
     */
    private void checkOrderStatus(OrderBackTrans bo, BigDecimal outAmount, String status) {
        if (bo.getRefund().compareTo(outAmount) > 0) {
            log.info("订单退款金额：{}，订单实际支付金额：{}", bo.getRefund(), outAmount);
            throw new ServiceException("退款金额不能超出订单金额");
        }
    }

    /**
     * 微信订单退款
     *
     * @param orderBackTrans         退款信息
     * @param order        订单信息
     * @param merchantVo 商户号信息
     */
    private void wxRefund(OrderBackTrans orderBackTrans, Order order, MerchantVo merchantVo) {
        String refundCallbackUrl = merchantVo.getRefundCallbackUrl();
        if (!refundCallbackUrl.contains(merchantVo.getId().toString())) {
            refundCallbackUrl = refundCallbackUrl + "/" + merchantVo.getId();
        }
        BigDecimal outAmount = order.getOutAmount();
        Integer amountTotal = BigDecimalUtils.toMinute(outAmount);
        if (amountTotal < 1) {
            outAmount = order.getWantAmount();
            amountTotal = BigDecimalUtils.toMinute(outAmount);
        }
        if (amountTotal < 1) {
            throw new ServiceException("退款金额错误");
        }
        String s;
        try {
            Long oldNumber = order.getCollectiveNumber();
            if (null == oldNumber) {
                oldNumber = orderBackTrans.getNumber();
            }
            s = WxUtils.wxRefund(oldNumber.toString(), orderBackTrans.getThNumber(), wxProperties.getRefundUrl(), merchantVo.getMerchantNo(), null, BigDecimalUtils.toMinute(orderBackTrans.getRefund()), amountTotal, refundCallbackUrl, merchantVo.getCertPath(), merchantVo.getMerchantKey(), merchantVo.getApiKey());
        } catch (Exception e) {
            log.error("微信退款异常：", e);
            throw new ServiceException("退款异常");
        }
        if (StringUtils.isBlank(s)) {
            throw new ServiceException("请求失败，请联系管理员处理");
        }
        log.info("订单：{}，微信退款返回数据：{}", orderBackTrans.getNumber(), s);
        Map<String, Object> result = JsonUtils.parseMap(s);
        if (null == result) {
            throw new ServiceException("请求失败，请联系管理员处理");
        }
        // 微信退款单号
        String refund_id = (String) result.get("refund_id");
        // 退款成功时间
        String success_time = (String) result.get("success_time");
        // 退款受理时间
        String create_time = (String) result.get("create_time");
        // 退款状态
        // SUCCESS：退款成功
        // CLOSED：退款关闭
        // PROCESSING：退款处理中
        // ABNORMAL：退款异常
        String status = (String) result.get("status");
        // 优惠退款信息
        Object promotion_detail = result.get("promotion_detail");
        // 金额信息
        Map<String, Object> map = BeanUtil.beanToMap(result.get("amount"));
        // 新增退款订单信息
        orderBackTrans.setRefundId(refund_id);
        if (("SUCCESS").equals(status)) {
            // 退款成功
            orderBackTrans.setPickupMethod("1");
            orderBackTrans.setOrderBackTransState("2");
            orderBackTrans.setSuccessTime(DateUtils.getNowDate());
            order.setStatus("5");
        } else if (!("PROCESSING").equals(status)) {
            orderBackTrans.setOrderBackTransState("1");
            order.setStatus("6");
        }
    }

    /**
     * 云闪付订单退款
     ** @param merchantVo 商户号信息
     */
    private void ysfRefund(OrderBackTrans orderBackTrans, Order order, MerchantVo merchantVo) {
        OrderInfoVo orderInfoVo = orderInfoMapper.selectVoById(orderBackTrans.getNumber());
        Map<String, String> result = PayUtils.backTransReq(orderInfoVo.getQueryId(), BigDecimalUtils.toMinute(orderBackTrans.getRefund()).toString(), orderBackTrans.getThNumber(), merchantVo.getRefundCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), orderInfoVo.getIssAddnData());
        if (null == result) {
            throw new ServiceException("请求失败，请联系客服处理");
        }
        String respCode = result.get("respCode");
        String queryId = result.get("queryId");
        String txnTime = result.get("txnTime");
        String origQryId = result.get("origQryId");
        if (("00").equals(respCode)) {
            orderBackTrans.setQueryId(queryId);
            orderBackTrans.setOrigQryId(origQryId);
            orderBackTrans.setTxnTime(txnTime);
            orderBackTrans.setOrderBackTransState("2");
            orderBackTrans.setPickupMethod("1");
            orderBackTrans.setSuccessTime(DateUtils.getNowDate());
            order.setStatus("5");
        } else if (!("03").equals(respCode) && !("04").equals(respCode) && !("05").equals(respCode)) {
            LogUtil.writeLog("【" + orderBackTrans.getNumber() + "】退款失败，应答信息respCode=" + respCode + ",respMsg=" + result.get("respMsg"));
            //其他应答码为失败请排查原因
            orderBackTrans.setOrderBackTransState("1");
            order.setStatus("6");
        }
    }


    @Override
    public void orderRefund(Long number, Long userId) {
        OrderVo orderVo = baseMapper.selectVoById(number);
        Order order = baseMapper.selectById(orderVo.getNumber());
        if (!orderVo.getUserId().equals(userId)) {
            throw new ServiceException("登录超时,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        //先查该订单是否支持用户侧退款
        if (ObjectUtil.isEmpty(orderVo.getCusRefund()) || !"1".equals(orderVo.getCusRefund())) {
            throw new ServiceException("该订单暂不支持退款");
        }
        if (!"2".equals(orderVo.getStatus())) {
            throw new ServiceException("订单不可退，如有疑问，请联系客服处理");
        }
        //查询大订单
        CollectiveOrder collectiveOrder = getCollectiveOrder(order.getCollectiveNumber());
        Refund refund = new Refund();
        refund.setNumber(orderVo.getNumber());
        refund.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        //退款申请人
        refund.setRefundApplicant(userId.toString());
        refund.setRefundAmount(orderVo.getOutAmount());
        refund.setRefundRemark("用户申请退款");
        //审核中
        refund.setStatus("0");
        String orderType = orderVo.getOrderType();
        PermissionUtils.setDeptIdAndUserId(refund, order.getSysDeptId(), order.getSysUserId());
        //根据订单类型进行退款
        if ("5".equals(orderType)) {
            //如果是美食订单 先查询订单
            queryFoodOrder(orderVo.getExternalOrderNumber());
            OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(orderVo.getNumber());
            if (ObjectUtil.isNotEmpty(orderFoodInfoVo.getVoucherStatus()) && !orderFoodInfoVo.getVoucherStatus().equals("EFFECTIVE")) {
                throw new ServiceException("该订单无法申请退款");
            }
            //大订单跟小订单状态一致
            collectiveOrder.setStatus("4");
            collectiveOrder.setCancelStatus("0");
            collectiveOrderMapper.updateById(collectiveOrder);
            order.setCancelStatus("0");
            order.setStatus("4");
            order = updateOrder(order);
            //如果电子券为未使用状态 在这里先走退款接口
            if (ObjectUtil.isNotEmpty(orderFoodInfoVo.getVoucherStatus()) && orderFoodInfoVo.getVoucherStatus().equals("EFFECTIVE")) {
                String appId = YSF_FOOD_PROPERTIES.getAppId();
                String rsaPrivateKey = YSF_FOOD_PROPERTIES.getRsaPrivateKey();
                String refundUrl = YSF_FOOD_PROPERTIES.getRefundUrl();
                //请求美食退款订单接口
                if ("1".equals(orderVo.getCancelStatus())) {
                    throw new ServiceException("退款已提交,不可重复申请");
                }
                YsfFoodUtils.cancelOrder(appId, orderVo.getExternalOrderNumber(), rsaPrivateKey, refundUrl);
            }
            refundMapper.insert(refund);
            return;
        } else if (orderType.equals("14")) {
            // 联联订单，先查询再处理
            OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(orderVo.getNumber());
            if (ObjectUtil.isNotEmpty(orderFoodInfoVo.getVoucherStatus()) && !orderFoodInfoVo.getVoucherStatus().equals("EFFECTIVE")) {
                throw new ServiceException("该订单无法申请退款");
            }
            //如果电子券为未使用状态 走退款接口
            if (ObjectUtil.isNotEmpty(orderFoodInfoVo.getVoucherStatus()) && orderFoodInfoVo.getVoucherStatus().equals("EFFECTIVE")) {
                //请求美食退款订单接口
                if ("1".equals(orderVo.getCancelStatus())) {
                    throw new ServiceException("退款已提交,不可重复申请");
                }
                // 账户id
                String channelId = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.channelId");
                // 密钥
                String secret = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.secret");
                // 域名
                String basePath = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.basePath");
                // 订单退款地址
                String lianRefund = ysfConfigService.queryValueByKey(order.getPlatformKey(), "LianLian.refund");
                JSONObject refundJson = LianLianUtils.refund(channelId, secret, basePath + lianRefund, order.getExternalOrderNumber(), orderFoodInfoVo.getBizOrderId());
                if (ObjectUtil.isNotEmpty(refundJson)) {
                    //大订单跟小订单状态一致
                    collectiveOrder.setStatus("4");
                    collectiveOrder.setCancelStatus("0");
                    collectiveOrderMapper.updateById(collectiveOrder);
                    order.setCancelStatus("0");
                    order.setStatus("4");
                    baseMapper.updateById(order);
                    refundMapper.insert(refund);
                    SpringUtils.context().publishEvent(new ShareOrderEvent(null, order.getNumber()));
                }
            }
            return;
        } else if (orderType.equals("15")) {
            //如果是美食订单 先查询订单
            OrderFoodInfoVo orderFoodInfoVo = orderFoodInfoMapper.selectVoById(orderVo.getNumber());
            if (ObjectUtil.isNotEmpty(orderFoodInfoVo.getVoucherStatus()) && !orderFoodInfoVo.getVoucherStatus().equals("EFFECTIVE")) {
                throw new ServiceException("该订单无法申请退款");
            }
            //大订单跟小订单状态一致
            collectiveOrder.setStatus("4");
            collectiveOrder.setCancelStatus("0");
            collectiveOrderMapper.updateById(collectiveOrder);
            order.setCancelStatus("0");
            order.setStatus("4");
            baseMapper.updateById(order);
            SpringUtils.context().publishEvent(new ShareOrderEvent(null, order.getNumber()));
            //如果电子券为未使用状态 在这里先走退款接口
            if (ObjectUtil.isNotEmpty(orderFoodInfoVo.getVoucherStatus()) && orderFoodInfoVo.getVoucherStatus().equals("EFFECTIVE")) {
                String accessToken = CtripUtils.getAccessToken();
                String refundUrl = CtripConfig.getUrl() + "?AID=" + CtripConfig.getAid() + "&SID=" + CtripConfig.getSid() +
                    "&ICODE=" + CtripConfig.getCancelOrderCode() + "&Token=" + accessToken;
                //请求美食退款订单接口
                if ("1".equals(orderVo.getCancelStatus())) {
                    throw new ServiceException("退款已提交,不可重复申请");
                }
                CtripUtils.cancelOrder(order.getExternalOrderNumber(), CtripConfig.getPartnerType(), refundUrl);
            }
            refundMapper.insert(refund);
            return;
        } else if ("7".equals(orderType)) {
            //如果是电子券卡密订单 等待同意
            refundMapper.insert(refund);
            //大订单跟小订单状态一致
            collectiveOrder.setStatus("4");
            collectiveOrder.setCancelStatus("0");
            collectiveOrderMapper.updateById(collectiveOrder);
            order.setStatus("4");
            order = updateOrder(order);
            return;
        } else {
            //其他订单 只在失败的情况下才能申请退款
            if (!"3".equals(orderVo.getSendStatus())) {
                throw new ServiceException("该订单无法申请退款");
            }
        }
        //失败订单直接给退钱
        refund.setStatus("1");
        refund.setRefundReviewer("系统");
        refundMapper.insert(refund);
        MerchantVo merchantVo = null;
        if (!"2".equals(orderVo.getPickupMethod())) {
            merchantVo = merchantService.queryById(orderVo.getPayMerchant());
            if (ObjectUtil.isEmpty(merchantVo)) {
                throw new ServiceException("商户不存在，请联系客服处理");
            }
        }
        OrderBackTrans orderBackTrans = new OrderBackTrans();
        orderBackTrans.setRefund(orderVo.getOutAmount());
        orderBackTrans.setThNumber(IdUtils.getSnowflakeNextIdStr("T"));
        orderBackTrans.setOrderBackTransState("0");
        orderBackTrans.setNumber(orderVo.getNumber());
        order.setStatus("4");
        //1.付费领取
        if ("1".equals(order.getPickupMethod())) {
            OrderInfoVo orderInfoVo = orderInfoMapper.selectVoById(orderBackTrans.getNumber());
            Map<String, String> result = PayUtils.backTransReq(orderInfoVo.getQueryId(), BigDecimalUtils.toMinute(orderBackTrans.getRefund()).toString(), orderBackTrans.getThNumber(), merchantVo.getRefundCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), orderInfoVo.getIssAddnData());
            if (ObjectUtil.isEmpty(result)) {
                throw new ServiceException("请求失败，请联系客服处理");
            }
            String respCode = result.get("respCode");
            String queryId = result.get("queryId");
            String txnTime = result.get("txnTime");
            String origQryId = result.get("origQryId");
            if (("00").equals(respCode)) {
                orderBackTrans.setQueryId(queryId);
                orderBackTrans.setOrigQryId(origQryId);
                orderBackTrans.setTxnTime(txnTime);
                orderBackTrans.setOrderBackTransState("2");
                orderBackTrans.setPickupMethod("1");
                orderBackTrans.setSuccessTime(DateUtils.getNowDate());
                order.setStatus("5");
                if (collectiveOrder.getCancelAmount().add(order.getWantAmount()).compareTo(collectiveOrder.getWantAmount()) < 0) {
                    //没退完
                    collectiveOrder.setStatus("4");
                    collectiveOrder.setCancelAmount(collectiveOrder.getCancelAmount().add(order.getWantAmount()));
                    collectiveOrder.setCancelStatus("3");
                } else {
                    //全退完了
                    collectiveOrder.setStatus("5");
                    collectiveOrder.setCancelAmount(collectiveOrder.getCancelAmount().add(order.getWantAmount()));
                    collectiveOrder.setCancelStatus("1");
                }

            } else if (("03").equals(respCode) || ("04").equals(respCode) || ("05").equals(respCode)) {
                //后续需发起交易状态查询交易确定交易状态
            } else {
                LogUtil.writeLog("【" + orderBackTrans.getNumber() + "】退款失败，应答信息respCode=" + respCode + ",respMsg=" + result.get("respMsg"));
                //其他应答码为失败请排查原因
                orderBackTrans.setOrderBackTransState("1");
                order.setStatus("6");
            }
        } else if ("2".equals(order.getPickupMethod())) {
            //2.积点兑换
            UserVo userVo = userService.queryById(order.getUserId(), PlatformEnumd.MP_YSF.getChannel());
            if (ObjectUtil.isEmpty(userVo)) {
                throw new ServiceException("用户不存在，请联系客服处理");
            }
            R<Void> result = YsfUtils.memberPointAcquire(orderVo.getNumber(), Integer.toUnsignedLong(orderBackTrans.getRefund().intValue()), "1", orderVo.getProductName() + "退款", userVo.getOpenId(), "1", orderVo.getPlatformKey());
            if (result.getCode() == 200) {
                orderBackTrans.setPickupMethod("2");
                orderBackTrans.setSuccessTime(DateUtils.getNowDate());
                orderBackTrans.setOrderBackTransState("2");
                order.setStatus("5");
                //全退完了
                collectiveOrder.setStatus("5");
                collectiveOrder.setCancelAmount(collectiveOrder.getCancelAmount().add(order.getWantAmount()));
                collectiveOrder.setCancelStatus("1");
            } else {
                orderBackTrans.setOrderBackTransState("1");
                order.setStatus("6");
                //退款失败
                collectiveOrder.setStatus("6");
                collectiveOrder.setCancelAmount(collectiveOrder.getCancelAmount().add(order.getWantAmount()));
                collectiveOrder.setCancelStatus("2");
            }
        }
        collectiveOrderMapper.updateById(collectiveOrder);
        order = updateOrder(order);
        if ("9".equals(order.getOrderType())) {
            Order ob = new Order();
            ob.setStatus(order.getStatus());
            baseMapper.update(ob, new LambdaQueryWrapper<Order>().eq(Order::getParentNumber, order.getNumber()));
        }
        PermissionUtils.setDeptIdAndUserId(orderBackTrans, order.getSysDeptId(), order.getSysUserId());
        orderBackTransMapper.insert(orderBackTrans);
    }

    /**
     * 订单支付(大订单支付)
     *
     * @param collectiveNumber 大订单号
     * @param userId           用户ID
     * @return 支付tn 成功返回ok
     */
    @Override
    public PayResultVo payOrder(Long collectiveNumber, Long userId) {
        PayResultVo payResultVo = new PayResultVo();
        CollectiveOrder collectiveOrder = getCollectiveOrder(collectiveNumber);
        if (null == collectiveOrder) {
            throw new ServiceException("订单不存在");
        }
        if (!collectiveOrder.getUserId().equals(userId)) {
            throw new ServiceException("登录超时，请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }

        List<Order> orders = baseMapper.selectList(new LambdaQueryWrapper<Order>().eq(Order::getCollectiveNumber, collectiveOrder.getCollectiveNumber()));
        if (ObjectUtil.isEmpty(orders)) {
            throw new ServiceException("订单不存在");
        }

        PlatformVo platformVo = platformService.queryById(ZlyyhUtils.getPlatformId(), ZlyyhUtils.getPlatformChannel());
        if (null == platformVo) {
            throw new ServiceException("平台异常");
        }
        MerchantVo merchantVo = null;
        // 查询城市商户
        String adcode = ServletUtils.getHeader(ZlyyhConstants.AD_CODE);
        if (StringUtils.isNotBlank(adcode)) {
            String s = adcode.substring(0, 4) + "00";
            CityMerchantVo cityMerchantVo = cityMerchantService.queryOneByCityCode(s, collectiveOrder.getPlatformKey());
            if (null != cityMerchantVo) {
                merchantVo = merchantService.queryById(cityMerchantVo.getMerchantId());
            }
        }
        //此处查询支付商户号
        if (orders.size() == 1) {
            // 查询商品信息
            ProductVo productVo = productService.queryById(orders.get(0).getProductId());
            if (null == productVo || !"0".equals(productVo.getStatus())) {
                throw new ServiceException(productVo.getProductName() + "不存在或已下架[pay]");
            }
            if (null != productVo.getMerchantId()) {
                merchantVo = merchantService.queryById(productVo.getMerchantId());
            }
        }
        if (null == merchantVo) {
            if (null != platformVo.getMerchantId()) {
                merchantVo = merchantService.queryById(platformVo.getMerchantId());
            }
        }
        if (null == merchantVo) {
            throw new ServiceException("支付商户配置异常");
        }

        // 积点兑换 扣除积点
        UserVo userVo = userService.queryById(collectiveOrder.getUserId(), ZlyyhUtils.getPlatformChannel());
        if (null == userVo || StringUtils.isBlank(userVo.getOpenId())) {
            throw new ServiceException("登录超时，请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        if ("0".equals(collectiveOrder.getStatus()) || "1".equals(collectiveOrder.getStatus())) {
            if (DateUtils.compare(collectiveOrder.getExpireDate()) <= 0) {
                try {
                    cancel(collectiveOrder.getCollectiveNumber(), collectiveOrder.getUserId());
                } catch (Exception ignored) {
                }
                throw new ServiceException("订单超时已关闭");
            }
            //如果是小订单里存在无需支付的商品还是要返回错误信息
            for (Order order : orders) {
                if ("0".equals(order.getPickupMethod())) {
                    throw new ServiceException("订单无需支付");
                } else if (!"1".equals(order.getPickupMethod()) && !"2".equals(order.getPickupMethod())) {
                    throw new ServiceException("订单异常[PickupMethod]");
                }
                // 查询商品信息
                ProductVo productVo = productService.queryById(order.getProductId());
                if (null == productVo || !"0".equals(productVo.getStatus())) {
                    throw new ServiceException(productVo.getProductName() + "不存在或已下架[pay]");
                }
                payResultVo.setIsPoup(productVo.getIsPoup());
                payResultVo.setPoupText(productVo.getPoupText());
                if ("1".equals(order.getPickupMethod())) {
                    order.setPayMerchant(merchantVo.getId());
                    order = updateOrder(order);
                    // 直销走银联支付，代销走原先支付
                    if ("12".equals(productVo.getProductType()) || "1".equals(productVo.getUnionPay())) {
                        payResultVo.setPayData(unionPayChannelService.getPayTn(order.getNumber(), order.getPlatformKey()));
                        return payResultVo;
                    }
                } else if ("2".equals(order.getPickupMethod())) {
                    R<Void> result = YsfUtils.memberPointDeduct(order.getNumber(), order.getWantAmount().longValue(), order.getProductName(), userVo.getOpenId(), order.getPlatformKey());
                    if (R.isError(result)) {
                        throw new ServiceException("积点扣除失败[" + result.getMsg() + "]");
                    }
                    // 修改订单为支付成功
                    order.setOutAmount(order.getWantAmount());
                    order.setStatus("2");
                    order.setPayTime(new Date());
                    order = updateOrder(order);
                    // 删除待支付订单缓存
                    delCacheOrder(order.getNumber());
                    // 发券
                    //判断是否为随机产品 再发券
                    if ("10".equals(order.getOrderType())) {
                        checkRandomProduct(order);
                    }
                    SpringUtils.context().publishEvent(new SendCouponEvent(order.getNumber(), order.getPlatformKey()));
                    payResultVo.setPayData("ok");
                    return payResultVo;
                }

            }
            //保存大订单支付商户信息
            collectiveOrder.setPayMerchant(merchantVo.getId());
            collectiveOrderMapper.updateById(collectiveOrder);
            // 支付金额 乘100，把元转成分
            Integer amount = BigDecimalUtils.toMinute(collectiveOrder.getWantAmount());
            if ("0".equals(merchantVo.getMerchantType())) {
                // 云闪付支付
                String tn = PayUtils.pay(collectiveOrder.getCollectiveNumber().toString(), amount.toString(), merchantVo.getPayCallbackUrl(), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath(), null, collectiveOrder.getExpireDate());
                if (StringUtils.isEmpty(tn)) {
                    throw new ServiceException("支付异常，请稍后重试");
                }
                payResultVo.setPayData(tn);
                return payResultVo;
            } else if ("1".equals(merchantVo.getMerchantType())) {
                // 微信支付
                String payCallbackUrl = merchantVo.getPayCallbackUrl();
                if (!payCallbackUrl.contains(merchantVo.getId().toString())) {
                    payCallbackUrl = payCallbackUrl + "/" + merchantVo.getId();
                }
                try {
                    Map<String, String> resultMap = WxUtils.wxPay(collectiveOrder.getCollectiveNumber().toString(), wxProperties.getPayUrl(), platformVo.getAppId(), merchantVo.getMerchantNo(), platformVo.getPlatformName(), amount, userVo.getOpenId(), payCallbackUrl, merchantVo.getCertPath(), merchantVo.getMerchantKey(), merchantVo.getApiKey());
                    payResultVo.setPayData(JsonUtils.toJsonString(resultMap));
                    return payResultVo;
                } catch (Exception e) {
                    log.error("微信支付异常，", e);
                    throw new ServiceException("支付异常，请稍后重试");
                }
            } else {
                throw new ServiceException("支付系统异常");
            }

        } else if ("3".equals(collectiveOrder.getStatus())) {
            throw new ServiceException("订单已关闭");
        }
        payResultVo.setPayData("ok");
        return payResultVo;
    }

    /**
     * 设置订单为取消状态
     *
     * @param collectiveOrder 大订单信息
     */
    public boolean updateOrderClose(CollectiveOrder collectiveOrder, List<Order> orders) {
        //查询支付是否成功
        String s = queryOrderPay(collectiveOrder.getCollectiveNumber());
        if ("订单支付成功".equals(s)) {
            collectiveOrder = getCollectiveOrder(collectiveOrder.getCollectiveNumber());
            if (!"0".equals(collectiveOrder.getStatus()) && !"1".equals(collectiveOrder.getStatus())) {
                return false;
            }
        }
        //先将大订单状态改为取消(已关闭)
        CollectiveOrder co = new CollectiveOrder();
        co.setCollectiveNumber(collectiveOrder.getCollectiveNumber());
        co.setStatus("3");
        collectiveOrderMapper.updateById(co);

        // 修改小订单状态为取消(已关闭)
        for (Order order : orders) {
            Order o = new Order();
            o.setNumber(order.getNumber());
            o.setStatus("3");
            updateOrder(o);
            // 删除用户未支付订单
            delCacheOrder(order.getNumber());
            // 回退名额
            callbackOrderCountCache(order.getPlatformKey(), order.getUserId(), order.getProductId(), order.getCreateTime(), order.getCount());
            try {
                if ("13".equals(order.getOrderType())) {
                    // 处理演出订单
                    OrderTicket orderTicket = orderTicketMapper.selectById(order.getNumber());
                    // 查询已购数量
                    Object cacheObject = RedisUtils.getCacheObject("lineNumber:" + orderTicket.getLineId());
                    // 若不为空执行
                    if (ObjectUtil.isNotEmpty(cacheObject)) {
                        Long count = (Long) cacheObject;
                        count -= orderTicket.getCount();
                        RedisUtils.setCacheObject("lineNumber:" + orderTicket.getLineId(), count, Duration.ofDays(1));
                    }
                }
                // 银联分销订单（处理）
                if ("11".equals(order.getOrderType()) || "12".equals(order.getOrderType()) || "1".equals(order.getUnionPay())) {
                    if ("11".equals(order.getOrderType())) {
                        UnionPayDistributionUtil.orderCancel(order.getNumber(), order.getExternalOrderNumber(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJCAppId(order.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJC(order.getPlatformKey()));
                    } else {
                        UnionPayDistributionUtil.orderCancel(order.getNumber(), order.getExternalOrderNumber(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(order.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJD(order.getPlatformKey()));
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // 如果有优惠券，设置优惠券状态为可用状态
        if (ObjectUtil.isNotEmpty(collectiveOrder.getCouponId())) {
            Coupon coupon = couponMapper.selectById(collectiveOrder.getCouponId());
            if (ObjectUtil.isNotEmpty(coupon) && "2".equals(coupon.getUseStatus())) {
                coupon.setUseStatus("1");
                couponMapper.updateById(coupon);
            }
        }

        return true;
    }

    /**
     * 查询订单支付状态
     *
     * @param collectiveNumber 大订单号
     * @return 支付结果
     */
    @Override
    public String queryOrderPay(Long collectiveNumber) {
        CollectiveOrder collectiveOrder = getCollectiveOrder(collectiveNumber);
        if (null == collectiveOrder) {
            return "订单不存在";
        }
        List<Order> orders = baseMapper.selectList(new LambdaQueryWrapper<Order>().eq(Order::getCollectiveNumber, collectiveOrder.getCollectiveNumber()));
        for (Order order : orders) {
            // 直销商品额外处理
            if ("12".equals(order.getOrderType()) || "1".equals(order.getUnionPay())) {
                // 查询订单券码状态
                List<OrderUnionSendVo> orderUnionSendVos = orderUnionSendService.queryListByNumber(order.getNumber());
                if (!orderUnionSendVos.isEmpty()) {
                    return "订单支付成功";
                }
                // 未发券，执行订单发券操作
                OrderUnionPay orderUnionPay = orderUnionPayMapper.selectById(order.getNumber());
                // 查询订单支付状态
                String orderStatusStr = UnionPayDistributionUtil.orderStatus(UnionPayBizMethod.FRONT.getBizMethod(), null, order.getNumber(), orderUnionPay.getTxnTime(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(order.getPlatformKey()), YsfDistributionPropertiesUtils.getCertPathJD(order.getPlatformKey()));
                JSONObject orderStatus = JSONObject.parseObject(orderStatusStr);
                if (orderStatus.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
                    String prodOrderSt = orderStatus.getString("prodOrderSt");
                    if (StringUtils.isNotEmpty(prodOrderSt) && "00".equals(prodOrderSt)) {
                        // 直销（银联分销）
                        order.setStatus("2");
                        order = updateOrder(order);
                        SpringUtils.context().publishEvent(new SendCouponEvent(order.getNumber(), order.getPlatformKey()));
                        return "订单支付成功";
                    }
                }
                return "订单未支付";
            }
        }

        if ("3".equals(collectiveOrder.getStatus())) {
            return "订单已关闭";
        } else if ("0".equals(collectiveOrder.getStatus()) || "1".equals(collectiveOrder.getStatus())) {
            if (null == collectiveOrder.getPayMerchant()) {
                return "订单未支付";
            }
            MerchantVo merchantVo = merchantService.queryById(collectiveOrder.getPayMerchant());
            if (null == merchantVo) {
                return "商户不存在";
            }
            if ("0".equals(merchantVo.getMerchantType())) {
                return queryYsfPayStatus(collectiveOrder, merchantVo);
            } else if ("1".equals(merchantVo.getMerchantType())) {
                return queryWxPayStatus(collectiveOrder, merchantVo);
            }
        }
        return "订单支付成功";

    }

    /**
     * 微信支付回调
     */
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean wxCallBack(Long merchantId, HttpServletRequest request) {
        //验证签名
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IoUtil.read(reader);
        log.info("微信支付回调通知，商户号ID：{},通知内容：{}", merchantId, body);
        AppWxPayCallbackParams appWxPayCallbackParams = JsonUtils.parseObject(body, AppWxPayCallbackParams.class);
        // 查询商户信息
        MerchantVo merchantVo = merchantService.queryById(merchantId);
        if (null == merchantVo) {
            log.error("微信支付回调通知，商户号不存在：{}", merchantId);
            throw new ServiceException("商户号不存在");
        }
        String apiV3Key, merchantKey, mchid, merchantSerialNumber;
        apiV3Key = merchantVo.getApiKey();
        merchantKey = merchantVo.getCertPath();
        mchid = merchantVo.getMerchantNo();
        merchantSerialNumber = merchantVo.getMerchantKey();
        // 查询订单信息
        String s;
        try {
            s = WxUtils.decryptToString(apiV3Key.getBytes(StandardCharsets.UTF_8), appWxPayCallbackParams.getResource().getAssociated_data().getBytes(StandardCharsets.UTF_8), appWxPayCallbackParams.getResource().getNonce().getBytes(StandardCharsets.UTF_8), appWxPayCallbackParams.getResource().getCiphertext());
        } catch (Exception e) {
            log.error("微信支付回调解密异常:", e);
            throw new ServiceException("解密异常");
        }
        log.info("微信支付回调通知，商户号ID：{},解密后内容：{}", merchantId, s);

        String wechatPayTimestamp = request.getHeader("Wechatpay-Timestamp");
        String wechatPayNonce = request.getHeader("Wechatpay-Nonce");
        String wechatSignature = request.getHeader("Wechatpay-Signature");
        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        //签名信息
        String signMessage = wechatPayTimestamp + "\n" + wechatPayNonce + "\n" + body + "\n";
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
            WxUtils.getCertInput(merchantKey));

        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
            new WechatPay2Credentials(mchid, new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)),
            apiV3Key.getBytes(StandardCharsets.UTF_8));
        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3秘钥）
        boolean verify = verifier.verify(wechatPaySerial, signMessage.getBytes(StandardCharsets.UTF_8), wechatSignature);
        if (!verify) {
            log.info("验签失败，签名信息：" + signMessage + "平台证书序列号：" + wechatPaySerial + "签名：" + wechatSignature);
//            throw new ServiceException("验签失败");
        }
        log.info("微信支付回调验签成功");

        Map<String, Object> result = JsonUtils.parseMap(s);
        if (null == result || null == result.get("out_trade_no")) {
            throw new ServiceException("无订单号out_trade_no");
        }
        // 商户订单号
        String orderId = (String) result.get("out_trade_no");
        // 交易状态
        String trade_state = (String) result.get("trade_state");
        // 微信支付订单号
        String queryId = (String) result.get("transaction_id");
        // 交易完成时间
        String pay_time = (String) result.get("success_time");
        // 付款银行
        String bank_type = (String) result.get("bank_type");
        // 优惠功能，享受优惠时返回该字段。
        String issAddnData = JsonUtils.toJsonString(result.get("promotion_detail"));
        // 订单金额
        Map<String, Object> amount = BeanUtil.beanToMap(result.get("amount"));
        // 订单总金额
        Integer total = (Integer) amount.get("total");
        // 用户支付金额
        Integer payerTotal = (Integer) amount.get("payer_total");

        // 交易状态，枚举值：
        //SUCCESS：支付成功
        //REFUND：转入退款
        //NOTPAY：未支付
        //CLOSED：已关闭
        //REVOKED：已撤销（付款码支付）
        //USERPAYING：用户支付中（付款码支付）
        //PAYERROR：支付失败(其他原因，如银行返回失败)
        if ("SUCCESS".equals(trade_state)) {
            // 查询大订单
            CollectiveOrder collectiveOrder = getCollectiveOrder(Long.parseLong(orderId));

            if (null == collectiveOrder) {
                log.error("微信支付回调订单【{}】不存在,通知内容：{}", orderId, s);
                return false;
            }
            handleOrder(collectiveOrder, BigDecimalUtils.toMoney(total), queryId, pay_time, queryId, pay_time, issAddnData, bank_type);
            return true;
        }
        return false;
    }

    /**
     * 查询微信订单支付状态
     *
     * @param collectiveOrder 大订单信息
     * @return 支付结果
     */
    @Transactional
    public String queryWxPayStatus(CollectiveOrder collectiveOrder, MerchantVo merchantVo) {
        String result;
        try {
            result = WxUtils.queryWxOrder(wxProperties.getQueryPayStatusUrl(), collectiveOrder.getCollectiveNumber().toString(), merchantVo.getMerchantNo(), merchantVo.getCertPath(), merchantVo.getMerchantKey(), merchantVo.getApiKey());
        } catch (IOException e) {
            log.error("微信订单支付结果查询异常：", e);
            return "系统繁忙";
        }
        log.info("订单号：{},查询微信订单返回结果：{}", collectiveOrder.getCollectiveNumber(), result);
        if (StringUtils.isBlank(result)) {
            return "支付结果查询失败，请稍后重试";
        }
        JSONObject resultData = JSONObject.parseObject(result);
        if (null == resultData.get("out_trade_no") || null == resultData.get("trade_state")) {
            if ("ORDER_NOT_EXIST".equals(resultData.getString("code"))) {
                return "订单未支付";
            }
            return "支付结果查询失败，请稍后重试";
        }
        // 交易状态
        String trade_state = resultData.getString("trade_state");
        // 交易状态，枚举值：
        //SUCCESS：支付成功
        //REFUND：转入退款
        //NOTPAY：未支付
        //CLOSED：已关闭
        //REVOKED：已撤销（付款码支付）
        //USERPAYING：用户支付中（付款码支付）
        //PAYERROR：支付失败(其他原因，如银行返回失败)
        if ("SUCCESS".equals(trade_state)) {
            // 商户订单号
            String orderId = resultData.getString("out_trade_no");
            // 微信支付订单号
            String queryId = resultData.getString("transaction_id");
            // 交易完成时间
            String pay_time = resultData.getString("success_time");
            // 付款银行
            String bank_type = resultData.getString("bank_type");
            // 优惠功能，享受优惠时返回该字段。
            String issAddnData = resultData.getString("promotion_detail");
            // 订单金额
            JSONObject amount = resultData.getJSONObject("amount");
            // 订单总金额
            Integer total = amount.getInteger("total");
            // 用户支付金额
            Integer payerTotal = amount.getInteger("payer_total");

            handleOrder(collectiveOrder, BigDecimalUtils.toMoney(total), queryId, pay_time, queryId, pay_time, issAddnData, bank_type);
            return "订单支付成功。";
        } else if ("NOTPAY".equals(trade_state)) {
            return "订单未支付。";
        } else if ("PAYERROR".equals(trade_state)) {
            return "订单支付失败。";
        } else {
            return "订单未支付。";
        }
    }

    /**
     * 查询云闪付订单支付状态
     *
     * @param collectiveOrder 大订单信息
     * @return 支付结果
     */
    @Transactional
    public String queryYsfPayStatus(CollectiveOrder collectiveOrder, MerchantVo merchantVo) {
        Map<String, String> rspData = PayUtils.queryOrder(collectiveOrder.getCollectiveNumber().toString(), DateUtils.parseDateToStr("yyyyMMddHHmmss", collectiveOrder.getCreateTime()), merchantVo.getMerchantNo(), merchantVo.getMerchantKey(), merchantVo.getCertPath());
        if (null == rspData) {
            return "支付结果查询失败，请稍后重试";
        }
        String origRespCode = rspData.get("origRespCode");
        // 查询订单时的订单号
        String queryId = rspData.get("queryId");
        // 交易传输时间
        String traceTime = rspData.get("traceTime");
        // 系统跟踪号
        String traceNo = rspData.get("traceNo");
        // 交易金额，单位分
        String txnAmt = rspData.get("txnAmt");
        // 转换单位为元
        BigDecimal txnAmount = BigDecimalUtils.toMoney(Integer.valueOf(txnAmt));
        // 记录订单发送时间	 商户代码merId、商户订单号orderId、订单发送时间txnTime三要素唯一确定一笔交易。
        String txnTime = rspData.get("txnTime");
        // 5.1.2issAddnData订单优惠信息（支持单品）
        String issAddnData = rspData.get("issAddnData");
        if (StringUtils.isNotBlank(issAddnData)) {
            issAddnData = Base64.decodeStr(issAddnData);
        }
        if (("00").equals(origRespCode)) {
            //交易成功，
            handleOrder(collectiveOrder, txnAmount, queryId, traceTime, traceNo, txnTime, issAddnData, "");
            return "订单支付成功";
        } else {
            //其他应答码为交易失败
            log.info("交易失败：" + rspData.get("origRespMsg") + "。<br> ");
            return "订单未支付";
        }
    }

    /**
     * 定时任务取消订单
     */
    @Override
    public void cancelOrder() {
        List<CollectiveOrderVo> orderVos = collectiveOrderMapper.selectVoList(new LambdaQueryWrapper<CollectiveOrder>().in(CollectiveOrder::getStatus, "0", "1").lt(CollectiveOrder::getExpireDate, new Date()).last("limit 300"));
        if (CollectionUtils.isNotEmpty(orderVos)) {
            for (CollectiveOrderVo orderVo : orderVos) {
                cancel(orderVo.getCollectiveNumber(), orderVo.getUserId());
            }
        }
    }

    /**
     * 定时任务取消订单
     */
    @Override
    public void queryOrderHandler() {
        List<CollectiveOrderVo> orderVos = collectiveOrderMapper.selectVoList(new LambdaQueryWrapper<CollectiveOrder>().in(CollectiveOrder::getStatus, "0", "1").lt(CollectiveOrder::getExpireDate, new Date()).last("limit 300"));
        if (CollectionUtils.isNotEmpty(orderVos)) {
            for (CollectiveOrderVo orderVo : orderVos) {
                queryOrderPay(orderVo.getCollectiveNumber());
            }
        }
    }

    /**
     * 定时任务取消订单
     */
    @Async
    @Override
    public void cancelOrder(Long userId) {

        List<CollectiveOrderVo> orderVos = collectiveOrderMapper.selectVoList(new LambdaQueryWrapper<CollectiveOrder>().in(CollectiveOrder::getStatus, "0", "1").lt(CollectiveOrder::getExpireDate, new Date()).eq(CollectiveOrder::getUserId, userId));
        if (CollectionUtils.isNotEmpty(orderVos)) {
            for (CollectiveOrderVo orderVo : orderVos) {
                cancel(orderVo.getCollectiveNumber(), orderVo.getUserId());
            }
        }
    }

    /**
     * 发放状态
     */
    @Override
    public List<OrderVo> sendStatusOrder() {
        List<OrderVo> orderVos = baseMapper.selectVoList(new LambdaQueryWrapper<Order>().eq(Order::getStatus, "2").in(Order::getSendStatus, "0", "1", "3").gt(Order::getCreateTime, DateUtil.offsetDay(new Date(), -3).toJdkDate()).last("limit 500"));
        if (ObjectUtil.isNotEmpty(orderVos)) {
            List<OrderVo> list = new ArrayList<>(orderVos.size());
            for (OrderVo orderVo : orderVos) {
                if ("0".equals(orderVo.getSendStatus()) || "3".equals(orderVo.getSendStatus())) {
                    list.add(orderVo);
                } else {
                    queryOrderSendStatus(orderVo.getPushNumber());
                    OrderVo order = baseMapper.selectVoById(orderVo.getNumber());
                    if (null != order && "3".equals(order.getSendStatus())) {
                        list.add(order);
                    }
                }
            }
            return list;
        }
        return orderVos;
    }

    /**
     * 云闪付支付回调
     *
     * @param valideData 通知信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payCallBack(Map<String, String> valideData) {
        log.info("银联支付回调信息：{}", valideData);
        //获取后台通知的数据，其他字段也可用类似方式获取
        // 应答码
        String respCode = valideData.get("respCode");
        // 商户订单号
        String orderId = valideData.get("orderId");
        // 交易金额，单位分
        String txnAmt = valideData.get("txnAmt");
        // 转换单位为元
        BigDecimal txnAmount = BigDecimalUtils.toMoney(Integer.valueOf(txnAmt));
        // 查询流水号 消费交易的流水号，供后续查询用
        String queryId = valideData.get("queryId");
        // 记录订单发送时间	 商户代码merId、商户订单号orderId、订单发送时间txnTime三要素唯一确定一笔交易。
        String txnTime = valideData.get("txnTime");
        // 交易传输时间
        String traceTime = valideData.get("traceTime");
        // 5.1.2issAddnData订单优惠信息（支持单品）
        String issAddnData = valideData.get("issAddnData");
        if (StringUtils.isNotBlank(issAddnData)) {
            issAddnData = Base64.decodeStr(issAddnData);
        }
        // 系统跟踪号
        String traceNo = valideData.get("traceNo");
        //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
        if ("00".equals(respCode)) {
            // 00 交易成功
            log.info("银联支付回调信息：订单号：{}，交易金额：{}元，查询流水号：{}，订单发送时间：{}，交易传输时间：{}，系统跟踪号：{},单品信息：{}", orderId, txnAmount, queryId, txnTime, traceTime, traceNo, issAddnData);
            // 查询订单信息
            try {
                CollectiveOrder collectiveOrder = getCollectiveOrder(Long.parseLong(orderId));
                if (null == collectiveOrder) {
                    log.error("银联支付回调订单【{}】不存在,通知内容：{}", orderId, valideData);
                    return;
                }
                handleOrder(collectiveOrder, txnAmount, queryId, traceTime, traceNo, txnTime, issAddnData, "");
            } catch (Exception e) {
                log.error("订单回调通知处理异常：", e);
            }
        }
    }

    /**
     * 美食订单支付回调
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void foodPayCallBack(JSONObject data) {
        log.info("美食订单支付回调信息：{}", data);
        // 美食订单号
        String exNumber = data.getString("number");
        //核销码
        String ticketCode = data.getString("ticketCode");
        //凭证id
        String voucherId = data.getString("voucherId");
        //凭证状态
        String voucherStatus = data.getString("voucherStatus");
        //凭证生效时间
        String effectTime = data.getString("effectTime");
        //凭证失效时间
        String expireTime = data.getString("expireTime");
        //总份数
        Integer totalAmount = data.getInteger("totalAmount");
        //已使用份数
        Integer usedAmount = data.getInteger("usedAmount");
        //已退款份数
        Integer refundAmount = data.getInteger("refundAmount");
        if (ObjectUtil.isEmpty(exNumber)) {
            log.info("美食订单不存在,通知内容：{}", data);
            return;
        }
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getExternalOrderNumber, exNumber));
        if (ObjectUtil.isEmpty(order)) {
            log.error("美食订单【{}】不存在,通知内容：{}", exNumber, data);
            return;
        }
        OrderFoodInfo orderFoodInfo = orderFoodInfoMapper.selectById(order.getNumber());
        if (ObjectUtil.isEmpty(orderFoodInfo)) {
            log.error("美食订单【{}】不存在,通知内容：{}", exNumber, data);
            return;
        }
        // 删除未支付订单缓存
        delCacheOrder(order.getNumber());
        OrderPushInfo orderPushInfo = orderPushInfoMapper.selectOne(new LambdaQueryWrapper<OrderPushInfo>().eq(OrderPushInfo::getNumber, order.getNumber()));
        if (ObjectUtil.isNotEmpty(ticketCode)) {
            order.setSendStatus("2");
            if (ObjectUtil.isNotEmpty(orderPushInfo)) {
                orderPushInfo.setStatus("1");
                orderPushInfoMapper.updateById(orderPushInfo);
            }
            orderFoodInfo.setTicketCode(ticketCode);
            orderFoodInfo.setVoucherId(voucherId);
            orderFoodInfo.setVoucherStatus(voucherStatus);
            orderFoodInfo.setEffectTime(effectTime);
            orderFoodInfo.setExpireTime(expireTime);
            orderFoodInfo.setTotalAmount(totalAmount);
            orderFoodInfo.setUsedAmount(usedAmount);
            orderFoodInfo.setRefundAmount(refundAmount);
            order = updateOrder(order);
            orderFoodInfoMapper.updateById(orderFoodInfo);
        }
    }

    /**
     * 携程订单支付回调
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void ctripOrderCallBack(JSONObject data) {
        log.info("携程订单支付回调信息：{}", data);
        // 美食订单号
        String exNumber = data.getString("orderId");

        if (ObjectUtil.isEmpty(exNumber)) {
            log.info("美食订单不存在,通知内容：{}", data);
            return;
        }
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getExternalOrderNumber, exNumber));
        if (ObjectUtil.isEmpty(order)) {
            log.error("携程订单【{}】不存在,通知内容：{}", exNumber, data);
            return;
        }
        OrderFoodInfo orderFoodInfo = orderFoodInfoMapper.selectById(order.getNumber());
        if (ObjectUtil.isEmpty(orderFoodInfo)) {
            log.error("携程订单【{}】不存在,通知内容：{}", exNumber, data);
            return;
        }
        // 删除未支付订单缓存
        delCacheOrder(order.getNumber());
        OrderPushInfo orderPushInfo = orderPushInfoMapper.selectOne(new LambdaQueryWrapper<OrderPushInfo>().eq(OrderPushInfo::getNumber, order.getNumber()));
        String status = data.getString("orderStatus");
        //根据这里的订单状态 同步美食订单的状态
        orderFoodInfo.setOrderStatus(status);

        JSONArray codes = data.getJSONArray("codes");
        if (ObjectUtil.isNotEmpty(codes)) {
            JSONObject resultJson = codes.getJSONObject(0);
            String codeId = resultJson.getString("codeId");
            String code = resultJson.getString("code");
            String couponStatus = resultJson.getString("status");
            order.setSendStatus("2");
            orderFoodInfo.setTicketCode(code);
            orderFoodInfo.setVoucherId(codeId);
            //把订单里的核销状态加入
            if (resultJson.getString("status").equals("0")) {
                order.setVerificationStatus("0");
                orderFoodInfo.setVoucherStatus("EFFECTIVE");

            } else if (resultJson.getString("status").equals("3")) {
                orderFoodInfo.setVoucherStatus("USED");
                orderFoodInfo.setUsedAmount(1);
                order.setVerificationStatus("1");
            } else {
                orderFoodInfo.setVoucherStatus("CANCELED");
                order.setVerificationStatus("2");
            }

            if (ObjectUtil.isNotEmpty(orderPushInfo)) {
                orderPushInfo.setStatus("1");
                orderPushInfoMapper.updateById(orderPushInfo);
            }

            orderFoodInfoMapper.updateById(orderFoodInfo);
        }

        if (status.equals("6")) {
            //如果是退款的推送 订单修改为供应商已退款
            order.setCancelStatus("1");
        }
        baseMapper.updateById(order);
    }

    /**
     * 订单支付回调 银联分销（直销）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unionPayBack(JSONObject data) {
        // 获取商品订单号
        String prodTn = data.getString("prodTn");
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getExternalOrderNumber, prodTn));
        if (order == null) {
            return;
        }
        if (!"12".equals(order.getOrderType()) && !"1".equals(order.getUnionPay())) {
            log.info("非银联分销（直销订单），不能执行发券");
            return;
        }
        //查询大订单
        CollectiveOrder collectiveOrder = getCollectiveOrder(order.getCollectiveNumber());
        // 删除未支付订单缓存
        delCacheOrder(order.getNumber());
        if (data.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
            List<OrderUnionSendVo> orderUnionSendVos = orderUnionSendService.queryListByNumber(order.getNumber());
            OrderUnionPay orderUnionPay = orderUnionPayMapper.selectById(order.getNumber());
            orderUnionPay.setSettleDt(data.getString("settleDt")); // 清算日期
            orderUnionPay.setSettleCurrencyCode(data.getString("settleCurrencyCode"));// 清算币中
            orderUnionPay.setSettleAmt(data.getString("settleAmt"));// 清算金额/单位分
            orderUnionPay.setSettleKey(data.getString("settleKey"));//清算主键
            if (orderUnionSendVos.isEmpty()) {
                // 修改订单状态
                order.setStatus("2");
                order = updateOrder(order);
                collectiveOrder.setStatus("2");
                collectiveOrderMapper.updateById(collectiveOrder);
                SpringUtils.context().publishEvent(new SendCouponEvent(order.getNumber(), order.getPlatformKey()));
            }
            orderUnionPayMapper.updateById(orderUnionPay);
        } else {
            log.info("银联分销支付异常。{}", data.getString("msg"));
            log.info("银联分销支付异常详情。{}", data.getString("subMsg"));
        }
    }

    /**
     * 订单发券与发券接口返回后处理 (银联分销)
     */
    private void orderUnionPaySendHand(String externalProductId, Order order, OrderUnionPay orderUnionPay, JSONObject data) {
        if (data.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
            // 发起订单状态查询，向银联确认发券状态
            String orderStatusStr;
            String certPath;
            if ("11".equals(order.getOrderType())) {
                certPath = YsfDistributionPropertiesUtils.getCertPathJC(order.getPlatformKey());
                orderStatusStr = UnionPayDistributionUtil.orderStatus(UnionPayBizMethod.CHNLPUR.getBizMethod(), externalProductId, order.getNumber(), data.getString("txnTime"), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJCAppId(order.getPlatformKey()), certPath);
            } else {
                certPath = YsfDistributionPropertiesUtils.getCertPathJD(order.getPlatformKey());
                orderStatusStr = UnionPayDistributionUtil.orderStatus(UnionPayBizMethod.chnlpur.getBizMethod(), externalProductId, order.getNumber(), orderUnionPay.getTxnTime(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(order.getPlatformKey()), certPath);
            }
            JSONObject orderStatus = JSONObject.parseObject(orderStatusStr);
            // 查询成功，开始处理。
            if (orderStatus.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
                // 发放成功
                if ("05".equals(orderStatus.getString("prodOrderSt"))) {
                    String prodTp = data.getString("prodTp");
                    JSONArray bondLst = data.getJSONArray("bondLst");
                    if (orderUnionPay != null) {
                        orderUnionSendService.insertList(order.getNumber(), orderUnionPay.getProdTn(), prodTp, bondLst, certPath, YsfDistributionPropertiesUtils.getCertPwd(order.getPlatformKey()));
                    } else {
                        String prodTn = orderStatus.getString("prodTn");
                        orderUnionSendService.insertList(order.getNumber(), prodTn, prodTp, bondLst, certPath, YsfDistributionPropertiesUtils.getCertPwd(order.getPlatformKey()));
                        orderUnionPay = new OrderUnionPay();
                        orderUnionPay.setNumber(order.getNumber());
                        orderUnionPay.setOrderId(data.getString("orderId"));
                        orderUnionPay.setProdTn(prodTn);
                        orderUnionPay.setTxnTime(data.getString("txnTime"));
                        orderUnionPay.setUsrPayAmt("0");
                        orderUnionPayMapper.insertOrUpdate(orderUnionPay);
                        order.setExternalOrderNumber(prodTn);
                    }
                    order.setSendStatus("2");
                    order.setSendTime(DateUtil.date());
                } else if ("04".equals(orderStatus.getString("prodOrderSt"))) {
                    order.setSendStatus("3");
                } else if ("02".equals(orderStatus.getString("prodOrderSt"))) {
                    order.setSendStatus("1");
                }
            }
        } else {
            order.setSendStatus("3");
        }
    }

    /**
     * 美食订单退款回调
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void foodCancelCallBack(JSONObject data) {
        log.info("美食订单支付回调信息：{}", data);
        // 美食订单号
        String exNumber = data.getString("number");
        //退款状态
        String state = data.getString("state");
        //已退款份数
        BigDecimal amount = data.getBigDecimal("amount");
        if (ObjectUtil.isEmpty(exNumber)) {
            log.info("美食订单不存在,通知内容：{}", data);
            return;
        }
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getExternalOrderNumber, exNumber));
        if (ObjectUtil.isEmpty(order)) {
            //如果通知订单不存在 先查一下历史订单 都不存在才返回失败
            HistoryOrder historyOrder = historyOrderMapper.selectOne(new LambdaQueryWrapper<HistoryOrder>().eq(HistoryOrder::getExternalOrderNumber, exNumber));
            if (ObjectUtil.isEmpty(historyOrder)) {
                log.error("美食订单【{}】不存在,通知内容：{}", exNumber, data);
                return;
            }
            OrderFoodInfo orderFoodInfo = orderFoodInfoMapper.selectById(historyOrder.getNumber());
            if (ObjectUtil.isEmpty(orderFoodInfo)) {
                log.error("美食订单【{}】不存在,通知内容：{}", exNumber, data);
                return;
            }
            if ("8".equals(state)) {
                //退款成功
                historyOrder.setCancelStatus("1");
                historyOrder.setVerificationStatus("2");
                historyOrderMapper.updateById(historyOrder);
            }
            return;
        }
        OrderFoodInfo orderFoodInfo = orderFoodInfoMapper.selectById(order.getNumber());
        if (ObjectUtil.isEmpty(orderFoodInfo)) {
            log.error("美食订单【{}】不存在,通知内容：{}", exNumber, data);
            return;
        }
        if ("8".equals(state)) {
            //退款成功
            order.setCancelStatus("1");
            order.setVerificationStatus("2");
            order = updateOrder(order);
        }
    }

    /**
     * 查询产品发放额度
     *
     * @param productIds 产品ID
     * @return 发放额度
     */
    @Override
    public BigDecimal sumSendValueByProductIds(List<Long> productIds, Long userId) {
        if (ObjectUtil.isEmpty(productIds)) {
            return new BigDecimal("0");
        }
        LambdaQueryWrapper<Order> lqw = buildQuery(productIds, userId);
        lqw.isNotNull(Order::getExternalProductSendValue);
        return baseMapper.sumSendValue(lqw);
    }

    /**
     * 查询产品发放额度
     *
     * @param productIds 产品ID
     * @return 发放额度
     */
    @Override
    public BigDecimal sumOutAmountByProductIds(List<Long> productIds, Long userId) {
        if (ObjectUtil.isEmpty(productIds)) {
            return new BigDecimal("0");
        }
        LambdaQueryWrapper<Order> lqw = buildQuery(productIds, userId);
        lqw.isNotNull(Order::getOutAmount);
        return baseMapper.sumOutAmount(lqw);
    }

    @Override
    public void cloudRechargeCallback(CloudRechargeEntity huiguyunEntity) {
        CloudRechargeUtils.getData(huiguyunEntity);
        JSONObject data = JSONObject.parseObject(huiguyunEntity.getEncryptedData());
        // 查询订单是否存在
        OrderPushInfo orderPushInfo = getOrderPushInfo(data.getString("externalOrderNumber"));
        if (null == orderPushInfo) {
            throw new ServiceException("订单不存在");
        }
        Order order = baseMapper.selectById(orderPushInfo.getNumber());
        if (null == order) {
            throw new ServiceException("订单不存在");
        }
        rechargeResult(data, orderPushInfo, order);
        order = updateOrder(order);
        orderPushInfoMapper.updateById(orderPushInfo);
    }

    /**
     * 获取最后购买的产品订单
     *
     * @param productIds 产品ID集合 从这里面找
     * @param userId     用户ID
     * @return 最后购买的产品订单
     */
    @Override
    public OrderVo getLastOrder(List<Long> productIds, Long userId) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.in(Order::getProductId, productIds);
        lqw.eq(Order::getUserId, userId);
        lqw.eq(Order::getStatus, "2");
        lqw.last("order by create_time desc limit 1");
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 获取今日购买次数
     *
     * @param productId 产品ID
     * @param userId    用户ID
     * @return 最后购买的产品订单
     */
    public Long getDayOrderCount(Long productId, Long userId) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getProductId, productId);
        lqw.eq(Order::getUserId, userId);
        lqw.eq(Order::getStatus, "2");
        lqw.ge(Order::getCreateTime, DateUtil.beginOfDay(new Date()));
        return baseMapper.selectCount(lqw);
    }

    @Override
    public List<OrderAndUserNumber> queryUserAndOrderNum(Date startTime, Date endTime, Integer indexNum, Integer indexPage) {
        return baseMapper.queryUserAndOrderNum(startTime, endTime, indexNum, indexPage);
    }

    /**
     * 充值中心订单结果处理
     *
     * @param data          订单结果
     * @param orderPushInfo 订单请求信息
     * @param order         订单信息
     */
    private void rechargeResult(JSONObject data, OrderPushInfo orderPushInfo, Order order) {
        String state = data.getString("state");
        order.setExternalOrderNumber(data.getString("number"));
        orderPushInfo.setExternalOrderNumber(order.getExternalOrderNumber());
        if ("4".equals(state)) {
            orderPushInfo.setStatus("1");
            order.setSendStatus("2");
            // 如果有卡密信息 保存卡密信息
            orderCardService.save(data.getJSONArray("cards"), order.getNumber(), data.getString("usedType"));
        } else if ("5".equals(state)) {
            // 发放失败
            orderPushInfo.setStatus("2");
            orderPushInfo.setRemark(data.getString("remark"));
            order.setSendStatus("3");
            order.setFailReason(orderPushInfo.getRemark());
        }
    }

    /**
     * 充值中心订单结果处理 （银联分销）
     */
    private void rechargeUnionResult(JSONObject data, OrderPushInfo orderPushInfo, Order order) {
        if (data.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
            orderPushInfo.setExternalOrderNumber(order.getExternalOrderNumber());
            orderPushInfo.setStatus("1");
            order.setSendStatus("2");
        } else {
            // 发放失败
            orderPushInfo.setStatus("2");
            orderPushInfo.setRemark(data.getString("remark"));
            order.setSendStatus("3");
            order.setFailReason(orderPushInfo.getRemark());
        }
    }

    /**
     * 查询请求订单信息
     *
     * @param pushNumber 请求订单号
     * @return 请求订单信息
     */
    private OrderPushInfo getOrderPushInfo(String pushNumber) {
        return orderPushInfoMapper.selectOne(new LambdaQueryWrapper<OrderPushInfo>().eq(OrderPushInfo::getPushNumber, pushNumber));
    }

    private LambdaQueryWrapper<Order> buildQuery(List<Long> productIds, Long userId) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.in(Order::getProductId, productIds);
        lqw.eq(userId != null, Order::getUserId, userId);
        lqw.in(Order::getStatus, "2");
        return lqw;
    }

    /**
     * 订单支付成功处理
     *
     * @param collectiveOrder 订单信息
     * @param txnAmount       回调支付金额 单位：元
     * @param queryId         银联查询号
     * @param traceTime       交易传输时间
     * @param traceNo         系统跟踪号
     * @param txnTime         订单发送时间
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrder(CollectiveOrder collectiveOrder, BigDecimal txnAmount, String queryId, String traceTime, String traceNo, String txnTime, String issAddnData, String bankType) {
        // 验证金额是否一致
        txnAmount = txnAmount.divide(new BigDecimal("1"), 2, RoundingMode.HALF_UP);
        if (collectiveOrder.getWantAmount().compareTo(txnAmount) != 0) {
            log.info("订单【{}】支付金额不一致, 订单getWantAmount={}，支付回调amount={}", collectiveOrder.getCollectiveNumber(), collectiveOrder.getWantAmount(), txnAmount);
            return;
        }
        if (!"0".equals(collectiveOrder.getStatus()) && !"1".equals(collectiveOrder.getStatus())) {
            // 已经处理成功的，直接返回成功
            log.info("订单【{}】已处理不重复处理", collectiveOrder.getCollectiveNumber());
            return;
        }
        //默认交易成功
        collectiveOrder.setStatus("2");
        collectiveOrder.setOutAmount(txnAmount);
        collectiveOrderMapper.updateById(collectiveOrder);
        // 优惠券状态改变为已使用
        if (ObjectUtil.isNotEmpty(collectiveOrder.getCouponId())) {
            Coupon coupon = couponMapper.selectById(collectiveOrder.getCouponId());
            if (ObjectUtil.isNotEmpty(coupon) && !"3".equals(coupon.getUseStatus())) {
                coupon.setUseStatus("3");
                couponMapper.updateById(coupon);
            }
        }
        //查询小订单
        List<Order> orders = baseMapper.selectList(new LambdaQueryWrapper<Order>().eq(Order::getCollectiveNumber, collectiveOrder.getCollectiveNumber()));
        for (Order order : orders) {
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setNumber(order.getNumber());
            orderInfo.setQueryId(queryId);
            orderInfo.setTraceTime(traceTime);
            orderInfo.setTraceNo(traceNo);
            orderInfo.setTxnTime(txnTime);
            orderInfo.setTxnAmt(order.getWantAmount());
            orderInfo.setIssAddnData(issAddnData);
            orderInfo.setPayBankType(bankType);
            // 修改订单扩展信息
            orderInfoMapper.updateById(orderInfo);
            // 默认交易成功
            order.setOutAmount(order.getWantAmount());
            order.setStatus("2");
            if (null == order.getPayTime()) {
                order.setPayTime(DateUtils.getNowDate());
            }
            order = updateOrder(order);
            // 删除未支付订单缓存
            delCacheOrder(order.getNumber());
            // 发券
            //判断是否为随机产品 再发券
            if ("10".equals(order.getOrderType())) {
                checkRandomProduct(order);
            }
            // 判断是否有限定银行卡，如果有则退款
            boolean b = checkPayBank(bankType, order);
            if (b) {
                SpringUtils.context().publishEvent(new SendCouponEvent(order.getNumber(), order.getPlatformKey()));
            }
        }
    }

    private boolean checkPayBank(String payBank, Order order) {
        if (StringUtils.isBlank(payBank)) {
            return true;
        }
        ProductVo productVo = productService.queryById(order.getProductId());
        if (null != productVo && StringUtils.isNotBlank(productVo.getPayBankType()) && !"ALL".equalsIgnoreCase(productVo.getPayBankType())) {
            if (!productVo.getPayBankType().contains(payBank)) {
                log.info("订单{}，未使用指定支付方式，系统自动退款", order.getNumber());
                // 发起退款
                remoteOrderService.refundOrder(order.getNumber(), order.getOutAmount(), "未使用特定付款方式");
                // 回退名额
                callbackOrderCountCache(order.getPlatformKey(), order.getUserId(), order.getProductId(), order.getCreateTime(), order.getCount());
                return false;
            }
        }
        return true;
    }

    private R<Void> productPackageHandle(Long number, Long productId, Long userId, String adcode, String cityName, Long platformKey, String channel) {
        // 查询券包产品
        List<ProductPackageVo> productPackageVos = productPackageService.queryListByProductId(productId);
        if (ObjectUtil.isEmpty(productPackageVos)) {
            return R.fail("券包内产品未配置");
        }
        for (ProductPackageVo productPackageVo : productPackageVos) {
            // 查询已发订单
            LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
            lqw.eq(Order::getParentNumber, number);
            lqw.eq(Order::getStatus, "2");
            lqw.eq(Order::getProductId, productPackageVo.getExtProductId());
            Long count = baseMapper.selectCount(lqw);
            if (count > 0) {
                continue;
            }
            CreateOrderBo createOrderBo = new CreateOrderBo();
            createOrderBo.setProductId(productPackageVo.getExtProductId());
            createOrderBo.setUserId(userId);
            createOrderBo.setAdcode(adcode);
            createOrderBo.setCityName(cityName);
            createOrderBo.setParentNumber(number);
            createOrderBo.setPlatformKey(platformKey);
            createOrderBo.setPayCount(productPackageVo.getSendCount());
            createOrderBo.setChannel(channel);
            CreateOrderResult order;
            try {
                order = this.createOrder(createOrderBo, true);
            } catch (Exception e) {
                log.error("券包创建子订单异常：", e);
                return R.fail(e.getMessage());
            }
            if (null == order || !"0".equals(order.getStatus())) {
                return R.fail("发放失败，产品" + productPackageVo.getExtProductId() + "配置有误");
            }
            ThreadUtil.sleep(100);
        }
        return R.warn("处理成功");
    }

    /**
     * 处理订单发券状态
     */
    private JSONObject queryProductUnionSendHandle(Long number) {
        OrderUnionPay orderUnionPay = orderUnionPayMapper.selectById(number);
        if (orderUnionPay != null) {
            Order order = baseMapper.selectById(number);
            String orderStatusStr;
            String certPath;
            if ("12".equals(order.getOrderType()) || "1".equals(order.getUnionPay())) {
                String expid = "1".equals(order.getUnionPay()) ? order.getUnionProductId() : order.getExternalProductId();
                certPath = YsfDistributionPropertiesUtils.getCertPathJD(order.getPlatformKey());
                // 直销
                orderStatusStr = UnionPayDistributionUtil.orderStatus(UnionPayBizMethod.chnlpur.getBizMethod(), expid, orderUnionPay.getNumber(), orderUnionPay.getTxnTime(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJDAppId(order.getPlatformKey()), certPath);
            } else { // 代销
                certPath = YsfDistributionPropertiesUtils.getCertPathJC(order.getPlatformKey());
                orderStatusStr = UnionPayDistributionUtil.orderStatus(UnionPayBizMethod.CHNLPUR.getBizMethod(), order.getExternalProductId(), orderUnionPay.getNumber(), orderUnionPay.getTxnTime(), order.getPlatformKey(), YsfDistributionPropertiesUtils.getJCAppId(order.getPlatformKey()), certPath);
            }
            JSONObject orderStatus = JSONObject.parseObject(orderStatusStr);
            // 查询成功，开始处理。
            if (orderStatus.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
                // 发放成功
                if ("05".equals(orderStatus.getString("prodOrderSt"))) {
                    String prodTp = orderStatus.getString("prodTp");
                    JSONArray bondLst = orderStatus.getJSONArray("bondLst");
                    String prodTn = orderStatus.getString("prodTn");
                    orderUnionSendService.insertList(order.getNumber(), prodTn, prodTp, bondLst, certPath, YsfDistributionPropertiesUtils.getCertPwd(order.getPlatformKey()));
                    orderUnionPay = new OrderUnionPay();
                    orderUnionPay.setNumber(order.getNumber());
                    orderUnionPay.setOrderId(orderStatus.getString("orderId"));
                    orderUnionPay.setProdTn(prodTn);
                    orderUnionPay.setTxnTime(orderStatus.getString("txnTime"));
                    orderUnionPay.setUsrPayAmt("0");
                    orderUnionPayMapper.insertOrUpdate(orderUnionPay);
                    order.setExternalOrderNumber(prodTn);
                    order.setSendStatus("2");
                    order.setSendTime(DateUtil.date());
                } else if ("04".equals(orderStatus.getString("prodOrderSt"))) {
                    order.setSendStatus("3");
                } else if ("02".equals(orderStatus.getString("prodOrderSt"))) {
                    order.setSendStatus("1");
                }
            }
            return orderStatus;
        }
        return null;
    }

    private R<Void> queryProductPackageHandle(Long number, Long productId) {
        // 查询券包产品
        List<ProductPackageVo> productPackageVos = productPackageService.queryListByProductId(productId);
        if (ObjectUtil.isEmpty(productPackageVos)) {
            return R.fail("券包内产品未配置");
        }
        for (ProductPackageVo productPackageVo : productPackageVos) {
            // 查询已发订单
            LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
            lqw.eq(Order::getParentNumber, number);
            lqw.eq(Order::getStatus, "2");
            lqw.eq(Order::getProductId, productPackageVo.getExtProductId());
            lqw.eq(Order::getSendStatus, "3");
            Long count = baseMapper.selectCount(lqw);
            if (count > 0) {
                return R.fail("发放失败，具体原因请查询子订单");
            }
        }
        return R.ok("发放成功");
    }

    private void checkRandomProduct(Order order) {
        Long productId = order.getProductId();
        String productName = order.getProductName();
        ProductVo productVo = productService.queryById(productId);
        if ("10".equals(productVo.getProductType())) {
            //随机产品订单设置随机发放金额 (查询商品金额表)
            ProductAmountBo productAmountBo = new ProductAmountBo();
            productAmountBo.setProductId(productId);
            List<ProductAmountVo> productAmountVos = productAmountService.queryList(productAmountBo);
            if (ObjectUtil.isNotEmpty(productAmountVos)) {
                List<Double> drawProbability = productAmountVos.stream().map(item -> item.getDrawProbability().doubleValue()).collect(Collectors.toList());
                // 抽奖
                AliasMethod method = new AliasMethod(drawProbability);
                int index = method.next();
                // 奖品
                ProductAmountVo productAmountVo = productAmountVos.get(index);
                productName = productName + "(" + productAmountVo.getExternalProductSendValue() + "元)";
                Order updateOrder = new Order();
                updateOrder.setNumber(order.getNumber());
                updateOrder.setProductName(productName);
                updateOrder.setExternalProductSendValue(productAmountVo.getExternalProductSendValue());
                updateOrder(updateOrder);
            }
        }

    }

    /**
     * 查询多少天的订单列表
     *
     * @return 订单
     */
    @Override
    public TableDataInfo<Order> queryHistoryPageList(Integer day, PageQuery pageQuery) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.apply("create_time <= DATE_SUB(now(), INTERVAL " + day + " DAY)");
        Page<Order> result = baseMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<Order> buildQueryWrapper(OrderBo bo) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, Order::getProductId, bo.getProductId());
        lqw.eq(bo.getUserId() != null, Order::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getPickupMethod()), Order::getPickupMethod, bo.getPickupMethod());
        lqw.eq(StringUtils.isNotBlank(bo.getVerificationStatus()), Order::getVerificationStatus, bo.getVerificationStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderType()), Order::getOrderType, bo.getOrderType());
        if (StringUtils.isNotBlank(bo.getStatus())) {
            lqw.in(Order::getStatus, bo.getStatus().split(","));
        }
        lqw.eq(StringUtils.isNotBlank(bo.getSendStatus()), Order::getSendStatus, bo.getSendStatus());
        return lqw;
    }

    /**
     * 云闪付预警消息
     */
    @Override
    public void ysfForewarningMessage(Long platformId, String backendToken, String desc_Details, String template_Value) {
        try {
            String appId = ysfConfigService.queryValueByKey(platformId, "appId");
            String mpId = ysfConfigService.queryValueByKey(platformId, "mpId");
            String templateId = ysfConfigService.queryValueByKey(platformId, "templateId");
            String descDetails = ysfConfigService.queryValueByKey(platformId, desc_Details);
            String destType = ysfConfigService.queryValueByKey(platformId, "destType");
            String templateKey = ysfConfigService.queryValueByKey(platformId, "template_key");
            String templateValue = ysfConfigService.queryValueByKey(platformId, template_Value);
            String uiType = ysfConfigService.queryValueByKey(platformId, "uiType");
            String users = ysfConfigService.queryValueByKey(platformId, "users");
            String url = ysfConfigService.queryValueByKey(platformId, "sendMessageUrl");
            String[] userList = users.split(",");

            Map<String, Object> contentData = new HashMap<>();
            // 我方唯一标识
            contentData.put("appId", appId);
            // 临时token
            contentData.put("backendToken", backendToken);
            // 小程序id
            contentData.put("mpId", mpId);
            // 通知模板id
            contentData.put("templateId", templateId);
            // 通知详情
            contentData.put("desc", descDetails);
            contentData.put("uiType", uiType);
            // 跳转类型
            contentData.put("destType", destType);
            contentData.put("destInfo", contentData.get("mpId"));

            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("key", templateKey);
            map.put("value", templateValue);
            list.add(map);
            contentData.put("valueList", list);
            for (String phone : userList) {
                log.info("用户手机号：{}", phone);
                String openId = userService.getOpenIdByMobile(platformId, phone, PlatformEnumd.MP_YSF.getChannel());
                contentData.put("openId", openId);
                log.info("云闪付预警消息参数：{}", JSONObject.toJSONString(contentData));
                String s = HttpUtil.post(url, JSONObject.toJSONString(contentData));
                log.info("云闪付预警消息返回结果：{}", s);
            }
        } catch (Exception e) {
            log.info("云闪付预警异常：", e);
        }
    }

    /**
     * 根据订单状态查询订单数量
     */
    @Override
    public Long queryNumberByUserId(List<Long> userIds, String status, Integer type, Date dateTime) {
        if (type.equals(1)) {
            LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
            lqw.in(Order::getUserId, userIds);
            lqw.eq(Order::getStatus, status);
            // 昨天时间字段
            lqw.ge(Order::getCreateTime, DateUtil.beginOfDay(dateTime));
            lqw.le(Order::getCreateTime, DateUtil.endOfDay(dateTime));
            lqw.select(Order::getNumber);
            return baseMapper.selectCount(lqw);
        } else if (type.equals(2)) {
            QueryWrapper<Order> lqw = new QueryWrapper<>();
            lqw.select("DISTINCT user_id").lambda().in(Order::getUserId, userIds).eq(Order::getStatus, status).ge(Order::getCreateTime, DateUtil.beginOfDay(dateTime)).le(Order::getCreateTime, DateUtil.endOfDay(dateTime));
            return baseMapper.selectCount(lqw);
        }
        return 0L;
    }

    @Override
    public Long countByUserId(Long userId, Integer day) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getUserId, userId);
        if (null != day) {
            lqw.last("and create_time >= DATE_SUB(CURDATE(), INTERVAL " + day + " DAY)");
        }
        return baseMapper.selectCount(lqw);
    }

    /**
     * 查询订单信息
     *
     * @param externalOrderNumber 供应商订单号
     * @return 订单信息
     */
    public OrderVo queryByExternalOrderNumber(String externalOrderNumber) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getExternalOrderNumber, externalOrderNumber);
        lqw.last("order by number desc limit 1");
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 判断优惠券id是否存在于优惠券商品列表中
     */
    private Boolean isProductCoupon(Collection<Long> ids, Collection<Long> carProductIds) {
        for (Long id : carProductIds) {
            if (ids.contains(id)) {
                return true;
            }
        }
        return false;
    }

    private CollectiveOrder getCollectiveOrder(Long number) {
        if (null == number) {
            return null;
        }
        CollectiveOrder collectiveOrder = collectiveOrderMapper.selectById(number);
        if (null != collectiveOrder) {
            return collectiveOrder;
        }
        OrderVo orderVo = baseMapper.selectVoById(number);
        if (null == orderVo) {
            return null;
        }
        return collectiveOrderMapper.selectById(orderVo.getCollectiveNumber());
    }

    /**
     * 查询未核销的订单列表 根据订单类型
     *
     * @param orderTypeList 订单类型
     * @return 订单集合
     */
    public TableDataInfo<OrderVo> queryOrderByOrderTypeList(List<String> orderTypeList, Long userId, PageQuery pageQuery) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.in(Order::getOrderType, orderTypeList);
        lqw.eq(Order::getStatus, "2");
        lqw.eq(Order::getSendStatus, "2");
        lqw.eq(Order::getVerificationStatus, "0");
        lqw.eq(null != userId, Order::getUserId, userId);
        lqw.gt(Order::getUsedEndTime, DateUtil.offsetDay(new Date(), -3));
        Page<OrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询订单核销状态
     *
     * @param orderVo 订单信息
     */
    public void queryOrderUsedStatus(OrderVo orderVo) {
        if (null == orderVo || !orderVo.getVerificationStatus().equals("0") || !"2".equals(orderVo.getStatus()) || !"2".equals(orderVo.getSendStatus())) {
            return;
        }
        if ("18".equals(orderVo.getOrderType())) {
            if (StringUtils.isBlank(orderVo.getExternalOrderNumber())) {
                return;
            }
            List<String> activityNoList = new ArrayList<>();
            activityNoList.add(orderVo.getExternalProductId());
            String chnlId = ysfConfigService.queryValueByKey(orderVo.getPlatformKey(), YsfUpConstants.up_chnlId);
            String appId = ysfConfigService.queryValueByKey(orderVo.getPlatformKey(), YsfUpConstants.up_appId);
            String sm4Key = ysfConfigService.queryValueByKey(orderVo.getPlatformKey(), YsfUpConstants.up_sm4Key);
            String rsaPrivateKey = ysfConfigService.queryValueByKey(orderVo.getPlatformKey(), YsfUpConstants.up_rsaPrivateKey);
            String entityTp = ysfConfigService.queryValueByKey(orderVo.getPlatformKey(), YsfUpConstants.up_entityTp);
            R<JSONObject> result = YsfUtils.userCoupon(orderVo.getAccount(), activityNoList, entityTp, chnlId, appId, rsaPrivateKey, sm4Key);
            if (R.isSuccess(result)) {
                JSONObject data = result.getData();
                JSONObject params = data.getJSONObject("params");
                if (null != params) {
                    JSONArray activityInfoList = params.getJSONArray("activityInfoList");
                    if (null != activityInfoList) {
                        for (int i = 0; i < activityInfoList.size(); i++) {
                            UpUserCouponInfo upUserCouponInfo = activityInfoList.getObject(i, UpUserCouponInfo.class);
                            if (orderVo.getExternalOrderNumber().equals(upUserCouponInfo.getCouponCd())) {
                                // 已使用
                                boolean updateFlag = false;
                                Order order = new Order();
                                order.setNumber(orderVo.getNumber());
                                if ("6".equals(upUserCouponInfo.getAcctSt())) {
                                    order.setVerificationStatus("1");
                                    updateFlag = true;
                                } else if ("2".equals(upUserCouponInfo.getAcctSt())) {
                                    // 已销户 已删除
                                    order.setVerificationStatus("2");
                                    updateFlag = true;
                                } else if ("5".equals(upUserCouponInfo.getAcctSt())) {
                                    // 已失效
                                    order.setVerificationStatus("2");
                                    updateFlag = true;
                                } else if ("1".equals(upUserCouponInfo.getAcctSt())) {
                                    if (!"0".equals(orderVo.getVerificationStatus())) {
                                        // 已失效
                                        order.setVerificationStatus("0");
                                        updateFlag = true;
                                    }
                                }
                                if (updateFlag) {
                                    updateOrder(order);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 优惠券状态变更
     *
     * @param operTp    操作类型 01：优惠券承兑；02：优惠券返还；03：优惠券无操作；04：优惠券获取；05：优惠券删除；06：优惠券过期
     * @param transTp   交易类型 仅在operTp为01、02、03时出现，取值为01消费，31撤销，04退货
     * @param couponCd  优惠券编码
     * @param couponNum 优惠券变动数量
     */
    @Override
    public void upCouponStatusChange(String operTp, String transTp, String couponCd, String couponNum) {
        if ("04".equals(operTp)) {
            return;
        }
        // 查询订单是否存在
        OrderVo orderVo = queryByExternalOrderNumber(couponCd);
        if (null == orderVo) {
            throw new ServiceException("订单不存在");
        }

        String verificationStatus = null;
        if ("01".equals(operTp)) {
            // 核销
            verificationStatus = "1";
        } else if ("02".equals(operTp)) {
            // 优惠券返还
            verificationStatus = "0";
        } else if ("03".equals(operTp)) {
            // 优惠券无操作
            if ("04".equals(transTp)) {
                verificationStatus = "2";
            }
        } else if ("05".equals(operTp)) {
            // 优惠券删除
            verificationStatus = "2";
        } else if ("06".equals(operTp)) {
            // 优惠券过期
            verificationStatus = "2";
        } else {
            log.error("银联票券通知，类型无处理方式，couponCd={}，operTp={}", couponCd, operTp);
            return;
        }
        if (StringUtils.isBlank(verificationStatus)) {
            log.error("银联票券通知，状态为空，couponCd={}，operTp={}", couponCd, operTp);
            return;
        }
        Order order = new Order();
        order.setNumber(orderVo.getNumber());
        order.setVerificationStatus(verificationStatus);
        updateOrder(order);
    }

    @Override
    public TableDataInfo<OrderVo> getUnUseOrderList(OrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, Order::getProductId, bo.getProductId());
        lqw.eq(bo.getUserId() != null, Order::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getPickupMethod()), Order::getPickupMethod, bo.getPickupMethod());
        lqw.eq(StringUtils.isNotBlank(bo.getVerificationStatus()), Order::getVerificationStatus, bo.getVerificationStatus());
        if (StringUtils.isNotBlank(bo.getStatus())) {
            lqw.in(Order::getStatus, bo.getStatus().split(","));
        }
        if (StringUtils.isNotBlank(bo.getOrderType())) {
            lqw.in(Order::getOrderType, bo.getOrderType().split(","));
        }
        lqw.eq(StringUtils.isNotBlank(bo.getSendStatus()), Order::getSendStatus, bo.getSendStatus());
        Page<OrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }
}
