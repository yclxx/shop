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
import com.ruoyi.zlyyh.domain.vo.ShopTourVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.zlyyhadmin.service.IShopTourService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 巡检商户控制器
 * 前端访问路由地址为:/zlyyh/shopTour
 *
 * @author yzg
 * @date 2024-01-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopTour")
public class ShopTourController extends BaseController {

    private final IShopTourService iShopTourService;

    /**
     * 查询巡检商户列表
     */
    @SaCheckPermission("zlyyh:shopTour:list")
    @GetMapping("/list")
    public TableDataInfo<ShopTourVo> list(ShopTourBo bo, PageQuery pageQuery) {
        return iShopTourService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出巡检商户列表
     */
    @SaCheckPermission("zlyyh:shopTour:export")
    @Log(title = "巡检商户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopTourBo bo, HttpServletResponse response) {
        List<ShopTourVo> list = iShopTourService.queryList(bo);
        ExcelUtil.exportExcel(list, "巡检商户", ShopTourVo.class, response);
    }

    /**
     * 获取巡检商户详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:shopTour:query")
    @GetMapping("/{id}")
    public R<ShopTourVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iShopTourService.queryById(id));
    }

    /**
     * 新增巡检商户
     */
    @SaCheckPermission("zlyyh:shopTour:add")
    @Log(title = "巡检商户", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopTourBo bo) {
        return toAjax(iShopTourService.insertByBo(bo));
    }

    /**
     * 修改巡检商户
     */
    @SaCheckPermission("zlyyh:shopTour:edit")
    @Log(title = "巡检商户", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopTourBo bo) {
        return toAjax(iShopTourService.updateByBo(bo));
    }

    /**
     * 删除巡检商户
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:shopTour:remove")
    @Log(title = "巡检商户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iShopTourService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 添加巡检商户
     * @return
     */
    @PostMapping("/changeTourShop")
    public R<Void> changeTourShop(@RequestBody ShopTourBo bo) {
        iShopTourService.changeTourShop(bo);
        return R.ok();
    }

    /**
     * 巡检审核通过
     */
    @PostMapping("/tourCheckPass")
    public R<Void> tourCheckPass(@RequestBody ShopTourBo bo) {
        iShopTourService.tourCheckPass(bo);
        return R.ok();
    }
}
