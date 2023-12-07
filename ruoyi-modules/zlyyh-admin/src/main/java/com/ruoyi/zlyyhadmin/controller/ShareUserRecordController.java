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
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;
import com.ruoyi.zlyyhadmin.service.IShareUserRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 分销记录控制器
 * 前端访问路由地址为:/zlyyh/shareUserRecord
 *
 * @author yzg
 * @date 2023-11-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shareUserRecord")
public class ShareUserRecordController extends BaseController {

    private final IShareUserRecordService iShareUserRecordService;

    /**
     * 查询分销记录列表
     */
    @SaCheckPermission("zlyyh:shareUserRecord:list")
    @GetMapping("/list")
    public TableDataInfo<ShareUserRecordVo> list(ShareUserRecordBo bo, PageQuery pageQuery) {
        return iShareUserRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出分销记录列表
     */
    @SaCheckPermission("zlyyh:shareUserRecord:export")
    @Log(title = "分销记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShareUserRecordBo bo, HttpServletResponse response) {
        List<ShareUserRecordVo> list = iShareUserRecordService.queryList(bo);
        ExcelUtil.exportExcel(list, "分销记录", ShareUserRecordVo.class, response);
    }

    /**
     * 获取分销记录详细信息
     *
     * @param recordId 主键
     */
    @SaCheckPermission("zlyyh:shareUserRecord:query")
    @GetMapping("/{recordId}")
    public R<ShareUserRecordVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long recordId) {
        return R.ok(iShareUserRecordService.queryById(recordId));
    }

    /**
     * 新增分销记录
     */
    @SaCheckPermission("zlyyh:shareUserRecord:add")
    @Log(title = "分销记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShareUserRecordBo bo) {
        return toAjax(iShareUserRecordService.insertByBo(bo));
    }

    /**
     * 修改分销记录
     */
    @SaCheckPermission("zlyyh:shareUserRecord:edit")
    @Log(title = "分销记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShareUserRecordBo bo) {
        return toAjax(iShareUserRecordService.updateByBo(bo));
    }

    /**
     * 删除分销记录
     *
     * @param recordIds 主键串
     */
    @SaCheckPermission("zlyyh:shareUserRecord:remove")
    @Log(title = "分销记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] recordIds) {
        return toAjax(iShareUserRecordService.deleteWithValidByIds(Arrays.asList(recordIds), true));
    }
}
