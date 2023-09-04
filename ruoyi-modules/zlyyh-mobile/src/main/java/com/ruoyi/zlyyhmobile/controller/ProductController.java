package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IProductService;
import com.ruoyi.zlyyhmobile.utils.ProductUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品管理控制器
 * 前端访问路由地址为:/zlyyh-mobile/product/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/ignore")
public class ProductController {
    private final IProductService productService;

    /**
     * 获取商品列表
     *
     * @return 商品列表
     */
    @GetMapping("/getProductList")
    public TableDataInfo<ProductVo> getProductList(ProductBo bo, PageQuery pageQuery) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setShowCity(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        return productService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取商品详情
     *
     * @return 商品列表
     */
    @GetMapping("/getProductInfo")
    public R<ProductVo> getProductList(ProductBo productBo) {
        ProductVo productVo = productService.queryFoodById(productBo.getProductId());
        return R.ok(productVo);
    }

    /**
     * 获取商品详情
     *
     * @return 商品列表
     */
    @GetMapping("/getProduct")
    public R<ProductVo> getProduct(ProductBo bo) {
        ProductVo productVo = productService.queryById(bo.getProductId());
        Long userId = null;
        try {
            userId = LoginHelper.getUserId();
        } catch (Exception ignored) {
        }
        return R.ok(ProductUtils.getProductVoCheck(productVo, userId, ZlyyhUtils.getPlatformId(), ServletUtils.getHeader(ZlyyhConstants.CITY_CODE), true));
    }
}
