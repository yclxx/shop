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
import com.ruoyi.zlyyh.domain.vo.ShopTourLogVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourLogBo;
import com.ruoyi.zlyyhadmin.service.IShopTourLogService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 巡检记录控制器
 * 前端访问路由地址为:/zlyyh/shopTourLog
 *
 * @author yzg
 * @date 2024-03-06
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopTourLog")
public class ShopTourLogController extends BaseController {

    private final IShopTourLogService iShopTourLogService;

    /**
     * 查询巡检记录列表
     */
    @SaCheckPermission("zlyyh:shopTourLog:list")
    @GetMapping("/list")
    public TableDataInfo<ShopTourLogVo> list(ShopTourLogBo bo, PageQuery pageQuery) {
        return iShopTourLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出巡检记录列表
     */
    @SaCheckPermission("zlyyh:shopTourLog:export")
    @Log(title = "巡检记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopTourLogBo bo, HttpServletResponse response) {
        List<ShopTourLogVo> list = iShopTourLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "巡检记录", ShopTourLogVo.class, response);
    }

    /**
     * 获取巡检记录详细信息
     *
     * @param tourLogId 主键
     */
    @SaCheckPermission("zlyyh:shopTourLog:query")
    @GetMapping("/{tourLogId}")
    public R<ShopTourLogVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long tourLogId) {
        return R.ok(iShopTourLogService.queryById(tourLogId));
    }

    /**
     * 新增巡检记录
     */
    @SaCheckPermission("zlyyh:shopTourLog:add")
    @Log(title = "巡检记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopTourLogBo bo) {
        return toAjax(iShopTourLogService.insertByBo(bo));
    }

    /**
     * 修改巡检记录
     */
    @SaCheckPermission("zlyyh:shopTourLog:edit")
    @Log(title = "巡检记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopTourLogBo bo) {
        return toAjax(iShopTourLogService.updateByBo(bo));
    }

    /**
     * 删除巡检记录
     *
     * @param tourLogIds 主键串
     */
    @SaCheckPermission("zlyyh:shopTourLog:remove")
    @Log(title = "巡检记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tourLogIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] tourLogIds) {
        return toAjax(iShopTourLogService.deleteWithValidByIds(Arrays.asList(tourLogIds), true));
    }
}
