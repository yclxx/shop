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
import com.ruoyi.zlyyh.domain.bo.TemplatePageBo;
import com.ruoyi.zlyyh.domain.vo.TemplatePageVo;
import com.ruoyi.zlyyhadmin.service.ITemplatePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 落地页控制器
 * 前端访问路由地址为:/zlyyh/templatePage
 *
 * @author yzg
 * @date 2023-06-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/templatePage")
public class TemplatePageController extends BaseController {

    private final ITemplatePageService iTemplatePageService;

    /**
     * 查询落地页列表
     */
    @SaCheckPermission("zlyyh:templatePage:list")
    @GetMapping("/list")
    public TableDataInfo<TemplatePageVo> list(TemplatePageBo bo, PageQuery pageQuery) {
        return iTemplatePageService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(TemplatePageBo bo) {
        List<TemplatePageVo> templatePageVos = iTemplatePageService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(templatePageVos, ColumnUtil.getFieldName(TemplatePageVo::getTemplateId), ColumnUtil.getFieldName(TemplatePageVo::getTemplateName), null));
    }

    /**
     * 导出落地页列表
     */
    @SaCheckPermission("zlyyh:templatePage:export")
    @Log(title = "落地页", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(TemplatePageBo bo, HttpServletResponse response) {
        List<TemplatePageVo> list = iTemplatePageService.queryList(bo);
        ExcelUtil.exportExcel(list, "落地页", TemplatePageVo.class, response);
    }

    /**
     * 获取落地页详细信息
     *
     * @param templateId 主键
     */
    @SaCheckPermission("zlyyh:templatePage:query")
    @GetMapping("/{templateId}")
    public R<TemplatePageVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long templateId) {
        return R.ok(iTemplatePageService.queryById(templateId));
    }

    /**
     * 新增落地页
     */
    @SaCheckPermission("zlyyh:templatePage:add")
    @Log(title = "落地页", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TemplatePageBo bo) {
        return toAjax(iTemplatePageService.insertByBo(bo));
    }

    /**
     * 修改落地页
     */
    @SaCheckPermission("zlyyh:templatePage:edit")
    @Log(title = "落地页", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TemplatePageBo bo) {
        return toAjax(iTemplatePageService.updateByBo(bo));
    }

    /**
     * 删除落地页
     *
     * @param templateIds 主键串
     */
    @SaCheckPermission("zlyyh:templatePage:remove")
    @Log(title = "落地页", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] templateIds) {
        return toAjax(iTemplatePageService.deleteWithValidByIds(Arrays.asList(templateIds), true));
    }
}
