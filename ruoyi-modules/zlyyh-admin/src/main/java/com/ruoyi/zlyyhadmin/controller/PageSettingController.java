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
import com.ruoyi.zlyyh.domain.bo.PageSettingBo;
import com.ruoyi.zlyyh.domain.vo.PageSettingVo;
import com.ruoyi.zlyyhadmin.service.IPageSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 页面配置控制器
 * 前端访问路由地址为:/zlyyh/pageSetting
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/pageSetting")
public class PageSettingController extends BaseController {

    private final IPageSettingService iPageSettingService;

    /**
     * 查询页面配置列表
     */
    @SaCheckPermission("zlyyh:pageSetting:list")
    @GetMapping("/list")
    public TableDataInfo<PageSettingVo> list(PageSettingBo bo, PageQuery pageQuery) {
        return iPageSettingService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出页面配置列表
     */
    @SaCheckPermission("zlyyh:pageSetting:export")
    @Log(title = "页面配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PageSettingBo bo, HttpServletResponse response) {
        List<PageSettingVo> list = iPageSettingService.queryList(bo);
        ExcelUtil.exportExcel(list, "页面配置", PageSettingVo.class, response);
    }

    /**
     * 获取页面配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:pageSetting:query")
    @GetMapping("/{id}")
    public R<PageSettingVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iPageSettingService.queryById(id));
    }

    /**
     * 新增页面配置
     */
    @SaCheckPermission("zlyyh:pageSetting:add")
    @Log(title = "页面配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PageSettingBo bo) {
        return toAjax(iPageSettingService.insertByBo(bo));
    }

    /**
     * 修改页面配置
     */
    @SaCheckPermission("zlyyh:pageSetting:edit")
    @Log(title = "页面配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PageSettingBo bo) {
        return toAjax(iPageSettingService.updateByBo(bo));
    }

    /**
     * 删除页面配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:pageSetting:remove")
    @Log(title = "页面配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iPageSettingService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
