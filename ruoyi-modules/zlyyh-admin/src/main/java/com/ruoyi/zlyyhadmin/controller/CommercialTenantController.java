package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ColumnUtil;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.SelectListEntity;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyhadmin.service.ICommercialTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 商户控制器
 * 前端访问路由地址为:/zlyyh/commercialTenant
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/commercialTenant")
public class CommercialTenantController extends BaseController {

    private final ICommercialTenantService iCommercialTenantService;

    /**
     * 查询商户列表
     */
    @SaCheckPermission("zlyyh:commercialTenant:list")
    @GetMapping("/list")
    public TableDataInfo<CommercialTenantVo> list(CommercialTenantBo bo, PageQuery pageQuery) {
        return iCommercialTenantService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询商户下拉列表
     */
    @GetMapping("/selectListMerchant")
    public R<List<SelectListEntity>> selectListMerchant(CommercialTenantBo bo){
        List<CommercialTenantVo> commercialTenantVos = iCommercialTenantService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(commercialTenantVos, ColumnUtil.getFieldName(CommercialTenantVo::getCommercialTenantId),ColumnUtil.getFieldName(CommercialTenantVo::getCommercialTenantName),null));
    }

    /**
     * 导出商户列表
     */
    @SaCheckPermission("zlyyh:commercialTenant:export")
    @Log(title = "商户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CommercialTenantBo bo, HttpServletResponse response) {
        List<CommercialTenantVo> list = iCommercialTenantService.queryList(bo);
        ExcelUtil.exportExcel(list, "商户", CommercialTenantVo.class, response);
    }

    /**
     * 获取商户详细信息
     *
     * @param commercialTenantId 主键
     */
    @SaCheckPermission("zlyyh:commercialTenant:query")
    @GetMapping("/{commercialTenantId}")
    public R<CommercialTenantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long commercialTenantId) {
        return R.ok(iCommercialTenantService.queryById(commercialTenantId));
    }

    /**
     * 新增商户
     */
    @SaCheckPermission("zlyyh:commercialTenant:add")
    @Log(title = "商户", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CommercialTenantBo bo) {
        return toAjax(iCommercialTenantService.insertByBo(bo));
    }

    /**
     * 修改商户
     */
    @SaCheckPermission("zlyyh:commercialTenant:edit")
    @Log(title = "商户", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CommercialTenantBo bo) {
        return toAjax(iCommercialTenantService.updateByBo(bo));
    }

    /**
     * 删除商户
     *
     * @param commercialTenantIds 主键串
     */
    @SaCheckPermission("zlyyh:commercialTenant:remove")
    @Log(title = "商户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commercialTenantIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] commercialTenantIds) {
        return toAjax(iCommercialTenantService.deleteWithValidByIds(Arrays.asList(commercialTenantIds), true));
    }
}
