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
import com.ruoyi.zlyyh.domain.bo.CategorySupplierBo;
import com.ruoyi.zlyyh.domain.vo.CategorySupplierVo;
import com.ruoyi.zlyyhadmin.service.ICategorySupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 供应商产品分类控制器
 * 前端访问路由地址为:/zlyyh/categorySupplier
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/categorySupplier")
public class CategorySupplierController extends BaseController {

    private final ICategorySupplierService iCategorySupplierService;

    /**
     * 查询供应商产品分类列表
     */
    @SaCheckPermission("zlyyh:categorySupplier:list")
    @GetMapping("/list")
    public TableDataInfo<CategorySupplierVo> list(CategorySupplierBo bo, PageQuery pageQuery) {
        return iCategorySupplierService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出供应商产品分类列表
     */
    @SaCheckPermission("zlyyh:categorySupplier:export")
    @Log(title = "供应商产品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CategorySupplierBo bo, HttpServletResponse response) {
        List<CategorySupplierVo> list = iCategorySupplierService.queryList(bo);
        ExcelUtil.exportExcel(list, "供应商产品分类", CategorySupplierVo.class, response);
    }

    /**
     * 获取供应商产品分类详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:categorySupplier:query")
    @GetMapping("/{id}")
    public R<CategorySupplierVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iCategorySupplierService.queryById(id));
    }

    /**
     * 新增供应商产品分类
     */
    @SaCheckPermission("zlyyh:categorySupplier:add")
    @Log(title = "供应商产品分类", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CategorySupplierBo bo) {
        return toAjax(iCategorySupplierService.insertByBo(bo));
    }

    /**
     * 修改供应商产品分类
     */
    @SaCheckPermission("zlyyh:categorySupplier:edit")
    @Log(title = "供应商产品分类", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CategorySupplierBo bo) {
        return toAjax(iCategorySupplierService.updateByBo(bo));
    }

    /**
     * 删除供应商产品分类
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:categorySupplier:remove")
    @Log(title = "供应商产品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iCategorySupplierService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
