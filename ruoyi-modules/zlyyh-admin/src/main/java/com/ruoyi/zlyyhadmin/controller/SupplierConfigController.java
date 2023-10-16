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
import com.ruoyi.zlyyh.domain.bo.SupplierConfigBo;
import com.ruoyi.zlyyh.domain.vo.SupplierConfigVo;
import com.ruoyi.zlyyhadmin.service.ISupplierConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 供应商参数配置控制器
 * 前端访问路由地址为:/zlyyh/supplierConfig
 *
 * @author yzg
 * @date 2023-10-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/supplierConfig")
public class SupplierConfigController extends BaseController {

    private final ISupplierConfigService iSupplierConfigService;

    /**
     * 查询供应商参数配置列表
     */
    @SaCheckPermission("zlyyh:supplierConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SupplierConfigVo> list(SupplierConfigBo bo, PageQuery pageQuery) {
        return iSupplierConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出供应商参数配置列表
     */
    @SaCheckPermission("zlyyh:supplierConfig:export")
    @Log(title = "供应商参数配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SupplierConfigBo bo, HttpServletResponse response) {
        List<SupplierConfigVo> list = iSupplierConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "供应商参数配置", SupplierConfigVo.class, response);
    }

    /**
     * 获取供应商参数配置详细信息
     *
     * @param configId 主键
     */
    @SaCheckPermission("zlyyh:supplierConfig:query")
    @GetMapping("/{configId}")
    public R<SupplierConfigVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long configId) {
        return R.ok(iSupplierConfigService.queryById(configId));
    }

    /**
     * 新增供应商参数配置
     */
    @SaCheckPermission("zlyyh:supplierConfig:add")
    @Log(title = "供应商参数配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SupplierConfigBo bo) {
        return toAjax(iSupplierConfigService.insertByBo(bo));
    }

    /**
     * 修改供应商参数配置
     */
    @SaCheckPermission("zlyyh:supplierConfig:edit")
    @Log(title = "供应商参数配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SupplierConfigBo bo) {
        return toAjax(iSupplierConfigService.updateByBo(bo));
    }

    /**
     * 删除供应商参数配置
     *
     * @param configIds 主键串
     */
    @SaCheckPermission("zlyyh:supplierConfig:remove")
    @Log(title = "供应商参数配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] configIds) {
        return toAjax(iSupplierConfigService.deleteWithValidByIds(Arrays.asList(configIds), true));
    }
}
