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
import com.ruoyi.zlyyh.domain.bo.RecordLogBo;
import com.ruoyi.zlyyh.domain.vo.RecordLogVo;
import com.ruoyi.zlyyhadmin.service.IRecordLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 记录日志控制器
 * 前端访问路由地址为:/zlyyh/recordLog
 *
 * @author yzg
 * @date 2023-08-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/recordLog")
public class RecordLogController extends BaseController {

    private final IRecordLogService iRecordLogService;

    /**
     * 查询记录日志列表
     */
    @SaCheckPermission("zlyyh:recordLog:list")
    @GetMapping("/list")
    public TableDataInfo<RecordLogVo> list(RecordLogBo bo, PageQuery pageQuery) {
        return iRecordLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出记录日志列表
     */
    @SaCheckPermission("zlyyh:recordLog:export")
    @Log(title = "记录日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RecordLogBo bo, HttpServletResponse response) {
        List<RecordLogVo> list = iRecordLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "记录日志", RecordLogVo.class, response);
    }

    /**
     * 获取记录日志详细信息
     *
     * @param recordId 主键
     */
    @SaCheckPermission("zlyyh:recordLog:query")
    @GetMapping("/{recordId}")
    public R<RecordLogVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long recordId) {
        return R.ok(iRecordLogService.queryById(recordId));
    }

    /**
     * 新增记录日志
     */
    @SaCheckPermission("zlyyh:recordLog:add")
    @Log(title = "记录日志", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody RecordLogBo bo) {
        return toAjax(iRecordLogService.insertByBo(bo));
    }

    /**
     * 修改记录日志
     */
    @SaCheckPermission("zlyyh:recordLog:edit")
    @Log(title = "记录日志", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RecordLogBo bo) {
        return toAjax(iRecordLogService.updateByBo(bo));
    }

    /**
     * 删除记录日志
     *
     * @param recordIds 主键串
     */
    @SaCheckPermission("zlyyh:recordLog:remove")
    @Log(title = "记录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] recordIds) {
        return toAjax(iRecordLogService.deleteWithValidByIds(Arrays.asList(recordIds), true));
    }
}
