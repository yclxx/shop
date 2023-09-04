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
import com.ruoyi.zlyyh.domain.bo.EquityRecordBo;
import com.ruoyi.zlyyh.domain.vo.EquityRecordVo;
import com.ruoyi.zlyyhadmin.service.IEquityRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 领取记录控制器
 * 前端访问路由地址为:/zlyyh/equityRecord
 *
 * @author yzg
 * @date 2023-06-06
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/equityRecord")
public class EquityRecordController extends BaseController {

    private final IEquityRecordService iEquityRecordService;

    /**
     * 查询领取记录列表
     */
    @SaCheckPermission("zlyyh:equityRecord:list")
    @GetMapping("/list")
    public TableDataInfo<EquityRecordVo> list(EquityRecordBo bo, PageQuery pageQuery) {
        return iEquityRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出领取记录列表
     */
    @SaCheckPermission("zlyyh:equityRecord:export")
    @Log(title = "领取记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(EquityRecordBo bo, HttpServletResponse response) {
        List<EquityRecordVo> list = iEquityRecordService.queryList(bo);
        ExcelUtil.exportExcel(list, "领取记录", EquityRecordVo.class, response);
    }

    /**
     * 获取领取记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:equityRecord:query")
    @GetMapping("/{id}")
    public R<EquityRecordVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iEquityRecordService.queryById(id));
    }

    /**
     * 新增领取记录
     */
    @SaCheckPermission("zlyyh:equityRecord:add")
    @Log(title = "领取记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody EquityRecordBo bo) {
        return toAjax(iEquityRecordService.insertByBo(bo));
    }

    /**
     * 修改领取记录
     */
    @SaCheckPermission("zlyyh:equityRecord:edit")
    @Log(title = "领取记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody EquityRecordBo bo) {
        return toAjax(iEquityRecordService.updateByBo(bo));
    }

    /**
     * 删除领取记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:equityRecord:remove")
    @Log(title = "领取记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iEquityRecordService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
