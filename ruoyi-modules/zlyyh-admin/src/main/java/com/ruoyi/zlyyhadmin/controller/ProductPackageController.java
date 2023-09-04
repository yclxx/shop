package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ProductPackageBo;
import com.ruoyi.zlyyh.domain.vo.ProductPackageVo;
import com.ruoyi.zlyyhadmin.service.IProductPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 商品券包控制器
 * 前端访问路由地址为:/zlyyh/productPackage
 *
 * @author yzg
 * @date 2023-06-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/productPackage")
public class ProductPackageController extends BaseController {

    private final IProductPackageService iProductPackageService;

    /**
     * 查询商品券包列表
     */
    @SaCheckPermission("zlyyh:productPackage:list")
    @GetMapping("/list")
    public TableDataInfo<ProductPackageVo> list(ProductPackageBo bo, PageQuery pageQuery) {
        return iProductPackageService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商品券包列表
     */
    @SaCheckPermission("zlyyh:productPackage:export")
    @Log(title = "商品券包", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProductPackageBo bo, HttpServletResponse response) {
        List<ProductPackageVo> list = iProductPackageService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品券包", ProductPackageVo.class, response);
    }

    /**
     * 获取商品券包详细信息
     *
     * @param packageId 主键
     */
    @SaCheckPermission("zlyyh:productPackage:query")
    @GetMapping("/{packageId}")
    public R<ProductPackageVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long packageId) {
        return R.ok(iProductPackageService.queryById(packageId));
    }

    /**
     * 新增商品券包
     */
    @SaCheckPermission("zlyyh:productPackage:add")
    @Log(title = "商品券包", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProductPackageBo bo) {
        return toAjax(iProductPackageService.insertByBo(bo));
    }

    /**
     * 修改商品券包
     */
    @SaCheckPermission("zlyyh:productPackage:edit")
    @Log(title = "商品券包", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProductPackageBo bo) {
        return toAjax(iProductPackageService.updateByBo(bo));
    }

    /**
     * 删除商品券包
     *
     * @param packageIds 主键串
     */
    @SaCheckPermission("zlyyh:productPackage:remove")
    @Log(title = "商品券包", businessType = BusinessType.DELETE)
    @DeleteMapping("/{packageIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] packageIds) {
        return toAjax(iProductPackageService.deleteWithValidByIds(Arrays.asList(packageIds), true));
    }
}
