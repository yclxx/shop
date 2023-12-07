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
import com.ruoyi.zlyyh.domain.bo.PromotionTaskBo;
import com.ruoyi.zlyyh.domain.vo.PromotionTaskVo;
import com.ruoyi.zlyyhadmin.service.IPromotionTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 推广任务控制器
 * 前端访问路由地址为:/zlyyh/promotionTask
 *
 * @author yzg
 * @date 2023-11-16
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotionTask")
public class PromotionTaskController extends BaseController {

    private final IPromotionTaskService promotionTaskService;

    /**
     * 查询推广任务列表
     */
    @SaCheckPermission("zlyyh:promotionTask:list")
    @GetMapping("/list")
    public TableDataInfo<PromotionTaskVo> list(PromotionTaskBo bo, PageQuery pageQuery) {
        return promotionTaskService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出推广任务列表
     */
    @SaCheckPermission("zlyyh:promotionTask:export")
    @Log(title = "推广任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PromotionTaskBo bo, HttpServletResponse response) {
        List<PromotionTaskVo> list = promotionTaskService.queryList(bo);
        ExcelUtil.exportExcel(list, "推广任务", PromotionTaskVo.class, response);
    }

    /**
     * 获取推广任务详细信息
     */
    @SaCheckPermission("zlyyh:promotionTask:query")
    @GetMapping("/{taskId}")
    public R<PromotionTaskVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long taskId) {
        return R.ok(promotionTaskService.queryById(taskId));
    }

    /**
     * 新增推广任务
     */
    @SaCheckPermission("zlyyh:promotionTask:add")
    @Log(title = "推广任务", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PromotionTaskBo bo) {
        return toAjax(promotionTaskService.insertByBo(bo));
    }

    /**
     * 修改推广任务
     */
    @SaCheckPermission("zlyyh:promotionTask:edit")
    @Log(title = "推广任务", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PromotionTaskBo bo) {
        return toAjax(promotionTaskService.updateByBo(bo));
    }

    /**
     * 删除推广任务
     */
    @SaCheckPermission("zlyyh:promotionTask:remove")
    @Log(title = "推广任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] taskIds) {
        return toAjax(promotionTaskService.deleteWithValidByIds(Arrays.asList(taskIds), true));
    }
}
