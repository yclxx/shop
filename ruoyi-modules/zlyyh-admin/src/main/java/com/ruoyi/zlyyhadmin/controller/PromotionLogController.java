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
import com.ruoyi.zlyyh.domain.bo.PromotionLogBo;
import com.ruoyi.zlyyh.domain.vo.PromotionLogVo;
import com.ruoyi.zlyyhadmin.service.IPromotionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 推广任务记录控制器
 * 前端访问路由地址为:/zlyyh/promotionLog
 *
 * @author yzg
 * @date 2023-11-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotionLog")
public class PromotionLogController extends BaseController {

    private final IPromotionLogService iPromotionLogService;

    /**
     * 查询推广任务记录列表
     */
    @SaCheckPermission("zlyyh:promotionLog:list")
    @GetMapping("/list")
    public TableDataInfo<PromotionLogVo> list(PromotionLogBo bo, PageQuery pageQuery) {
        return iPromotionLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出推广任务记录列表
     */
    @SaCheckPermission("zlyyh:promotionLog:export")
    @Log(title = "推广任务记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PromotionLogBo bo, HttpServletResponse response) {
        List<PromotionLogVo> list = iPromotionLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "推广任务记录", PromotionLogVo.class, response);
    }

    /**
     * 获取推广任务记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:promotionLog:query")
    @GetMapping("/{id}")
    public R<PromotionLogVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iPromotionLogService.queryById(id));
    }

    /**
     * 新增推广任务记录
     */
    @SaCheckPermission("zlyyh:promotionLog:add")
    @Log(title = "推广任务记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PromotionLogBo bo) {
        return toAjax(iPromotionLogService.insertByBo(bo));
    }

    /**
     * 修改推广任务记录
     */
    @SaCheckPermission("zlyyh:promotionLog:edit")
    @Log(title = "推广任务记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PromotionLogBo bo) {
        return toAjax(iPromotionLogService.updateByBo(bo));
    }

    /**
     * 删除推广任务记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:promotionLog:remove")
    @Log(title = "推广任务记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iPromotionLogService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
