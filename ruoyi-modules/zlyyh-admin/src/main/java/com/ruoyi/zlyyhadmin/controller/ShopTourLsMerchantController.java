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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ShopTourLsMerchantVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourLsMerchantBo;
import com.ruoyi.zlyyhadmin.service.IShopTourLsMerchantService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 巡检商户号临时控制器
 * 前端访问路由地址为:/zlyyh/shopTourLsMerchant
 *
 * @author yzg
 * @date 2024-03-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopTourLsMerchant")
public class ShopTourLsMerchantController extends BaseController {

    private final IShopTourLsMerchantService iShopTourLsMerchantService;

    /**
     * 查询巡检商户号临时列表
     */
    @SaCheckPermission("zlyyh:shopTourLsMerchant:list")
    @GetMapping("/list")
    public TableDataInfo<ShopTourLsMerchantVo> list(ShopTourLsMerchantBo bo, PageQuery pageQuery) {
        return iShopTourLsMerchantService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出巡检商户号临时列表
     */
    @SaCheckPermission("zlyyh:shopTourLsMerchant:export")
    @Log(title = "巡检商户号临时", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopTourLsMerchantBo bo, HttpServletResponse response) {
        List<ShopTourLsMerchantVo> list = iShopTourLsMerchantService.queryList(bo);
        ExcelUtil.exportExcel(list, "巡检商户号临时", ShopTourLsMerchantVo.class, response);
    }

    /**
     * 获取巡检商户号临时详细信息
     *
     * @param tourMerchantLsId 主键
     */
    @SaCheckPermission("zlyyh:shopTourLsMerchant:query")
    @GetMapping("/{tourMerchantLsId}")
    public R<ShopTourLsMerchantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long tourMerchantLsId) {
        return R.ok(iShopTourLsMerchantService.queryById(tourMerchantLsId));
    }

    /**
     * 新增巡检商户号临时
     */
    @SaCheckPermission("zlyyh:shopTourLsMerchant:add")
    @Log(title = "巡检商户号临时", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopTourLsMerchantBo bo) {
        return toAjax(iShopTourLsMerchantService.insertByBo(bo));
    }

    /**
     * 修改巡检商户号临时
     */
    @SaCheckPermission("zlyyh:shopTourLsMerchant:edit")
    @Log(title = "巡检商户号临时", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopTourLsMerchantBo bo) {
        return toAjax(iShopTourLsMerchantService.updateByBo(bo));
    }

    /**
     * 删除巡检商户号临时
     *
     * @param tourMerchantLsIds 主键串
     */
    @SaCheckPermission("zlyyh:shopTourLsMerchant:remove")
    @Log(title = "巡检商户号临时", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tourMerchantLsIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] tourMerchantLsIds) {
        return toAjax(iShopTourLsMerchantService.deleteWithValidByIds(Arrays.asList(tourMerchantLsIds), true));
    }
}
