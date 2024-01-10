package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.validate.AppEditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.ShopMerchant;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.ShopAndMerchantBo;
import com.ruoyi.zlyyhmobile.domain.bo.VerifierShopBo;
import com.ruoyi.zlyyhmobile.service.ICommercialTenantService;
import com.ruoyi.zlyyhmobile.service.IShopService;
import com.ruoyi.zlyyhmobile.service.IVerifierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 商户端信息处理
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/verifier")
public class VerifierController extends BaseController {
    private final IVerifierService verifierService;
    private final IShopService shopService;
    private final ICommercialTenantService commercialTenantService;

    /**
     * 查询个人信息
     */
    @GetMapping("/info")
    public R<VerifierVo> info(VerifierBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setId(LoginHelper.getUserId());
        VerifierVo info = verifierService.info(bo);
        return R.ok(info);
    }

    /**
     * 修改核销人员信息
     */
    @PostMapping("/updateVerifier")
    public R updateVerifier(@RequestBody VerifierBo bo) {
        bo.setId(LoginHelper.getUserId());
        return R.ok(verifierService.updateVerifier(bo));
    }

    /**
     * 根据门店查询核销人员
     */
    @GetMapping("/verifierList")
    public R<List<VerifierVo>> verifierList(Long shopId) {
        return R.ok(verifierService.verifierList(shopId));
    }

    /**
     * 查询核销人所处门店列表
     */
    @GetMapping("/shopList")
    public R<List<ShopVo>> shopList(VerifierBo bo) {
        bo.setId(LoginHelper.getUserId());
        List<ShopVo> shopVos = verifierService.queryShopList(bo);
        return R.ok(shopVos);
    }

    /**
     * 商户端查询门店详情
     */
    @GetMapping("/getShopId/{shopId}")
    public R<Map<String, Object>> getShopId(@PathVariable("shopId") Long shopId) {
        return R.ok(verifierService.getShopId(shopId));
    }

    /**
     * 商户端修改门店详情
     */
    @PostMapping("/updateShopId")
    public R updateShopId(@RequestBody ShopAndMerchantBo bo) {
        return R.ok(verifierService.updateShopId(bo));
    }

    /**
     * 商户端修改门店详情
     */
    @PostMapping("/insertShop")
    public R insertShop(@RequestBody ShopAndMerchantBo bo) {
        return R.ok(verifierService.insertShop(bo));
    }

    /**
     * 编辑门店信息
     */
    @PostMapping("/updateShopById")
    public R<Boolean> updateShopById(@RequestBody ShopBo bo) {
        return R.ok(verifierService.updateShopById(bo));
    }

    /**
     * 查询门店分页
     */
    @GetMapping("/shopPage")
    public TableDataInfo<ShopVo> shopPage(VerifierBo bo, PageQuery pageQuery) {
        //bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setId(LoginHelper.getUserId());
        return verifierService.queryShopPageList(bo, pageQuery);
    }

    /**
     * 查询商品分页
     */
    @GetMapping("/productPage")
    public TableDataInfo<ProductVo> productPage(VerifierBo bo, PageQuery pageQuery) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setId(LoginHelper.getUserId());
        return verifierService.queryProductPageList(bo, pageQuery);
    }

    /**
     * 修改商品状态
     */
    @PostMapping("/productStatus")
    public R productStatus(@RequestBody ProductBo bo) {
        verifierService.productStatus(bo);
        return R.ok();
    }

    /**
     * 修改商品信息
     */
    @PostMapping("/updateProductById")
    public R<Boolean> updateProductById(@RequestBody ProductBo bo) {
        return R.ok(verifierService.updateProductById(bo));
    }

    /**
     * 新增商品信息
     *
     * @return 商品列表
     */
    @PostMapping("/insertProduct")
    public R<Boolean> insertProduct(@RequestBody ProductBo bo) {
        return R.ok(verifierService.insertProduct(bo));
    }

    /**
     * 查询门店核销人员信息
     */
    @GetMapping("/getVerifierListByShop")
    public TableDataInfo<VerifierVo> getVerifierListByShop(CodeBo bo, PageQuery pageQuery) {
        bo.setVerifierId(LoginHelper.getUserId());
        return verifierService.getVerifierListByShop(bo, pageQuery);
    }

    /**
     * 取消门店与核销人员关系
     */
    @PostMapping("/cancelVerifier")
    public R cancelVerifier(@RequestBody VerifierBo bo) {
        return R.ok(verifierService.cancelVerifier(bo));
    }

    /**
     * 添加门店与核销人员关系
     */
    @PostMapping("/addVerifier")
    public R addVerifier(@RequestBody VerifierBo bo) {
        return R.ok(verifierService.addVerifier(bo));
    }

    /**
     * 门店商户表
     */
    @GetMapping("/getShopMerchant")
    public R<List<ShopMerchantVo>> getShopMerchant(ShopMerchantBo bo) {
        List<ShopMerchantVo> shopMerchantVo = verifierService.getShopMerchant(bo);
        return R.ok(shopMerchantVo);
    }

    /**
     * 门店商户表修改或新增
     */
    @PostMapping("/insertShopMerchant")
    public R<Void> insertShopMerchant(@RequestBody ShopAndMerchantBo bo) {
        verifierService.insertShopMerchant(bo);
        return R.ok();
    }

    /**
     * 新增商户门店和门店商编(商户端)
     */
    @PostMapping("/insertTenantShopMerchant")
    public R<Void> insertTenantShopMerchant(@RequestBody ShopAndMerchantBo bo) {
        bo.getCommercialTenant().setVerifierId(LoginHelper.getUserId());
        verifierService.insertTenantShopMerchant(bo);
        return R.ok();
    }

    /**
     * 新增商户门店和门店商编(商户端)
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/editShop")
    public R<Void> editShop(@Validated(AppEditGroup.class) @RequestBody VerifierShopBo bo) {
        VerifierVo info = verifierService.queryById(LoginHelper.getUserId());
        if (null == info) {
            return R.fail(HttpStatus.HTTP_UNAUTHORIZED, "登录超时，请退出重试");
        }
        if (!info.getIsBd() && !info.getIsAdmin()) {
            return R.fail("无权操作");
        }
        CommercialTenantVo commercialTenantVo = commercialTenantService.getDetails(bo.getCommercialTenantId());
        if (null == commercialTenantVo) {
            return R.fail("商户不存在");
        }
        if (!Objects.equals(info.getId(), commercialTenantVo.getVerifierId()) && !Objects.equals(info.getMobile(), commercialTenantVo.getAdminMobile())) {
            return R.fail("请求错误，无权操作");
        }
        ShopBo shopBo = BeanCopyUtils.copy(bo, ShopBo.class);
        if (null == shopBo) {
            return R.fail("系统繁忙，请稍后重试");
        }
        if (null == shopBo.getShopId()) {
            shopService.insertByBo(shopBo);
        } else {
            shopService.updateByBo(shopBo);
        }
        // 门店商户号
        if (ObjectUtil.isNotEmpty(bo.getShopMerchantBos())) {
            List<ShopMerchant> shopMerchants = BeanCopyUtils.copyList(bo.getShopMerchantBos(), ShopMerchant.class);
            shopService.updateShopMerchantById(shopBo.getShopId(), shopMerchants);
        }
        return R.ok();
    }
}
