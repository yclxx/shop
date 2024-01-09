package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ColumnUtil;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.SelectListEntity;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.zlyyh.domain.bo.MerchantTypeBo;
import com.ruoyi.zlyyh.domain.vo.MerchantTypeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.FileImportLogVo;
import com.ruoyi.zlyyh.domain.bo.FileImportLogBo;
import com.ruoyi.zlyyhadmin.service.IFileImportLogService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件导入记录控制器
 * 前端访问路由地址为:/zlyyh/fileImportLog
 *
 * @author yzg
 * @date 2024-01-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/fileImportLog")
public class FileImportLogController extends BaseController {

    private final IFileImportLogService iFileImportLogService;

    /**
     * 查询文件导入记录列表
     */
    @SaCheckPermission("zlyyh:fileImportLog:list")
    @GetMapping("/list")
    public TableDataInfo<FileImportLogVo> list(FileImportLogBo bo, PageQuery pageQuery) {
        return iFileImportLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询导入文件下拉列表
     */
    @GetMapping("/selectFileList")
    public R<List<SelectListEntity>> selectFileList(FileImportLogBo bo){
        List<FileImportLogVo> fileImportLogVoList = iFileImportLogService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(fileImportLogVoList, ColumnUtil.getFieldName(FileImportLogVo::getImportLogId),ColumnUtil.getFieldName(FileImportLogVo::getName),null));
    }

    /**
     * 导出文件导入记录列表
     */
    @SaCheckPermission("zlyyh:fileImportLog:export")
    @Log(title = "文件导入记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(FileImportLogBo bo, HttpServletResponse response) {
        List<FileImportLogVo> list = iFileImportLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "文件导入记录", FileImportLogVo.class, response);
    }

    /**
     * 获取文件导入记录详细信息
     *
     * @param importLogId 主键
     */
    @SaCheckPermission("zlyyh:fileImportLog:query")
    @GetMapping("/{importLogId}")
    public R<FileImportLogVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long importLogId) {
        return R.ok(iFileImportLogService.queryById(importLogId));
    }

    /**
     * 新增文件导入记录
     */
    @SaCheckPermission("zlyyh:fileImportLog:add")
    @Log(title = "文件导入记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody FileImportLogBo bo) {
        return toAjax(iFileImportLogService.insertByBo(bo));
    }

    /**
     * 修改文件导入记录
     */
    @SaCheckPermission("zlyyh:fileImportLog:edit")
    @Log(title = "文件导入记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody FileImportLogBo bo) {
        return toAjax(iFileImportLogService.updateByBo(bo));
    }

    /**
     * 删除文件导入记录
     *
     * @param importLogIds 主键串
     */
    @SaCheckPermission("zlyyh:fileImportLog:remove")
    @Log(title = "文件导入记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{importLogIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] importLogIds) {
        return toAjax(iFileImportLogService.deleteWithValidByIds(Arrays.asList(importLogIds), true));
    }
}
