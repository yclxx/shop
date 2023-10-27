package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.zlyyh.domain.Action;
import com.ruoyi.zlyyh.domain.Coupon;
import com.ruoyi.zlyyh.domain.Market;
import com.ruoyi.zlyyh.domain.MarketLog;
import com.ruoyi.zlyyh.domain.bo.ActionBo;
import com.ruoyi.zlyyh.domain.bo.MarketBo;
import com.ruoyi.zlyyh.domain.vo.MarketVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.ActionMapper;
import com.ruoyi.zlyyh.mapper.MarketLogMapper;
import com.ruoyi.zlyyh.mapper.MarketMapper;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.service.IActionService;
import com.ruoyi.zlyyhmobile.service.IMarketService;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class MarketServiceImpl implements IMarketService {
    private final MarketMapper baseMapper;
    private final MarketLogMapper marketLogMapper;
    private final ActionMapper actionMapper;
    private final IActionService actionService;
    private final IUserService userService;
    private final IOrderService orderService;

    /**
     * 查看平台新用户营销信息(默认获取最新一条的数据)
     */
    @Cacheable(cacheNames = CacheNames.marketInfo, key = "#bo.getPlatformKey()+'-'+#bo.getSupportChannel()")
    @Override
    public MarketVo queryMarketVo(MarketBo bo) {
        LambdaQueryWrapper<Market> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoOne(lqw);
    }

    @Cacheable(cacheNames = CacheNames.marketLog, key = "#bo.getPlatformKey()+'-'+#userId")
    @Override
    public MarketLog queryMarketLogVo(MarketBo bo, Long userId) {
        return this.buildMarketLogQuery(bo, userId);
    }

    @Override
    public MarketLog insertUserMarket(MarketBo bo, Long userId) {
        if (ObjectUtil.isEmpty(bo) || ObjectUtil.isEmpty(userId)) throw new ServiceException("奖励领取失败");
        Market market = baseMapper.selectById(bo.getMarketId());
        if (ObjectUtil.isEmpty(market)) throw new ServiceException("活动已结束");

        // 判断用户是否已经领取奖励
        MarketLog marketLog = this.buildMarketLogQuery(bo, userId);
        // 记录不为空，并且状态为没有被领取
        if (ObjectUtil.isNotEmpty(marketLog) && !marketLog.getStatus().equals("0")) {
            throw new ServiceException("你已领取奖励");
        }
        // 获取当前时间
        Date nowDate = DateUtils.getNowDate();
        // 判断活动时间
        if (DateUtils.compare(nowDate, market.getBeginTime()) < 0) throw new ServiceException("活动未开始");
        if (DateUtils.compare(nowDate, market.getEndTime()) > 0) throw new ServiceException("活动已结束");

        UserVo userVo = userService.queryById(userId, bo.getSupportChannel());
        if (ObjectUtil.isEmpty(userVo)) throw new ServiceException("未查到你的用户信息");
        // 判断上次登录时间是否小于指定时间
        if (DateUtils.compare(userVo.getLastLoginDate(), market.getDateSpecific()) < 0) {
            if (market.getRewardType().equals("2")) {
                // 优惠券
                Action action = actionMapper.selectById(market.getActionId());
                ActionBo actionBo = new ActionBo();
                actionBo.setActionNo(action.getActionNo());
                // 领取优惠券
                Coupon coupon = actionService.insertUserCoupon(actionBo, userId);
                if (ObjectUtil.isNotEmpty(coupon)) {
                    if (ObjectUtil.isEmpty(marketLog)) {
                        marketLog = this.buildMarketLog(market, userId);
                    }
                    marketLog.setCouponId(coupon.getCouponId());
                    marketLog.setStatus("1");
                    marketLogMapper.updateById(marketLog);
                    // 清除缓存
                    String key = market.getPlatformKey() + "-" + userId;
                    CacheUtils.evict(CacheNames.marketLog, key);
                    return marketLog;
                }
            } else if (market.getRewardType().equals("3")) {
                // 商品
                CreateOrderBo createOrderBo = new CreateOrderBo();
                createOrderBo.setProductId(market.getProductId());
                createOrderBo.setUserId(userId);
                createOrderBo.setAdcode(ZlyyhUtils.getAdCode());
                createOrderBo.setCityName(ZlyyhUtils.getCityName());
                createOrderBo.setPlatformKey(bo.getPlatformKey());
                CreateOrderResult order = orderService.createOrder(createOrderBo, true);
                if (ObjectUtil.isNotEmpty(order)) {
                    if (ObjectUtil.isEmpty(marketLog)) {
                        marketLog = this.buildMarketLog(market, userId);
                    }
                    marketLog.setCouponId(market.getProductId());
                    marketLog.setStatus("2");
                    marketLogMapper.updateById(marketLog);
                    // 清除缓存
                    String key = market.getPlatformKey() + "-" + userId;
                    CacheUtils.evict(CacheNames.marketLog, key);
                    return marketLog;
                }
            } else {
                throw new ServiceException("领取失败");
            }
        } else {
            throw new ServiceException("你不符合活动规则");
        }
        return null;
    }

    private MarketLog buildMarketLogQuery(MarketBo bo, Long userId) {
        LambdaQueryWrapper<MarketLog> logLambda = Wrappers.lambdaQuery();
        logLambda.eq(bo.getMarketId() != null, MarketLog::getMarketId, bo.getMarketId());
        logLambda.eq(bo.getPlatformKey() != null, MarketLog::getPlatformKey, bo.getPlatformKey());
        logLambda.eq(MarketLog::getUserId, userId);
        return marketLogMapper.selectOne(logLambda);
    }

    private LambdaQueryWrapper<Market> buildQueryWrapper(MarketBo bo) {
        LambdaQueryWrapper<Market> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getMarketId() != null, Market::getMarketId, bo.getMarketId());
        lqw.eq(bo.getPlatformKey() != null, Market::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotEmpty(bo.getStatus()), Market::getStatus, bo.getStatus());
        Date nowTime = DateUtils.getNowDate();
        lqw.le(Market::getBeginTime, nowTime);
        lqw.ge(Market::getEndTime, nowTime);
        if (StringUtils.isNotBlank(bo.getSupportChannel())) {
            lqw.and(lm -> {
                lm.eq(Market::getSupportChannel, "ALL").or().likeRight(Market::getSupportChannel, bo.getSupportChannel());
            });
        }
        lqw.last("order by create_time desc limit 1");
        return lqw;
    }

    /**
     * 新增奖励领取记录
     */
    private MarketLog buildMarketLog(Market market, Long userId) {
        MarketLog marketLog = new MarketLog();
        marketLog.setLogId(IdUtil.getSnowflakeNextId());
        marketLog.setUserId(userId);
        marketLog.setPlatformKey(market.getPlatformKey());
        marketLog.setMarketId(market.getMarketId());
        marketLog.setRewardType(market.getRewardType());
        marketLog.setSupportChannel(market.getSupportChannel());
        marketLog.setReceiveDate(DateUtils.getNowDate());
        marketLog.setStatus("0");
        marketLogMapper.insert(marketLog);
        return marketLog;
    }
}
