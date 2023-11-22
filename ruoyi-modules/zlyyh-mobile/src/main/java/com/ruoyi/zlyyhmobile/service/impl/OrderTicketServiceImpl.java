package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.user.UserException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderTicketBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.event.SendCouponEvent;
import com.ruoyi.zlyyhmobile.event.ShareOrderEvent;
import com.ruoyi.zlyyhmobile.service.*;
import com.ruoyi.zlyyhmobile.utils.redis.OrderCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderTicketServiceImpl implements OrderTicketService {
    private final OrderMapper orderMapper;
    private final IPlatformService platformService;
    private final IProductService productService;
    private final IUserService userService;
    private final OrderTicketMapper baseMapper;
    private final IUserAddressService userAddressService;
    private final IShopService shopService;
    private final IUserIdcardService userIdcardService;
    private final IHistoryOrderService historyOrderService;

    private final OrderIdcardMapper orderIdcardMapper;
    private final ProductTicketMapper ticketMapper;
    private final ProductTicketSessionMapper ticketSessionMapper;
    private final ProductTicketLineMapper ticketLineMapper;
    private final CodeMapper codeMapper;
    private final IUnionPayChannelService unionPayChannelService;
    private final CollectiveOrderMapper collectiveOrderMapper;

    /**
     * 创建订单
     *
     * @param bo
     * @return
     */
    @Override
    public CreateOrderResult createTicketOrder(CreateOrderTicketBo bo) {
        PlatformVo platformVo = platformService.queryById(bo.getPlatformKey(), ZlyyhUtils.getPlatformChannel());
        if (null == platformVo) {
            throw new ServiceException("请求失败，请退出重试");
        }
        // 校验城市
        //if (StringUtils.isBlank(bo.getCityCode())) {
        //    throw new ServiceException("未获取到您的位置信息,请确认是否开启定位服务");
        //}
        //if (StringUtils.isNotBlank(platformVo.getPlatformCity()) && !"ALL".equalsIgnoreCase(platformVo.getPlatformCity()) && !platformVo.getPlatformCity().contains(bo.getCityCode())) {
        //    throw new ServiceException("您当前所在位置不在观影地址参与范围!");
        //}
        // 校验是否有订单，有订单直接返回
        String cacheObject = RedisUtils.getCacheObject(OrderCacheUtils.getUsreOrderOneCacheKey(platformVo.getPlatformKey(), bo.getUserId(), bo.getLineId()));
        if (StringUtils.isNotBlank(cacheObject)) {
            Order order = RedisUtils.getCacheObject(cacheObject);
            if (null != order && "0".equals(order.getStatus())) {
                return new CreateOrderResult(order.getCollectiveNumber(), order.getNumber(), "1");
            }
        }
        UserVo userVo = userService.queryById(bo.getUserId(), ZlyyhUtils.getPlatformChannel());
        if (null == userVo || "0".equals(userVo.getReloadUser()) || StringUtils.isBlank(userVo.getMobile())) {
            throw new ServiceException("登录超时，请退出重试[user]", HttpStatus.HTTP_UNAUTHORIZED);
        }
        if (!platformVo.getPlatformKey().equals(userVo.getPlatformKey())) {
            throw new ServiceException("登录超时，请退出重试[platform]", HttpStatus.HTTP_UNAUTHORIZED);
        }
        if ("1".equals(userVo.getStatus())) {
            throw new UserException("user.blocked", userVo.getMobile());
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

        // 票种查询
        ProductTicketLineVo ticketLineVo = ticketLineMapper.selectVoById(bo.getLineId());
        if (ObjectUtil.isEmpty(ticketLineVo)) throw new ServiceException("此票档无法购买");
        if (ticketLineVo.getLineUpperLimit() < bo.getPayCount())
            throw new ServiceException("单次购买数量不能超过" + ticketLineVo.getLineUpperLimit());

        String lineNumber = "lineNumber:";
        // 查询已购商品数量缓存
        Long lineNumbers = RedisUtils.getCacheObject(lineNumber + bo.getLineId());
        if (ObjectUtil.isNotEmpty(lineNumbers)) {
            // 总数量减去已购数量，获得剩余可购数量
            Long s = ticketLineVo.getLineNumber() - lineNumbers;
            if (s.compareTo(bo.getPayCount()) < 0) {
                throw new ServiceException("票档所剩数量不足");
            }
        } else {
            lineNumbers = 0L;
            // 产品已购买数量
            BigDecimal orderTicketLineNumber = baseMapper.getOrderTicketLineNumber(ticketLineVo.getLineId());
            if (null == orderTicketLineNumber) orderTicketLineNumber = BigDecimal.ZERO;
            // 计算总购买数量
            long payCount = orderTicketLineNumber.longValue() + bo.getPayCount();
            if (ticketLineVo.getLineNumber() < payCount)
                throw new ServiceException("票档所剩数量不足");
            // 缓存异构数量
        }

        // 门店信息处理
        ShopVo shopVo = shopService.queryById(bo.getShopId());
        if (ObjectUtil.isEmpty(shopVo)) throw new ServiceException("观影信息异常");
        if (!shopVo.getStatus().equals("0")) throw new ServiceException("观影地址已失效");
        // 演出票信息查询
        ProductTicket ticket = ticketMapper.selectVoByProductId(ticketLineVo.getProductId());
        ProductTicketSessionVo ticketSession = ticketSessionMapper.selectVoById(ticketLineVo.getSessionId());

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
        order.setNumber(IdUtil.getSnowflakeNextId());
        order.setCollectiveNumber(collectiveOrder.getCollectiveNumber());
        order.setProductId(productVo.getProductId());
        order.setCusRefund(productVo.getCusRefund());
        order.setUserId(bo.getUserId());
        order.setCount(bo.getPayCount());
        order.setProductName(productVo.getProductName());
        order.setProductImg(productVo.getProductImg());
        order.setPickupMethod(productVo.getPickupMethod());
        order.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        order.setExpireDate(collectiveOrder.getExpireDate());
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
        // order.setParentNumber(bo.getParentNumber());
        order.setUsedStartTime(productVo.getUsedStartTime());
        order.setUsedEndTime(productVo.getUsedEndTime());
        order.setUnionPay(productVo.getUnionPay());
        order.setUnionProductId(productVo.getUnionProductId());
        order.setSysDeptId(productVo.getSysDeptId());
        order.setSysUserId(productVo.getSysUserId());
        // 第三方订单号
        order.setExternalProductId(ticketLineVo.getOtherId());
        order.setUnionProductId(ticketLineVo.getOtherId());
        // 场次与票档保存
        order.setProductSessionId(ticketSession.getSessionId());
        order.setProductSessionName(ticketSession.getSession());
        order.setProductSkuId(ticketLineVo.getLineId());
        order.setProductSkuName(ticketLineVo.getLineTitle());

        OrderTicket orderTicket = new OrderTicket();
        orderTicket.setNumber(order.getNumber());
        orderTicket.setMobile(userVo.getMobile());
        orderTicket.setProductId(ticketLineVo.getProductId());
        orderTicket.setSessionId(ticketLineVo.getSessionId());
        orderTicket.setLineId(ticketLineVo.getLineId());
        if (ObjectUtil.isNotEmpty(ticketSession.getDate())) {
            orderTicket.setTicketTime(ticketSession.getDate());
        }
        orderTicket.setPrice(ticketLineVo.getLinePrice());
        orderTicket.setSellPrice(ticketLineVo.getLineSettlePrice());
        orderTicket.setCount(bo.getPayCount());
        orderTicket.setTicketNonsupport(ticket.getTicketNonsupport());
        orderTicket.setTicketInvoice(ticket.getTicketInvoice());
        orderTicket.setTicketExpired(ticket.getTicketExpired());
        orderTicket.setTicketInvoice(ticket.getTicketInvoice());
        orderTicket.setTicketAnyTime(ticket.getTicketAnyTime());
        orderTicket.setTicketChooseSeat(ticket.getTicketChooseSeat());
        orderTicket.setShopId(shopVo.getShopId());
        orderTicket.setShopName(shopVo.getShopName());
        orderTicket.setShopAddress(shopVo.getAddress());
        orderTicket.setTicketPostWay(ticket.getTicketPostWay());
        orderTicket.setTicketPostage(ticket.getTicketPostage());
        orderTicket.setTicketForm(ticket.getTicketForm());
        orderTicket.setProductName(productVo.getProductName());
        orderTicket.setSessionName(ticketSession.getSession());
        orderTicket.setLineName(ticketLineVo.getLineTitle());
        // 设置预约时间
        if (ticketSession.getIsRange().equals("0")) {
            if (StringUtils.isEmpty(bo.getReservation())) throw new ServiceException("请选择预约时间");
            orderTicket.setReservation(bo.getReservation());
        }

        // 票形式 实体票处理地址信息
        if (ticket.getTicketForm().equals("2")) {
            UserAddressVo userAddressVo = userAddressService.queryById(bo.getAddressId());
            if (ObjectUtil.isEmpty(userAddressVo)) throw new ServiceException("请选择收货地址");
            orderTicket.setUserAddressId(userAddressVo.getUserAddressId());
            orderTicket.setName(userAddressVo.getName());
            orderTicket.setTel(userAddressVo.getTel());
            orderTicket.setAddress(userAddressVo.getAddress());
            orderTicket.setAddressInfo(userAddressVo.getAddressInfo());
        }

        // 价格处理
        // 总金额
        order.setTotalAmount(orderTicket.getPrice().multiply(BigDecimal.valueOf(bo.getPayCount())));
        // 需支付金额
        order.setWantAmount(orderTicket.getSellPrice().multiply(BigDecimal.valueOf(bo.getPayCount())));
        // 实际支付金额
        order.setOutAmount(orderTicket.getSellPrice().multiply(BigDecimal.valueOf(bo.getPayCount())));
        collectiveOrder.setTotalAmount(order.getTotalAmount());
        collectiveOrder.setReducedPrice(order.getReducedPrice());
        collectiveOrder.setWantAmount(order.getWantAmount());
        collectiveOrder.setOutAmount(order.getOutAmount());
        // 快递方式处理
        if (ticket.getTicketPostWay().equals("2")) {
            order.setTotalAmount(order.getTotalAmount().add(ticket.getTicketPostage()));
            order.setWantAmount(order.getWantAmount().add(ticket.getTicketPostage()));
            order.setOutAmount(order.getOutAmount().add(ticket.getTicketPostage()));
        }
        // 优惠金额
        order.setReducedPrice(order.getTotalAmount().subtract(order.getWantAmount()));

        // 身份信息 必填处理
        if (ticket.getTicketCard().equals("0")) {
            if (null == bo.getCardList() || bo.getCardList().isEmpty()) throw new ServiceException("请选择观影人信息");
            int size = bo.getCardList().size();
            if (!bo.getPayCount().equals((long) size)) throw new ServiceException("观影人数必须与购票数量一致");
            List<UserIdcardVo> userIdCardVos = userIdcardService.queryListByIds(bo.getCardList());
            List<OrderIdcard> orderCardList = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(userIdCardVos)) {
                userIdCardVos.forEach(o -> {
                    OrderIdcard idCard = new OrderIdcard();
                    idCard.setOrderCardId(IdUtil.getSnowflakeNextId());
                    idCard.setNumber(order.getNumber());
                    idCard.setOrderType(order.getOrderType());
                    idCard.setName(o.getName());
                    idCard.setIdCard(o.getIdCard());
                    orderCardList.add(idCard);
                });
            }
            if (!orderCardList.isEmpty()) {
                orderIdcardMapper.insertBatch(orderCardList);
            }
        }

        if ("0".equals(productVo.getPickupMethod())) {
            order.setStatus("2");
            order.setPayTime(new Date());
            SpringUtils.context().publishEvent(new SendCouponEvent(order.getNumber(), order.getPlatformKey()));
            orderMapper.insert(order);
            baseMapper.insert(orderTicket);
            collectiveOrder.setStatus("2");
            collectiveOrderMapper.insert(collectiveOrder);
            // 分销处理
            SpringUtils.context().publishEvent(new ShareOrderEvent(bo.getShareUserId(), order.getNumber()));
            return new CreateOrderResult(collectiveOrder.getCollectiveNumber(), order.getNumber(), "1");
        }

        // 银联分销预下单处理
        if ("1".equals(productVo.getUnionPay())) {
            String externalProductId = ticketLineVo.getOtherId();
            if (StringUtils.isEmpty(externalProductId)) {
                throw new ServiceException("抱歉，商品配置错误[expid]");
            }
            unionPayChannelService.createUnionPayOrder(externalProductId, order);
        }
        // 向 redis 缓存设置已购买数量
        RedisUtils.setCacheObject(lineNumber + bo.getLineId(), lineNumbers + bo.getPayCount(), Duration.ofDays(1));
        orderMapper.insert(order);
        baseMapper.insert(orderTicket);

        collectiveOrderMapper.insert(collectiveOrder);
        collectiveOrder = collectiveOrderMapper.selectById(collectiveOrder.getCollectiveNumber());
        // 缓存订单数据
        cacheOrder(order);
        // 分销处理
        SpringUtils.context().publishEvent(new ShareOrderEvent(bo.getShareUserId(), order.getNumber()));
        return new CreateOrderResult(collectiveOrder.getCollectiveNumber(), order.getNumber(), "1");
    }

    /**
     * 缓存用户未支付订单
     *
     * @param order 订单信息
     */
    private void cacheOrder(Order order) {
        String orderCacheKey = "orders:" + order.getNumber();
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

    @Override
    public OrderTicketVo orderInfo(Long number) {
        Order order = orderMapper.selectById(number);
        if (null == order) {
            throw new ServiceException("无此订单数据");
        }
        OrderTicketVo orderTicketVo = baseMapper.selectVoById(number);

        LambdaQueryWrapper<OrderIdcard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderIdcard::getNumber, number);
        queryWrapper.eq(OrderIdcard::getOrderType, "13");
        List<OrderIdcardVo> orderIdCardVos = orderIdcardMapper.selectVoList(queryWrapper);
        orderTicketVo.setOrderIdCardVos(orderIdCardVos);
        orderTicketVo.setOrder(order);

        if (orderTicketVo.getTicketForm().equals("1")) {
            LambdaQueryWrapper<Code> queryCode = new LambdaQueryWrapper<>();
            queryCode.eq(Code::getNumber, number);
            List<CodeVo> codeVos = codeMapper.selectVoList(queryCode);
            if (ObjectUtil.isNotEmpty(codeVos)) {
                orderTicketVo.setCodeVos(codeVos);
            }
        }
        return orderTicketVo;
    }

    @Override
    public OrderTicketVo getHistoryOrderInfo(Long number) {
        HistoryOrderVo historyOrderVo = historyOrderService.queryById(number);
        if (null == historyOrderVo) {
            throw new ServiceException("无此订单数据");
        }
        OrderTicketVo orderTicketVo = baseMapper.selectVoById(number);

        LambdaQueryWrapper<OrderIdcard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderIdcard::getNumber, number);
        queryWrapper.eq(OrderIdcard::getOrderType, "13");
        List<OrderIdcardVo> orderIdCardVos = orderIdcardMapper.selectVoList(queryWrapper);
        orderTicketVo.setOrderIdCardVos(orderIdCardVos);
        Order order = BeanCopyUtils.copy(historyOrderVo, Order.class);
        orderTicketVo.setOrder(order);

        if (orderTicketVo.getTicketForm().equals("1")) {
            LambdaQueryWrapper<Code> queryCode = new LambdaQueryWrapper<>();
            queryCode.eq(Code::getNumber, number);
            List<CodeVo> codeVos = codeMapper.selectVoList(queryCode);
            if (ObjectUtil.isNotEmpty(codeVos)) {
                orderTicketVo.setCodeVos(codeVos);

            }
        }
        return orderTicketVo;
    }
}
