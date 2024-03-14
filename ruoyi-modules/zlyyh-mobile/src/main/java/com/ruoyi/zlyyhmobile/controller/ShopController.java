package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.QueryShopProductBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.domain.vo.ShopProductListVo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.utils.Md5Utils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IProductService;
import com.ruoyi.zlyyhmobile.service.IShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 商户门店控制器
 * 前端访问路由地址为:/zlyyh-mobile/shop/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/ignore")
public class ShopController {
    private final IShopService shopService;
    private final IProductService productService;

    /**
     * 获取门店商品列表
     *
     * @return 门店商品列表
     */
    @GetMapping("/getShopProductList")
    public TableDataInfo<ShopProductListVo> getShopProductList(QueryShopProductBo bo, PageQuery pageQuery) {
        TimeInterval timer = DateUtil.timer();
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setCityCode(ZlyyhUtils.getUserCheckCityCode());
        bo.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        // 忽略前端传的分页
        pageQuery.setIsAsc(null);
        pageQuery.setOrderByColumn(null);
        JSONObject boObj = JSONObject.parseObject(JSONObject.toJSONString(bo));
        if (StringUtils.isBlank(bo.getOrderByType()) || !"1".equals(bo.getOrderByType())) {
            boObj.remove("latitude");
            boObj.remove("longitude");
        }
        String cacheKey = boObj.toJSONString() + ":pageNum_" + pageQuery.getPageNum() + "_pageSize_" + pageQuery.getPageSize();
        cacheKey = Md5Utils.hash(cacheKey);
        TableDataInfo<ShopProductListVo> cacheData = RedisUtils.getCacheObject(cacheKey);
        if (null != cacheData) {
            log.info("查询商户门店缓存耗时：{}毫秒", timer.interval());
            return cacheData;
        }
        TableDataInfo<ShopProductListVo> result = shopService.queryShopProductPageList(bo, pageQuery);
        for (ShopProductListVo record : result.getRows()) {
            // 计算距离
            record.calculateDistance();
            // 查询商品
            List<ProductVo> list = productService.queryListByShopId(bo.getPlatformKey(), record.getShopId(), null, bo.getCityCode());
            if (ObjectUtil.isNotEmpty(list)) {
                ProductVo productVo = list.get(0);
                // 计算折扣
                productVo.calculateDiscount();
                record.setProductVo(productVo);
            }
        }
        // 缓存5分钟
        RedisUtils.setCacheObject(cacheKey, result, Duration.ofMinutes(5));
        log.info("查询商户门店耗时：{}毫秒", timer.interval());
        return result;
    }

    /**
     * 获取门店列表
     *
     * @return 门店列表
     */
    @GetMapping("/getShopList")
    public TableDataInfo<ShopVo> getShopList(ShopBo bo, PageQuery pageQuery) {
        bo.setCitycode(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        return shopService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取商品门店列表
     *
     * @return 门店列表
     */
    @GetMapping("/getShopListByProductId")
    public TableDataInfo<ShopVo> getShopListByProductId(ShopBo bo, PageQuery pageQuery) {
        if (null == bo.getProductId() || bo.getProductId() < 1) {
            return TableDataInfo.build(new ArrayList<>());
        }
        bo.setCitycode(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        return shopService.getShopListByProductId(bo, pageQuery);
    }

    /**
     * 获取商品门店信息
     *
     * @return 门店列表
     */
    @GetMapping("/{shopId}")
    public R<ShopVo> getShopListByProductId(@NotNull(message = "主键不能为空") @PathVariable Long shopId) {
        return R.ok(shopService.queryById(shopId));
    }

    /**
     * 商户申请
     */
    @PostMapping("/addApproval")
    public R addApproval(@RequestBody MerchantApprovalBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return R.ok(shopService.addApproval(bo));
    }

    /**
     * 请求高德地图，获取完整地址信息
     */
    @GetMapping("/address")
    public R<JSONObject> address(ShopBo bo) {
        if (StringUtils.isBlank(bo.getAddress())) {
            return R.fail("缺少地址信息");
        }
        JSONObject addressInfo = AddressUtils.getAddressInfo(bo.getAddress(), null);
        return R.ok(addressInfo);
    }
}
