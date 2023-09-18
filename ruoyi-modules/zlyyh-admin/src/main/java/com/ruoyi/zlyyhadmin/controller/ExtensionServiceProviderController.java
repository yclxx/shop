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
import com.ruoyi.zlyyhadmin.service.IExtensionServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ExtensionServiceProviderVo;
import com.ruoyi.zlyyh.domain.bo.ExtensionServiceProviderBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 商户拓展服务商控制器
 * 前端访问路由地址为:/zlyyh/extensionServiceProvider
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/extensionServiceProvider")
public class ExtensionServiceProviderController extends BaseController {

    private final IExtensionServiceProviderService iExtensionServiceProviderService;

    /**
     * 查询商户拓展服务商列表
     */
    @SaCheckPermission("zlyyh:extensionServiceProvider:list")
    @GetMapping("/list")
    public TableDataInfo<ExtensionServiceProviderVo> list(ExtensionServiceProviderBo bo, PageQuery pageQuery) {
        return iExtensionServiceProviderService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商户拓展服务商列表
     */
    @SaCheckPermission("zlyyh:extensionServiceProvider:export")
    @Log(title = "商户拓展服务商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ExtensionServiceProviderBo bo, HttpServletResponse response) {
        List<ExtensionServiceProviderVo> list = iExtensionServiceProviderService.queryList(bo);
        ExcelUtil.exportExcel(list, "商户拓展服务商", ExtensionServiceProviderVo.class, response);
    }

    /**
     * 获取商户拓展服务商详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:extensionServiceProvider:query")
    @GetMapping("/{id}")
    public R<ExtensionServiceProviderVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iExtensionServiceProviderService.queryById(id));
    }

    /**
     * 新增商户拓展服务商
     */
    @SaCheckPermission("zlyyh:extensionServiceProvider:add")
    @Log(title = "商户拓展服务商", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ExtensionServiceProviderBo bo) {
        return toAjax(iExtensionServiceProviderService.insertByBo(bo));
    }

    /**
     * 修改商户拓展服务商
     */
    @SaCheckPermission("zlyyh:extensionServiceProvider:edit")
    @Log(title = "商户拓展服务商", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ExtensionServiceProviderBo bo) {
        return toAjax(iExtensionServiceProviderService.updateByBo(bo));
    }

    /**
     * 删除商户拓展服务商
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:extensionServiceProvider:remove")
    @Log(title = "商户拓展服务商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iExtensionServiceProviderService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
