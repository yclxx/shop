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
import com.ruoyi.zlyyhadmin.service.IProductInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ProductInfoVo;
import com.ruoyi.zlyyh.domain.bo.ProductInfoBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 商品拓展控制器
 * 前端访问路由地址为:/zlyyh/productInfo
 *
 * @author yzg
 * @date 2023-05-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/productInfo")
public class ProductInfoController extends BaseController {

    private final IProductInfoService iProductInfoService;

    /**
     * 查询商品拓展列表
     */
    @SaCheckPermission("zlyyh:productInfo:list")
    @GetMapping("/list")
    public TableDataInfo<ProductInfoVo> list(ProductInfoBo bo, PageQuery pageQuery) {
        return iProductInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商品拓展列表
     */
    @SaCheckPermission("zlyyh:productInfo:export")
    @Log(title = "商品拓展", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProductInfoBo bo, HttpServletResponse response) {
        List<ProductInfoVo> list = iProductInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品拓展", ProductInfoVo.class, response);
    }

    /**
     * 获取商品拓展详细信息
     *
     * @param productId 主键
     */
    @SaCheckPermission("zlyyh:productInfo:query")
    @GetMapping("/{productId}")
    public R<ProductInfoVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long productId) {
        return R.ok(iProductInfoService.queryById(productId));
    }

    /**
     * 新增商品拓展
     */
    @SaCheckPermission("zlyyh:productInfo:add")
    @Log(title = "商品拓展", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProductInfoBo bo) {
        return toAjax(iProductInfoService.insertByBo(bo));
    }

    /**
     * 修改商品拓展
     */
    @SaCheckPermission("zlyyh:productInfo:edit")
    @Log(title = "商品拓展", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProductInfoBo bo) {
        return toAjax(iProductInfoService.updateByBo(bo));
    }

    /**
     * 删除商品拓展
     *
     * @param productIds 主键串
     */
    @SaCheckPermission("zlyyh:productInfo:remove")
    @Log(title = "商品拓展", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] productIds) {
        return toAjax(iProductInfoService.deleteWithValidByIds(Arrays.asList(productIds), true));
    }
}
