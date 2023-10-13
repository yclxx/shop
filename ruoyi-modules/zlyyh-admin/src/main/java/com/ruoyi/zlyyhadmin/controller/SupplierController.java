package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ColumnUtil;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.SupplierBo;
import com.ruoyi.zlyyh.domain.vo.SupplierVo;
import com.ruoyi.zlyyhadmin.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 供应商控制器
 * 前端访问路由地址为:/zlyyh/supplier
 *
 * @author yzg
 * @date 2023-10-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/supplier")
public class SupplierController extends BaseController {

    private final ISupplierService iSupplierService;

    /**
     * 查询供应商列表
     */
    @SaCheckPermission("zlyyh:supplier:list")
    @GetMapping("/list")
    public TableDataInfo<SupplierVo> list(SupplierBo bo, PageQuery pageQuery) {
        return iSupplierService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出供应商列表
     */
    @SaCheckPermission("zlyyh:supplier:export")
    @Log(title = "供应商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SupplierBo bo, HttpServletResponse response) {
        List<SupplierVo> list = iSupplierService.queryList(bo);
        ExcelUtil.exportExcel(list, "供应商", SupplierVo.class, response);
    }

    /**
     * 供应商列表
     */
    @GetMapping("/select")
    public R select(SupplierBo bo) {
        List<SupplierVo> list = iSupplierService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(list, ColumnUtil.getFieldName(SupplierVo::getSupplierId), ColumnUtil.getFieldName(SupplierVo::getSupplierName), null));
    }

    /**
     * 获取供应商详细信息
     *
     * @param supplierId 主键
     */
    @SaCheckPermission("zlyyh:supplier:query")
    @GetMapping("/{supplierId}")
    public R<SupplierVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long supplierId) {
        return R.ok(iSupplierService.queryById(supplierId));
    }

    /**
     * 新增供应商
     */
    @SaCheckPermission("zlyyh:supplier:add")
    @Log(title = "供应商", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SupplierBo bo) {
        return toAjax(iSupplierService.insertByBo(bo));
    }

    /**
     * 修改供应商
     */
    @SaCheckPermission("zlyyh:supplier:edit")
    @Log(title = "供应商", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SupplierBo bo) {
        return toAjax(iSupplierService.updateByBo(bo));
    }

    /**
     * 删除供应商
     *
     * @param supplierIds 主键串
     */
    @SaCheckPermission("zlyyh:supplier:remove")
    @Log(title = "供应商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{supplierIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] supplierIds) {
        return toAjax(iSupplierService.deleteWithValidByIds(Arrays.asList(supplierIds), true));
    }
}
