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
import com.ruoyi.zlyyhadmin.service.IPlatformCityGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.PlatformCityGroupVo;
import com.ruoyi.zlyyh.domain.bo.PlatformCityGroupBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 平台城市企业微信群控制器
 * 前端访问路由地址为:/zlyyh/platformCityGroup
 *
 * @author yzg
 * @date 2024-02-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/platformCityGroup")
public class PlatformCityGroupController extends BaseController {

    private final IPlatformCityGroupService iPlatformCityGroupService;

    /**
     * 查询平台城市企业微信群列表
     */
    @SaCheckPermission("zlyyh:platformCityGroup:list")
    @GetMapping("/list")
    public TableDataInfo<PlatformCityGroupVo> list(PlatformCityGroupBo bo, PageQuery pageQuery) {
        return iPlatformCityGroupService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出平台城市企业微信群列表
     */
    @SaCheckPermission("zlyyh:platformCityGroup:export")
    @Log(title = "平台城市企业微信群", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PlatformCityGroupBo bo, HttpServletResponse response) {
        List<PlatformCityGroupVo> list = iPlatformCityGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "平台城市企业微信群", PlatformCityGroupVo.class, response);
    }

    /**
     * 获取平台城市企业微信群详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:platformCityGroup:query")
    @GetMapping("/{id}")
    public R<PlatformCityGroupVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iPlatformCityGroupService.queryById(id));
    }

    /**
     * 新增平台城市企业微信群
     */
    @SaCheckPermission("zlyyh:platformCityGroup:add")
    @Log(title = "平台城市企业微信群", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PlatformCityGroupBo bo) {
        return toAjax(iPlatformCityGroupService.insertByBo(bo));
    }

    /**
     * 修改平台城市企业微信群
     */
    @SaCheckPermission("zlyyh:platformCityGroup:edit")
    @Log(title = "平台城市企业微信群", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PlatformCityGroupBo bo) {
        return toAjax(iPlatformCityGroupService.updateByBo(bo));
    }

    /**
     * 删除平台城市企业微信群
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:platformCityGroup:remove")
    @Log(title = "平台城市企业微信群", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iPlatformCityGroupService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
