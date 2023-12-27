package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.Browse;
import com.ruoyi.zlyyh.domain.bo.BrowseBo;
import com.ruoyi.zlyyh.domain.vo.BrowseVo;
import com.ruoyi.zlyyh.mapper.BrowseMapper;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.service.IBrowseService;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 浏览任务Service业务层处理
 *
 * @author yzg
 * @date 2023-12-14
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BrowseServiceImpl implements IBrowseService {

    private final BrowseMapper baseMapper;
    private final IOrderService orderService;
    private final LockTemplate lockTemplate;

    @Override
    @Cacheable(cacheNames = CacheNames.BROWSEVO, key = "#browseId")
    public BrowseVo queryById(Long browseId) {
        return baseMapper.selectVoById(browseId);
    }

    /**
     * 查询浏览任务列表
     */
    @Cacheable(cacheNames = CacheNames.BROWSELIST, key = "#bo.getPlatformKey()+'-'+#bo.getSupportChannel()")
    @Override
    public List<BrowseVo> queryList(BrowseBo bo) {
        LambdaQueryWrapper<Browse> lqw = Wrappers.lambdaQuery();
        lqw.eq(Browse::getStatus, "0");
        lqw.and(lm -> {
            lm.isNull(Browse::getStartTime).or().lt(Browse::getStartTime, new Date());
        });
        lqw.and(lm -> {
            lm.isNull(Browse::getEndTime).or().gt(Browse::getEndTime, new Date());
        });
        lqw.eq(Browse::getPlatformKey, bo.getPlatformKey());
        if (StringUtils.isNotBlank(bo.getSupportChannel())) {
            lqw.and(lm -> {
                lm.eq(Browse::getSupportChannel, "ALL").or().like(Browse::getSupportChannel, bo.getSupportChannel());
            });
        }
        lqw.last("order by sort asc");
        return baseMapper.selectVoList(lqw);
    }

    public void sendReward(Long userId, BrowseVo browseVo, String adCode, String cityName, String channel) {
        String key = "browseSendReward:" + browseVo.getBrowseId() + ":" + userId;
        final LockInfo lockInfo = lockTemplate.lock("lock" + key, 30000L, 1000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            return;
        }
        // 获取锁成功，处理业务
        try {
            if (!queryUserWhetherToParticipateToday(userId, browseVo)) {
                return;
            }
            // 生成订单发放奖励
            CreateOrderBo createOrderBo = new CreateOrderBo();
            createOrderBo.setProductId(browseVo.getProductId());
            createOrderBo.setUserId(userId);
            createOrderBo.setAdcode(adCode);
            createOrderBo.setCityName(cityName);
            createOrderBo.setPlatformKey(browseVo.getPlatformKey());
            createOrderBo.setChannel(channel);
            try {
                CreateOrderResult order = orderService.createOrder(createOrderBo, true);
                // 缓存今日已参与
                String cacheKey = getCacheUserWhetherToParticipateTodayKey(userId, browseVo.getBrowseId());
                RedisUtils.setCacheObject(cacheKey, order.getNumber());
            } catch (Exception e) {
                log.info("浏览任务奖励发放失败，用户Id：{}，任务Id：{}，失败信息：{}", userId, browseVo.getBrowseId(), e.getMessage());
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 查询用户今日是否可参与浏览任务
     *
     * @param userId   用户ID
     * @param browseVo 浏览任务
     * @return true 可参与，false 不可参与
     */
    public boolean queryUserWhetherToParticipateToday(Long userId, BrowseVo browseVo) {
        String cacheKey = getCacheUserWhetherToParticipateTodayKey(userId, browseVo.getBrowseId());
        Long number = RedisUtils.getCacheObject(cacheKey);
        if (null != number) {
            return false;
        }
        if (null == browseVo.getProductId()) {
            return false;
        }
        Long dayOrderCount = orderService.getDayOrderCount(browseVo.getProductId(), userId);
        return dayOrderCount <= 0;
    }

    private String getCacheUserWhetherToParticipateTodayKey(Long userId, Long browseId) {
        return "browseUserDay:" + browseId + ":" + userId + ":" + DateUtils.getDate();
    }
}
