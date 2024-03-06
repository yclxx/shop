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
import com.ruoyi.zlyyhadmin.service.IPlatformUserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.PlatformUserGroupVo;
import com.ruoyi.zlyyh.domain.bo.PlatformUserGroupBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 平台城市企业微信用户来源控制器
 * 前端访问路由地址为:/zlyyh/platformUserGroup
 *
 * @author yzg
 * @date 2024-03-06
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/platformUserGroup")
public class PlatformUserGroupController extends BaseController {

    private final IPlatformUserGroupService iPlatformUserGroupService;

    /**
     * 查询平台城市企业微信用户来源列表
     */
    @SaCheckPermission("zlyyh:platformUserGroup:list")
    @GetMapping("/list")
    public TableDataInfo<PlatformUserGroupVo> list(PlatformUserGroupBo bo, PageQuery pageQuery) {
        return iPlatformUserGroupService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出平台城市企业微信用户来源列表
     */
    @SaCheckPermission("zlyyh:platformUserGroup:export")
    @Log(title = "平台城市企业微信用户来源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PlatformUserGroupBo bo, HttpServletResponse response) {
        List<PlatformUserGroupVo> list = iPlatformUserGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "平台城市企业微信用户来源", PlatformUserGroupVo.class, response);
    }

    /**
     * 获取平台城市企业微信用户来源详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:platformUserGroup:query")
    @GetMapping("/{id}")
    public R<PlatformUserGroupVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iPlatformUserGroupService.queryById(id));
    }

    /**
     * 新增平台城市企业微信用户来源
     */
    @SaCheckPermission("zlyyh:platformUserGroup:add")
    @Log(title = "平台城市企业微信用户来源", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PlatformUserGroupBo bo) {
        return toAjax(iPlatformUserGroupService.insertByBo(bo));
    }

    /**
     * 修改平台城市企业微信用户来源
     */
    @SaCheckPermission("zlyyh:platformUserGroup:edit")
    @Log(title = "平台城市企业微信用户来源", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PlatformUserGroupBo bo) {
        return toAjax(iPlatformUserGroupService.updateByBo(bo));
    }

    /**
     * 删除平台城市企业微信用户来源
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:platformUserGroup:remove")
    @Log(title = "平台城市企业微信用户来源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iPlatformUserGroupService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
