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
import com.ruoyi.zlyyh.domain.bo.TemplateSettingBo;
import com.ruoyi.zlyyh.domain.vo.TemplateSettingVo;
import com.ruoyi.zlyyhadmin.service.ITemplateSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 落地页配置控制器
 * 前端访问路由地址为:/zlyyh/templateSetting
 *
 * @author yzg
 * @date 2023-06-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/templateSetting")
public class TemplateSettingController extends BaseController {

    private final ITemplateSettingService iTemplateSettingService;

    /**
     * 查询落地页配置列表
     */
    @SaCheckPermission("zlyyh:templateSetting:list")
    @GetMapping("/list")
    public TableDataInfo<TemplateSettingVo> list(TemplateSettingBo bo, PageQuery pageQuery) {
        return iTemplateSettingService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出落地页配置列表
     */
    @SaCheckPermission("zlyyh:templateSetting:export")
    @Log(title = "落地页配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(TemplateSettingBo bo, HttpServletResponse response) {
        List<TemplateSettingVo> list = iTemplateSettingService.queryList(bo);
        ExcelUtil.exportExcel(list, "落地页配置", TemplateSettingVo.class, response);
    }

    /**
     * 获取落地页配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:templateSetting:query")
    @GetMapping("/{id}")
    public R<TemplateSettingVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iTemplateSettingService.queryById(id));
    }

    /**
     * 新增落地页配置
     */
    @SaCheckPermission("zlyyh:templateSetting:add")
    @Log(title = "落地页配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TemplateSettingBo bo) {
        return toAjax(iTemplateSettingService.insertByBo(bo));
    }

    /**
     * 修改落地页配置
     */
    @SaCheckPermission("zlyyh:templateSetting:edit")
    @Log(title = "落地页配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TemplateSettingBo bo) {
        return toAjax(iTemplateSettingService.updateByBo(bo));
    }

    /**
     * 删除落地页配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:templateSetting:remove")
    @Log(title = "落地页配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iTemplateSettingService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
