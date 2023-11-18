package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IVerifierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setId(LoginHelper.getUserId());
        List<ShopVo> shopVos = verifierService.queryShopList(bo);
        return R.ok(shopVos);
    }

    /**
     * 编辑门店信息
     */
    @PostMapping("/updateShopById")
    public R<Boolean> updateShopById(@RequestBody ShopBo bo) {
        return R.ok(verifierService.updateShopById(bo));
    }


    /**
     * 编辑门店信息(商户核销)
     */
    @PostMapping("/updateShopMerchantById")
    public R<Boolean> updateShopMerchantById(@RequestBody List<ShopMerchantBo> bos) {
        return R.ok(verifierService.updateShopMerchantById(bos));
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
    public TableDataInfo<Product> productPage(VerifierBo bo, PageQuery pageQuery) {
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
     * @return 商品列表
     */
    @PostMapping("/updateProductById")
    public R<Boolean> updateProductById(@RequestBody ProductBo bo) {
        return R.ok(verifierService.updateProductById(bo));
    }

    /**
     * 新增商品信息
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
    public R cancelVerifier(@RequestBody CodeBo bo) {
        return R.ok(verifierService.cancelVerifier(bo));
    }

    /**
     * 添加门店与核销人员关系
     */
    @PostMapping("/addVerifier")
    public R addVerifier(@RequestBody CodeBo bo) {
        return R.ok(verifierService.addVerifier(bo));
    }
}
