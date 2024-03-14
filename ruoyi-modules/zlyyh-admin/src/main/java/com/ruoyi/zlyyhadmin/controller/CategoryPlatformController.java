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
import com.ruoyi.zlyyh.domain.vo.CategoryVo;
import com.ruoyi.zlyyhadmin.service.ICategoryPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.CategoryPlatformVo;
import com.ruoyi.zlyyh.domain.bo.CategoryPlatformBo;

import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 多平台类别控制器
 * 前端访问路由地址为:/zlyyh/categoryPlatform
 *
 * @author yzg
 * @date 2024-02-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/categoryPlatform")
public class CategoryPlatformController extends BaseController {

    private final ICategoryPlatformService iCategoryPlatformService;

    /**
     * 查询多平台类别列表
     */
    @SaCheckPermission("zlyyh:categoryPlatform:list")
    @GetMapping("/list")
    public TableDataInfo<CategoryPlatformVo> list(CategoryPlatformBo bo, PageQuery pageQuery) {
        return iCategoryPlatformService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询多平台类别列表
     */
    @SaCheckPermission("zlyyh:categoryPlatform:list")
    @GetMapping("/listAll")
    public R<List<CategoryPlatformVo>> listAll(CategoryPlatformBo bo) {
        return R.ok(iCategoryPlatformService.queryList(bo));
    }

    /**
     * 导出多平台类别列表
     */
    @SaCheckPermission("zlyyh:categoryPlatform:export")
    @Log(title = "多平台类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CategoryPlatformBo bo, HttpServletResponse response) {
        List<CategoryPlatformVo> list = iCategoryPlatformService.queryList(bo);
        ExcelUtil.exportExcel(list, "多平台类别", CategoryPlatformVo.class, response);
    }

    /**
     * 获取多平台类别详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:categoryPlatform:query")
    @GetMapping("/{id}")
    public R<CategoryPlatformVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iCategoryPlatformService.queryById(id));
    }

    /**
     * 新增多平台类别
     */
    @SaCheckPermission("zlyyh:categoryPlatform:add")
    @Log(title = "多平台类别", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CategoryPlatformBo bo) {
        return toAjax(iCategoryPlatformService.insertByBo(bo));
    }

    /**
     * 修改多平台类别
     */
    @SaCheckPermission("zlyyh:categoryPlatform:edit")
    @Log(title = "多平台类别", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CategoryPlatformBo bo) {
        return toAjax(iCategoryPlatformService.updateByBo(bo));
    }

    /**
     * 删除多平台类别
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:categoryPlatform:remove")
    @Log(title = "多平台类别", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iCategoryPlatformService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
