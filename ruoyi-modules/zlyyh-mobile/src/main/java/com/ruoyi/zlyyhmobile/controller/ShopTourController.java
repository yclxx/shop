package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.ActivityFileShopBo;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IShopService;
import com.ruoyi.zlyyhmobile.service.IShopTourService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

/**
 * 巡检门店控制器
 * 前端访问路由地址为:/zlyyh-mobile/shopTour/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopTour/ignore")
public class ShopTourController {
    private final IShopService shopService;
    private final IShopTourService shopTourService;

    /**
     * 获取城市列表
     */
    @GetMapping("/getCityList")
    public R<List<CityVo>> getCityList(String cityCode) {
        return R.ok(shopTourService.getCityList(cityCode));
    }

    /**
     * 获取商圈列表
     */
    @GetMapping("/getBusinessDistrictList")
    public R<List<BusinessDistrictVo>> getBusinessDistrictList(String adcode) {
        return R.ok(shopTourService.getBusinessDistrictList(adcode));
    }

    /**
     * 获取巡检门店列表
     */
    @GetMapping("/getTourShopList")
    public TableDataInfo<ShopTourVo> getTourShopList(ShopTourBo bo, PageQuery pageQuery) {
        return shopTourService.queryPageTourList(bo, pageQuery);
    }

    /**
     * 获取预约门店列表
     */
    @GetMapping("/getReserveShopList")
    public TableDataInfo<ShopTourVo> getReserveShopList(ShopTourBo bo, PageQuery pageQuery) {
        return shopTourService.getReserveShopList(bo, pageQuery);
    }

    /**
     * 获取附近巡检门店列表
     */
    @GetMapping("/getNearTourShopList")
    public TableDataInfo<ShopTourVo> getNearTourShopList(ShopTourBo bo, PageQuery pageQuery) {
        return shopTourService.queryPageNearTourList(bo, pageQuery);
    }

    /**
     * 预约巡检商户门店
     */
    @GetMapping("/reserveTourShop")
    public R<String> reserveTourShop(Long tourShopId) throws ServerException {
        Long userId = LoginHelper.getUserId();
        if (ObjectUtil.isEmpty(userId)) {
            throw new ServerException("获取信息异常，请尝试重新登录");
        }
        return R.ok(shopTourService.reserveTourShop(tourShopId,userId));
    }

    /**
     * 取消预约巡检商户门店
     */
    @GetMapping("/cancelReserveTourShop")
    public R<Void> cancelReserveTourShop(Long tourShopId, String tourType){
        shopTourService.cancelReserveTourShop(tourShopId,tourType);
        return R.ok();
    }

    /**
     * 开始巡检核验有效期
     */
    @GetMapping("/verifyDate")
    public R<String> verifyDate(Long tourShopId){
        return R.ok(shopTourService.verifyDate(tourShopId));
    }

    /**
     * 获取巡检商户门店信息
     */
    @GetMapping("/getTourShopInfo")
    public R<ShopTourVo> getTourShopInfo(Long tourId,String type) {
        return R.ok(shopTourService.getTourShopInfo(tourId,type));
    }

    /**
     * 巡检信息暂存
     * @param bo
     * @return
     */
    @PostMapping("/tourZanCun")
    public R<Void> tourZanCun(@RequestBody ShopTourBo bo) {
        shopTourService.tourZanCun(bo);
        return R.ok();
    }

    /**
     * 巡检信息提交
     * @param bo
     * @return
     */
    @PostMapping("/tourSubmit")
    public R<Void> tourSubmit(@RequestBody ShopTourBo bo) {
        shopTourService.tourSubmit(bo);
        return R.ok();
    }

    /**
     * 获取巡检人员巡检奖励
     */
    @GetMapping("/getTourReward")
    public R<ShopTourRewardVo> getTourReward() {
        Long userId = LoginHelper.getUserId();
        return R.ok(shopTourService.getTourReward(userId));
    }

    /**
     * 获取商户号
     */
    @GetMapping("/getShopMerchantNoList/{shopId}")
    public R<List<ShopMerchantVo>> getShopMerchantNoList(@PathVariable("shopId") Long shopId) {
        return R.ok(shopTourService.getShopMerchantNoList(shopId));
    }

    /**
     * 获取商户号信息
     */
    @GetMapping("/getShopMerchantNoInfo")
    public R<List<ShopMerchantVo>> getShopMerchantNoInfo(Long shopId,String merchantType) {
        return R.ok(shopTourService.getShopMerchantNoInfo(shopId,merchantType));
    }
}
