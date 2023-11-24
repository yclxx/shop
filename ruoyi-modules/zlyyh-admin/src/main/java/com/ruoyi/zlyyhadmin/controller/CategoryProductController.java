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
import com.ruoyi.zlyyh.domain.bo.CategoryProductBo;
import com.ruoyi.zlyyh.domain.bo.ShopProductBo;
import com.ruoyi.zlyyh.domain.vo.CategoryProductVo;
import com.ruoyi.zlyyhadmin.service.ICategoryProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 栏目商品关联控制器
 * 前端访问路由地址为:/zlyyh/categoryProduct
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/categoryProduct")
public class CategoryProductController extends BaseController {

    private final ICategoryProductService iCategoryProductService;

    /**
     * 查询栏目商品关联列表
     */
    @SaCheckPermission("zlyyh:categoryProduct:list")
    @GetMapping("/list")
    public TableDataInfo<CategoryProductVo> list(CategoryProductBo bo, PageQuery pageQuery) {
        return iCategoryProductService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出栏目商品关联列表
     */
    @SaCheckPermission("zlyyh:categoryProduct:export")
    @Log(title = "栏目商品关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CategoryProductBo bo, HttpServletResponse response) {
        List<CategoryProductVo> list = iCategoryProductService.queryList(bo);
        ExcelUtil.exportExcel(list, "栏目商品关联", CategoryProductVo.class, response);
    }

    /**
     * 获取栏目商品关联详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:categoryProduct:query")
    @GetMapping("/{id}")
    public R<CategoryProductVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iCategoryProductService.queryById(id));
    }

    /**
     * 新增栏目商品关联
     */
    @SaCheckPermission("zlyyh:categoryProduct:add")
    @Log(title = "栏目商品关联", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CategoryProductBo bo) {
        return toAjax(iCategoryProductService.insertByBo(bo));
    }

    /**
     * 修改栏目商品关联
     */
    @SaCheckPermission("zlyyh:categoryProduct:edit")
    @Log(title = "栏目商品关联", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CategoryProductBo bo) {
        return toAjax(iCategoryProductService.updateByBo(bo));
    }

    /**
     * 删除栏目商品关联
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:categoryProduct:remove")
    @Log(title = "栏目商品关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iCategoryProductService.deleteWithValidByIds(Arrays.asList(ids), true));
    }


    /**
     * 为栏目批量添加商品
     */
    @PostMapping("addProductByCategory")
    public R<Void> addProductByCategory(@RequestBody CategoryProductBo bo) {
        return toAjax(iCategoryProductService.addProductByCategory(bo));
    }

    /**
     * 为栏目批量删除商品
     */
    @PostMapping("delProductByCategory")
    public R<Void> delProductByCategory(@RequestBody CategoryProductBo bo) {
        return toAjax(iCategoryProductService.delProductByCategory(bo));
    }
}
