package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.GrabPeriodProduct;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodProductVo;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.mapper.GrabPeriodMapper;
import com.ruoyi.zlyyh.mapper.GrabPeriodProductMapper;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.GrabPeriodProductQueryBo;
import com.ruoyi.zlyyhmobile.domain.vo.AppProductVo;
import com.ruoyi.zlyyhmobile.service.IGrabPeriodService;
import com.ruoyi.zlyyhmobile.service.IProductService;
import com.ruoyi.zlyyhmobile.utils.ProductUtils;
import com.ruoyi.zlyyhmobile.utils.redis.OrderCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 秒杀配置Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GrabPeriodServiceImpl implements IGrabPeriodService {

    private final GrabPeriodMapper baseMapper;
    private final GrabPeriodProductMapper grabPeriodProductMapper;
    private final IProductService productService;

    /**
     * 查询秒杀配置
     */
    @Cacheable(cacheNames = CacheNames.GRAB_PERIOD, key = "#id")
    @Override
    public GrabPeriodVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询秒杀商品
     *
     * @return 产品接口
     */
    @Override
    public List<AppProductVo> getProductList(GrabPeriodProductQueryBo bo) {
        if (null == bo.getGrabPeriodId()) {
            return new ArrayList<>(0);
        }
        GrabPeriodVo grabPeriodVo = CacheUtils.get(CacheNames.GRAB_PERIOD, bo.getGrabPeriodId());
        if (null == grabPeriodVo) {
            grabPeriodVo = this.queryById(bo.getGrabPeriodId());
            CacheUtils.put(CacheNames.GRAB_PERIOD, bo.getGrabPeriodId(), grabPeriodVo);
        }
        if (null == grabPeriodVo || !"0".equals(grabPeriodVo.getStatus())) {
            return new ArrayList<>(0);
        }
        String cityCode = ServletUtils.getHeader(ZlyyhConstants.CITY_CODE);
        List<GrabPeriodProductVo> grabPeriodProductVos = CacheUtils.get(CacheNames.GRAB_PERIOD_PRODUCT, bo.getGrabPeriodId());
        if (ObjectUtil.isEmpty(grabPeriodProductVos)) {
            LambdaQueryWrapper<GrabPeriodProduct> lqw = Wrappers.lambdaQuery();
            lqw.select(GrabPeriodProduct::getProductId, GrabPeriodProduct::getSort)
                .eq(GrabPeriodProduct::getGrabPeriodId, bo.getGrabPeriodId());
            grabPeriodProductVos = grabPeriodProductMapper.selectVoList(lqw);
            CacheUtils.put(CacheNames.GRAB_PERIOD_PRODUCT, bo.getGrabPeriodId(), grabPeriodProductVos);
        }
        if (ObjectUtil.isNotEmpty(grabPeriodProductVos)) {
            Long userId = null;
            try {
                userId = LoginHelper.getUserId();
            } catch (Exception ignored) {
            }
            Map<Long, Long> collect = grabPeriodProductVos.stream().collect(HashMap::new, (m, v) -> m.put(v.getProductId(), Optional.ofNullable(v.getSort()).orElse(99L)), HashMap::putAll);
            List<ProductVo> productVos = productService.queryGrabPeriodProduct((Set) collect.keySet(), cityCode, bo.getWeekDate(), ZlyyhUtils.getPlatformId());
            List<AppProductVo> appProductVos = BeanCopyUtils.copyList(productVos, AppProductVo.class);
            boolean dayFlag = StringUtils.isBlank(bo.getWeekDate()) || bo.getWeekDate().equals("" + DateUtil.dayOfWeek(new Date()));
            for (AppProductVo appProductVo : appProductVos) {
                appProductVo.setSort(collect.get(appProductVo.getProductId()));
                if (dayFlag) {
                    // 校验是否抢完
                    R<ProductVo> checkProductCountResult = ProductUtils.checkProduct(appProductVo, cityCode);
                    appProductVo = (AppProductVo) checkProductCountResult.getData();
                    // 商品根据用户过滤处理
                    if ("0".equals(appProductVo.getProductStatus()) && null != userId && userId > 0) {
                        // 校验是否有订单，有订单直接返回
                        String cacheObject = RedisUtils.getCacheObject(OrderCacheUtils.getUsreOrderOneCacheKey(ZlyyhUtils.getPlatformId(), userId, appProductVo.getProductId()));
                        ProductUtils.checkOrder(userId, appProductVo, cacheObject);
                    }
                }
            }
            // 排序 用户纬度 正序 产品纬度 正序 产品本身排序
            appProductVos.sort(Comparator.comparing(AppProductVo::getUserProductStatus)
                .thenComparing(AppProductVo::getProductStatus)
                .thenComparing(AppProductVo::getSort));
            return appProductVos;
        }
        return new ArrayList<>(0);
    }

    public static void main(String[] args) {
//        String url = "https://open.95516.com/open/access/1.0/activity.quota";
//        String backendToken = "06c5f5302000005c1B9dolbJ";
//        String activityType = "6";
//        String activityNo = "3102023033068463";
//        String transSeq = IdUtil.getSnowflakeNextIdStr();
//
//        String appId = "5d6630e7212a456e82f8d6a495faaec7";
//        String reqTs = DateUtils.createTimestampStr(false);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("appId", appId);
//        params.put("activityNo", activityNo);
//        params.put("activityType", activityType);
//        params.put("transSeqId", transSeq);
//        params.put("backendToken", backendToken);
//
//        String body = HttpUtil.createPost(url).body(JSONObject.toJSONString(params)).execute().body();
//        System.out.println(body);
    }
}
