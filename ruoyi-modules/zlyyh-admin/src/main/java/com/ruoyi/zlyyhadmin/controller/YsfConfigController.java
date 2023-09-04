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
import com.ruoyi.zlyyh.domain.bo.YsfConfigBo;
import com.ruoyi.zlyyh.domain.vo.YsfConfigVo;
import com.ruoyi.zlyyhadmin.service.IYsfConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 云闪付参数配置控制器
 * 前端访问路由地址为:/zlyyh/ysfConfig
 *
 * @author yzg
 * @date 2023-07-31
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ysfConfig")
public class YsfConfigController extends BaseController {

    private final IYsfConfigService iYsfConfigService;

    /**
     * 查询云闪付参数配置列表
     */
    @SaCheckPermission("zlyyh:ysfConfig:list")
    @GetMapping("/list")
    public TableDataInfo<YsfConfigVo> list(YsfConfigBo bo, PageQuery pageQuery) {
        return iYsfConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出云闪付参数配置列表
     */
    @SaCheckPermission("zlyyh:ysfConfig:export")
    @Log(title = "云闪付参数配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(YsfConfigBo bo, HttpServletResponse response) {
        List<YsfConfigVo> list = iYsfConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "云闪付参数配置", YsfConfigVo.class, response);
    }

    /**
     * 获取云闪付参数配置详细信息
     *
     * @param configId 主键
     */
    @SaCheckPermission("zlyyh:ysfConfig:query")
    @GetMapping("/{configId}")
    public R<YsfConfigVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long configId) {
        return R.ok(iYsfConfigService.queryById(configId));
    }

    /**
     * 新增云闪付参数配置
     */
    @SaCheckPermission("zlyyh:ysfConfig:add")
    @Log(title = "云闪付参数配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody YsfConfigBo bo) {
        return toAjax(iYsfConfigService.insertByBo(bo));
    }

    /**
     * 修改云闪付参数配置
     */
    @SaCheckPermission("zlyyh:ysfConfig:edit")
    @Log(title = "云闪付参数配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody YsfConfigBo bo) {
        return toAjax(iYsfConfigService.updateByBo(bo));
    }

    /**
     * 删除云闪付参数配置
     *
     * @param configIds 主键串
     */
    @SaCheckPermission("zlyyh:ysfConfig:remove")
    @Log(title = "云闪付参数配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] configIds) {
        return toAjax(iYsfConfigService.deleteWithValidByIds(Arrays.asList(configIds), true));
    }
}
