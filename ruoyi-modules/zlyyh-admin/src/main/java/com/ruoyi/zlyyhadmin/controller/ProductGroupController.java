package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.zlyyh.domain.bo.ProductGroupConnectBo;
import com.ruoyi.zlyyhadmin.domain.bo.ProductActionBo;
import com.ruoyi.zlyyhadmin.service.IProductGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ProductGroupVo;
import com.ruoyi.zlyyh.domain.bo.ProductGroupBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 商品组规则配置控制器
 * 前端访问路由地址为:/zlyyh/productGroup
 *
 * @author yzg
 * @date 2024-01-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/productGroup")
public class ProductGroupController extends BaseController {

    private final IProductGroupService iProductGroupService;

    /**
     * 查询商品组规则配置列表
     */
    @SaCheckPermission("zlyyh:productGroup:list")
    @GetMapping("/list")
    public TableDataInfo<ProductGroupVo> list(ProductGroupBo bo, PageQuery pageQuery) {
        return iProductGroupService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商品组规则配置列表
     */
    @SaCheckPermission("zlyyh:productGroup:export")
    @Log(title = "商品组规则配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProductGroupBo bo, HttpServletResponse response) {
        List<ProductGroupVo> list = iProductGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品组规则配置", ProductGroupVo.class, response);
    }

    /**
     * 获取商品组规则配置详细信息
     *
     * @param productGroupId 主键
     */
    @SaCheckPermission("zlyyh:productGroup:query")
    @GetMapping("/{productGroupId}")
    public R<ProductGroupVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long productGroupId) {
        return R.ok(iProductGroupService.queryById(productGroupId));
    }

    /**
     * 新增商品组规则配置
     */
    @SaCheckPermission("zlyyh:productGroup:add")
    @Log(title = "商品组规则配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProductGroupBo bo) {
        return toAjax(iProductGroupService.insertByBo(bo));
    }

    /**
     * 修改商品组规则配置
     */
    @SaCheckPermission("zlyyh:productGroup:edit")
    @Log(title = "商品组规则配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProductGroupBo bo) {
        return toAjax(iProductGroupService.updateByBo(bo));
    }

    /**
     * 修改优惠券批次商品
     */
    @PostMapping("/updateGroupProduct")
    public R<Void> updateActionProduct(@RequestBody ProductGroupConnectBo bo) {
        return toAjax(iProductGroupService.updateGroupProduct(bo.getProductIds(), bo.getProductGroupId(), bo.getType()));
    }


    /**
     * 删除商品组规则配置
     *
     * @param productGroupIds 主键串
     */
    @SaCheckPermission("zlyyh:productGroup:remove")
    @Log(title = "商品组规则配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productGroupIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] productGroupIds) {
        return toAjax(iProductGroupService.deleteWithValidByIds(Arrays.asList(productGroupIds), true));
    }
}
