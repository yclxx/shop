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
import com.ruoyi.zlyyh.domain.bo.PageBo;
import com.ruoyi.zlyyh.domain.vo.PageVo;
import com.ruoyi.zlyyhadmin.service.IPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 页面控制器
 * 前端访问路由地址为:/zlyyh/page
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/page")
public class PageController extends BaseController {

    private final IPageService iPageService;

    /**
     * 查询页面列表
     */
    @SaCheckPermission("zlyyh:page:list")
    @GetMapping("/list")
    public TableDataInfo<PageVo> list(PageBo bo, PageQuery pageQuery) {
        return iPageService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询板块模版下拉列表
     */
    @GetMapping("/selectListPage")
    public R<List<SelectListEntity>> selectListPage(PageBo bo){
        List<PageVo> pageVos = iPageService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(pageVos, ColumnUtil.getFieldName(PageVo::getId),ColumnUtil.getFieldName(PageVo::getPageRemake),ColumnUtil.getFieldName(PageVo::getPagePath)));
    }

    /**
     * 导出页面列表
     */
    @SaCheckPermission("zlyyh:page:export")
    @Log(title = "页面", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PageBo bo, HttpServletResponse response) {
        List<PageVo> list = iPageService.queryList(bo);
        ExcelUtil.exportExcel(list, "页面", PageVo.class, response);
    }

    /**
     * 获取页面详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:page:query")
    @GetMapping("/{id}")
    public R<PageVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iPageService.queryById(id));
    }

    /**
     * 新增页面
     */
    @SaCheckPermission("zlyyh:page:add")
    @Log(title = "页面", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PageBo bo) {
        return toAjax(iPageService.insertByBo(bo));
    }

    /**
     * 修改页面
     */
    @SaCheckPermission("zlyyh:page:edit")
    @Log(title = "页面", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PageBo bo) {
        return toAjax(iPageService.updateByBo(bo));
    }

    /**
     * 删除页面
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:page:remove")
    @Log(title = "页面", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iPageService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
