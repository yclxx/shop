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
import com.ruoyi.zlyyh.domain.bo.CategoryBo;
import com.ruoyi.zlyyh.domain.vo.CategoryVo;
import com.ruoyi.zlyyhadmin.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 栏目控制器
 * 前端访问路由地址为:/zlyyh/category
 *
 * @author yzgnet
 * @date 2023-03-31
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {

    private final ICategoryService iCategoryService;

    /**
     * 查询栏目列表
     */
    @SaCheckPermission("zlyyh:category:list")
    @GetMapping("/list")
    public R<List<CategoryVo>> list(CategoryBo bo) {
        List<CategoryVo> list = iCategoryService.queryList(bo);
        return R.ok(list);
    }

    /**
     * 查询栏目列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(CategoryBo bo){
        List<CategoryVo> categoryVos = iCategoryService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(categoryVos, ColumnUtil.getFieldName(CategoryVo::getCategoryId),ColumnUtil.getFieldName(CategoryVo::getCategoryName),null));
    }

    /**
     * 导出栏目列表
     */
    @SaCheckPermission("zlyyh:category:export")
    @Log(title = "栏目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CategoryBo bo, HttpServletResponse response) {
        List<CategoryVo> list = iCategoryService.queryList(bo);
        ExcelUtil.exportExcel(list, "栏目", CategoryVo.class, response);
    }

    /**
     * 获取栏目详细信息
     *
     * @param categoryId 主键
     */
    @SaCheckPermission("zlyyh:category:query")
    @GetMapping("/{categoryId}")
    public R<CategoryVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long categoryId) {
        return R.ok(iCategoryService.queryById(categoryId));
    }

    /**
     * 新增栏目
     */
    @SaCheckPermission("zlyyh:category:add")
    @Log(title = "栏目", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CategoryBo bo) {
        return toAjax(iCategoryService.insertByBo(bo));
    }

    /**
     * 修改栏目
     */
    @SaCheckPermission("zlyyh:category:edit")
    @Log(title = "栏目", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CategoryBo bo) {
        return toAjax(iCategoryService.updateByBo(bo));
    }

    /**
     * 删除栏目
     *
     * @param categoryIds 主键串
     */
    @SaCheckPermission("zlyyh:category:remove")
    @Log(title = "栏目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] categoryIds) {
        return toAjax(iCategoryService.deleteWithValidByIds(Arrays.asList(categoryIds), true));
    }
}
