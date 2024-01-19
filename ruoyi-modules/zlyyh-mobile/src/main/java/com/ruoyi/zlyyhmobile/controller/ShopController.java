package com.ruoyi.zlyyhmobile.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

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
