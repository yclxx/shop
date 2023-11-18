package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.bo.ShopMerchantBo;
import com.ruoyi.zlyyh.domain.vo.ShopMerchantVo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 商户门店控制器
 * 前端访问路由地址为:/zlyyh-mobile/shop/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/ignore")
public class ShopController {
    private final IShopService shopService;

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
     * 获取商品门店信息(去除电话脱敏)
     *
     * @return 门店列表
     */
    @GetMapping("/details/{shopId}")
    public R<Shop> getShopBytId(@NotNull(message = "主键不能为空") @PathVariable Long shopId) {
        return R.ok(shopService.getShopBytId(shopId));
    }

    /**
     * 门店商品关联表
     */
    @GetMapping("getShopMerchant")
    public R<List<ShopMerchantVo>> getShopMerchantVo(ShopMerchantBo bo) {
        List<ShopMerchantVo> shopMerchantVo = shopService.getShopMerchantVo(bo);
        return R.ok(shopMerchantVo);
    }

    /**
     * 商户门店申请
     */
    @PostMapping("/addApproval")
    public R addApproval(@RequestBody MerchantApprovalBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        shopService.addApproval(bo);
        return R.ok();
    }
}
