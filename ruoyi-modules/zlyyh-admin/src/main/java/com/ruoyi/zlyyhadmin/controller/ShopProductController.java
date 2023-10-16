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
import com.ruoyi.zlyyh.domain.bo.ShopProductBo;
import com.ruoyi.zlyyh.domain.vo.ShopProductVo;
import com.ruoyi.zlyyhadmin.service.IShopProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 商品门店关联控制器
 * 前端访问路由地址为:/zlyyh/shopProduct
 *
 * @author yzg
 * @date 2023-05-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopProduct")
public class ShopProductController extends BaseController {

    private final IShopProductService iShopProductService;

    /**
     * 查询商品门店关联列表
     */
    @SaCheckPermission("zlyyh:shopProduct:list")
    @GetMapping("/list")
    public TableDataInfo<ShopProductVo> list(ShopProductBo bo, PageQuery pageQuery) {
        return iShopProductService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商品门店关联列表
     */
    @SaCheckPermission("zlyyh:shopProduct:export")
    @Log(title = "商品门店关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopProductBo bo, HttpServletResponse response) {
        List<ShopProductVo> list = iShopProductService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品门店关联", ShopProductVo.class, response);
    }

    /**
     * 获取商品门店关联详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:shopProduct:query")
    @GetMapping("/{id}")
    public R<ShopProductVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iShopProductService.queryById(id));
    }

    /**
     * 新增商品门店关联
     */
    @SaCheckPermission("zlyyh:shopProduct:add")
    @Log(title = "商品门店关联", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopProductBo bo) {
        return toAjax(iShopProductService.insertByBo(bo));
    }

    /**
     * 修改商品门店关联
     */
    @SaCheckPermission("zlyyh:shopProduct:edit")
    @Log(title = "商品门店关联", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopProductBo bo) {
        return toAjax(iShopProductService.updateByBo(bo));
    }

    /**
     * 删除商品门店关联
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:shopProduct:remove")
    @Log(title = "商品门店关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iShopProductService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 为商品批量添加门店
     */
    @PostMapping("addShopByProduct")
    public R<Void> addShopByProduct(@RequestBody ShopProductBo bo) {
        return toAjax(iShopProductService.addShopByProduct(bo));
    }

    /**
     * 为商品批量删除门店
     */
    @PostMapping("delByShopProduct")
    public R<Void> delByShopProduct(@RequestBody ShopProductBo bo) {
        return toAjax(iShopProductService.delByShopProduct(bo));
    }
}
