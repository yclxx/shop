package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ColumnUtil;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.SelectListEntity;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.PlatformBo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyhadmin.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 平台信息控制器
 * 前端访问路由地址为:/zlyyh/platform
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/platform")
public class PlatformController extends BaseController {

    private final IPlatformService iPlatformService;

    /**
     * 查询平台信息列表
     */
    @SaCheckPermission("zlyyh:platform:list")
    @GetMapping("/list")
    public TableDataInfo<PlatformVo> list(PlatformBo bo, PageQuery pageQuery) {
        return iPlatformService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询平台下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(PlatformBo bo){
        List<PlatformVo> platformVoList = iPlatformService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(platformVoList, ColumnUtil.getFieldName(PlatformVo::getPlatformKey),ColumnUtil.getFieldName(PlatformVo::getPlatformName),null));
    }

    /**
     * 导出平台信息列表
     */
    @SaCheckPermission("zlyyh:platform:export")
    @Log(title = "平台信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PlatformBo bo, HttpServletResponse response) {
        List<PlatformVo> list = iPlatformService.queryList(bo);
        ExcelUtil.exportExcel(list, "平台信息", PlatformVo.class, response);
    }

    /**
     * 获取平台信息详细信息
     *
     * @param platformKey 主键
     */
    @SaCheckPermission("zlyyh:platform:query")
    @GetMapping("/{platformKey}")
    public R<PlatformVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long platformKey) {
        return R.ok(iPlatformService.queryById(platformKey));
    }

    /**
     * 新增平台信息
     */
    @SaCheckPermission("zlyyh:platform:add")
    @Log(title = "平台信息", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PlatformBo bo) {
        return toAjax(iPlatformService.insertByBo(bo));
    }

    /**
     * 修改平台信息
     */
    @SaCheckPermission("zlyyh:platform:edit")
    @Log(title = "平台信息", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PlatformBo bo) {
        return toAjax(iPlatformService.updateByBo(bo));
    }

    /**
     * 删除平台信息
     *
     * @param platformKeys 主键串
     */
    @SaCheckPermission("zlyyh:platform:remove")
    @Log(title = "平台信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{platformKeys}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] platformKeys) {
        return toAjax(iPlatformService.deleteWithValidByIds(Arrays.asList(platformKeys), true));
    }
}
