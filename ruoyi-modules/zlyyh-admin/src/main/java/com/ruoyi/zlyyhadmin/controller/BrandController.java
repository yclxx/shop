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
import com.ruoyi.zlyyh.domain.bo.BrandBo;
import com.ruoyi.zlyyh.domain.vo.BrandVo;
import com.ruoyi.zlyyhadmin.service.IBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 品牌管理控制器
 * 前端访问路由地址为:/zlyyh/brand
 *
 * @author yzg
 * @date 2023-12-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {

    private final IBrandService iBrandService;

    /**
     * 查询品牌管理列表
     */
    @SaCheckPermission("zlyyh:brand:list")
    @GetMapping("/list")
    public TableDataInfo<BrandVo> list(BrandBo bo, PageQuery pageQuery) {
        return iBrandService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出品牌管理列表
     */
    @SaCheckPermission("zlyyh:brand:export")
    @Log(title = "品牌管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BrandBo bo, HttpServletResponse response) {
        List<BrandVo> list = iBrandService.queryList(bo);
        ExcelUtil.exportExcel(list, "品牌管理", BrandVo.class, response);
    }

    /**
     * 获取品牌管理详细信息
     *
     * @param brandId 主键
     */
    @SaCheckPermission("zlyyh:brand:query")
    @GetMapping("/{brandId}")
    public R<BrandVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long brandId) {
        return R.ok(iBrandService.queryById(brandId));
    }

    /**
     * 新增品牌管理
     */
    @SaCheckPermission("zlyyh:brand:add")
    @Log(title = "品牌管理", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BrandBo bo) {
        return toAjax(iBrandService.insertByBo(bo));
    }

    /**
     * 修改品牌管理
     */
    @SaCheckPermission("zlyyh:brand:edit")
    @Log(title = "品牌管理", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BrandBo bo) {
        return toAjax(iBrandService.updateByBo(bo));
    }

    /**
     * 删除品牌管理
     *
     * @param brandIds 主键串
     */
    @SaCheckPermission("zlyyh:brand:remove")
    @Log(title = "品牌管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{brandIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] brandIds) {
        return toAjax(iBrandService.deleteWithValidByIds(Arrays.asList(brandIds), true));
    }
}
