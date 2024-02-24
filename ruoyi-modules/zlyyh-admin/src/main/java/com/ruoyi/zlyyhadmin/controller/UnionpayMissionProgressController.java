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
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionProgressVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionProgressBo;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionProgressService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 银联任务进度控制器
 * 前端访问路由地址为:/zlyyh/unionpayMissionProgress
 *
 * @author yzg
 * @date 2024-02-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionpayMissionProgress")
public class UnionpayMissionProgressController extends BaseController {

    private final IUnionpayMissionProgressService iUnionpayMissionProgressService;

    /**
     * 查询银联任务进度列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionProgress:list")
    @GetMapping("/list")
    public TableDataInfo<UnionpayMissionProgressVo> list(UnionpayMissionProgressBo bo, PageQuery pageQuery) {
        return iUnionpayMissionProgressService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出银联任务进度列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionProgress:export")
    @Log(title = "银联任务进度", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionpayMissionProgressBo bo, HttpServletResponse response) {
        List<UnionpayMissionProgressVo> list = iUnionpayMissionProgressService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联任务进度", UnionpayMissionProgressVo.class, response);
    }

    /**
     * 获取银联任务进度详细信息
     *
     * @param progressId 主键
     */
    @SaCheckPermission("zlyyh:unionpayMissionProgress:query")
    @GetMapping("/{progressId}")
    public R<UnionpayMissionProgressVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long progressId) {
        return R.ok(iUnionpayMissionProgressService.queryById(progressId));
    }

    /**
     * 新增银联任务进度
     */
    @SaCheckPermission("zlyyh:unionpayMissionProgress:add")
    @Log(title = "银联任务进度", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionpayMissionProgressBo bo) {
        return toAjax(iUnionpayMissionProgressService.insertByBo(bo));
    }

    /**
     * 修改银联任务进度
     */
    @SaCheckPermission("zlyyh:unionpayMissionProgress:edit")
    @Log(title = "银联任务进度", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionpayMissionProgressBo bo) {
        return toAjax(iUnionpayMissionProgressService.updateByBo(bo));
    }

    /**
     * 删除银联任务进度
     *
     * @param progressIds 主键串
     */
    @SaCheckPermission("zlyyh:unionpayMissionProgress:remove")
    @Log(title = "银联任务进度", businessType = BusinessType.DELETE)
    @DeleteMapping("/{progressIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] progressIds) {
        return toAjax(iUnionpayMissionProgressService.deleteWithValidByIds(Arrays.asList(progressIds), true));
    }
}
