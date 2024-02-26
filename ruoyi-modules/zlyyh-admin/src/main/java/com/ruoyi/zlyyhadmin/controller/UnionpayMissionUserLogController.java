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
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserLogVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionUserLogBo;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionUserLogService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 银联任务奖励发放记录控制器
 * 前端访问路由地址为:/zlyyh/unionpayMissionUserLog
 *
 * @author yzg
 * @date 2024-02-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionpayMissionUserLog")
public class UnionpayMissionUserLogController extends BaseController {

    private final IUnionpayMissionUserLogService iUnionpayMissionUserLogService;

    /**
     * 查询银联任务奖励发放记录列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionUserLog:list")
    @GetMapping("/list")
    public TableDataInfo<UnionpayMissionUserLogVo> list(UnionpayMissionUserLogBo bo, PageQuery pageQuery) {
        return iUnionpayMissionUserLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出银联任务奖励发放记录列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionUserLog:export")
    @Log(title = "银联任务奖励发放记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionpayMissionUserLogBo bo, HttpServletResponse response) {
        List<UnionpayMissionUserLogVo> list = iUnionpayMissionUserLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联任务奖励发放记录", UnionpayMissionUserLogVo.class, response);
    }

    /**
     * 获取银联任务奖励发放记录详细信息
     *
     * @param upMissionUserLog 主键
     */
    @SaCheckPermission("zlyyh:unionpayMissionUserLog:query")
    @GetMapping("/{upMissionUserLog}")
    public R<UnionpayMissionUserLogVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long upMissionUserLog) {
        return R.ok(iUnionpayMissionUserLogService.queryById(upMissionUserLog));
    }

    /**
     * 新增银联任务奖励发放记录
     */
    @SaCheckPermission("zlyyh:unionpayMissionUserLog:add")
    @Log(title = "银联任务奖励发放记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionpayMissionUserLogBo bo) {
        return toAjax(iUnionpayMissionUserLogService.insertByBo(bo));
    }

    /**
     * 修改银联任务奖励发放记录
     */
    @SaCheckPermission("zlyyh:unionpayMissionUserLog:edit")
    @Log(title = "银联任务奖励发放记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionpayMissionUserLogBo bo) {
        return toAjax(iUnionpayMissionUserLogService.updateByBo(bo));
    }

    /**
     * 删除银联任务奖励发放记录
     *
     * @param upMissionUserLogs 主键串
     */
    @SaCheckPermission("zlyyh:unionpayMissionUserLog:remove")
    @Log(title = "银联任务奖励发放记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{upMissionUserLogs}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] upMissionUserLogs) {
        return toAjax(iUnionpayMissionUserLogService.deleteWithValidByIds(Arrays.asList(upMissionUserLogs), true));
    }
}
