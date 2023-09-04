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
import com.ruoyi.zlyyhadmin.service.IShopGroupRelevanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ShopGroupRelevanceVo;
import com.ruoyi.zlyyh.domain.bo.ShopGroupRelevanceBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 门店组门店关联控制器
 * 前端访问路由地址为:/zlyyh/shopGroupRelevance
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopGroupRelevance")
public class ShopGroupRelevanceController extends BaseController {

    private final IShopGroupRelevanceService iShopGroupRelevanceService;

    /**
     * 查询门店组门店关联列表
     */
    @SaCheckPermission("zlyyh:shopGroupRelevance:list")
    @GetMapping("/list")
    public TableDataInfo<ShopGroupRelevanceVo> list(ShopGroupRelevanceBo bo, PageQuery pageQuery) {
        return iShopGroupRelevanceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出门店组门店关联列表
     */
    @SaCheckPermission("zlyyh:shopGroupRelevance:export")
    @Log(title = "门店组门店关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopGroupRelevanceBo bo, HttpServletResponse response) {
        List<ShopGroupRelevanceVo> list = iShopGroupRelevanceService.queryList(bo);
        ExcelUtil.exportExcel(list, "门店组门店关联", ShopGroupRelevanceVo.class, response);
    }

    /**
     * 获取门店组门店关联详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:shopGroupRelevance:query")
    @GetMapping("/{id}")
    public R<ShopGroupRelevanceVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iShopGroupRelevanceService.queryById(id));
    }

    /**
     * 新增门店组门店关联
     */
    @SaCheckPermission("zlyyh:shopGroupRelevance:add")
    @Log(title = "门店组门店关联", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopGroupRelevanceBo bo) {
        return toAjax(iShopGroupRelevanceService.insertByBo(bo));
    }

    /**
     * 修改门店组门店关联
     */
    @SaCheckPermission("zlyyh:shopGroupRelevance:edit")
    @Log(title = "门店组门店关联", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopGroupRelevanceBo bo) {
        return toAjax(iShopGroupRelevanceService.updateByBo(bo));
    }

    /**
     * 删除门店组门店关联
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:shopGroupRelevance:remove")
    @Log(title = "门店组门店关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iShopGroupRelevanceService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
