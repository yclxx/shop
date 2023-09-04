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
import com.ruoyi.zlyyh.domain.bo.ShopGroupBo;
import com.ruoyi.zlyyh.domain.vo.ShopGroupVo;
import com.ruoyi.zlyyhadmin.service.IShopGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 门店组配置控制器
 * 前端访问路由地址为:/zlyyh/shopGroup
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopGroup")
public class ShopGroupController extends BaseController {

    private final IShopGroupService iShopGroupService;

    /**
     * 查询门店组配置列表
     */
    @SaCheckPermission("zlyyh:shopGroup:list")
    @GetMapping("/list")
    public TableDataInfo<ShopGroupVo> list(ShopGroupBo bo, PageQuery pageQuery) {
        return iShopGroupService.queryPageList(bo, pageQuery);
    }

    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(ShopGroupBo bo){
        List<ShopGroupVo> shopGroupVos = iShopGroupService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(shopGroupVos, ColumnUtil.getFieldName(ShopGroupVo::getShopGroupId),ColumnUtil.getFieldName(ShopGroupVo::getShopGroupName),null));

    }

    /**
     * 导出门店组配置列表
     */
    @SaCheckPermission("zlyyh:shopGroup:export")
    @Log(title = "门店组配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopGroupBo bo, HttpServletResponse response) {
        List<ShopGroupVo> list = iShopGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "门店组配置", ShopGroupVo.class, response);
    }

    /**
     * 获取门店组配置详细信息
     *
     * @param shopGroupId 主键
     */
    @SaCheckPermission("zlyyh:shopGroup:query")
    @GetMapping("/{shopGroupId}")
    public R<ShopGroupVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long shopGroupId) {
        return R.ok(iShopGroupService.queryById(shopGroupId));
    }

    /**
     * 新增门店组配置
     */
    @SaCheckPermission("zlyyh:shopGroup:add")
    @Log(title = "门店组配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopGroupBo bo) {
        return toAjax(iShopGroupService.insertByBo(bo));
    }

    /**
     * 修改门店组配置
     */
    @SaCheckPermission("zlyyh:shopGroup:edit")
    @Log(title = "门店组配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopGroupBo bo) {
        return toAjax(iShopGroupService.updateByBo(bo));
    }

    /**
     * 删除门店组配置
     *
     * @param shopGroupIds 主键串
     */
    @SaCheckPermission("zlyyh:shopGroup:remove")
    @Log(title = "门店组配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{shopGroupIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] shopGroupIds) {
        return toAjax(iShopGroupService.deleteWithValidByIds(Arrays.asList(shopGroupIds), true));
    }
}
