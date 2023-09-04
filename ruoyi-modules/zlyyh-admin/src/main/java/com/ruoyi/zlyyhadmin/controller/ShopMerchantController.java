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
import com.ruoyi.zlyyhadmin.service.IShopMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ShopMerchantVo;
import com.ruoyi.zlyyh.domain.bo.ShopMerchantBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 门店商户号控制器
 * 前端访问路由地址为:/zlyyh/shopMerchant
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopMerchant")
public class ShopMerchantController extends BaseController {

    private final IShopMerchantService iShopMerchantService;

    /**
     * 查询门店商户号列表
     */
    @SaCheckPermission("zlyyh:shopMerchant:list")
    @GetMapping("/list")
    public TableDataInfo<ShopMerchantVo> list(ShopMerchantBo bo, PageQuery pageQuery) {
        return iShopMerchantService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出门店商户号列表
     */
    @SaCheckPermission("zlyyh:shopMerchant:export")
    @Log(title = "门店商户号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopMerchantBo bo, HttpServletResponse response) {
        List<ShopMerchantVo> list = iShopMerchantService.queryList(bo);
        ExcelUtil.exportExcel(list, "门店商户号", ShopMerchantVo.class, response);
    }

    /**
     * 获取门店商户号详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:shopMerchant:query")
    @GetMapping("/{id}")
    public R<ShopMerchantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iShopMerchantService.queryById(id));
    }

    /**
     * 新增门店商户号
     */
    @SaCheckPermission("zlyyh:shopMerchant:add")
    @Log(title = "门店商户号", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopMerchantBo bo) {
        return toAjax(iShopMerchantService.insertByBo(bo));
    }

    /**
     * 修改门店商户号
     */
    @SaCheckPermission("zlyyh:shopMerchant:edit")
    @Log(title = "门店商户号", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopMerchantBo bo) {
        return toAjax(iShopMerchantService.updateByBo(bo));
    }

    /**
     * 删除门店商户号
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:shopMerchant:remove")
    @Log(title = "门店商户号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iShopMerchantService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
