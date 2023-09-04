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
import com.ruoyi.zlyyh.domain.bo.MissionGroupBo;
import com.ruoyi.zlyyh.domain.vo.MissionGroupVo;
import com.ruoyi.zlyyhadmin.service.IMissionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 任务组控制器
 * 前端访问路由地址为:/zlyyh/missionGroup
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/missionGroup")
public class MissionGroupController extends BaseController {

    private final IMissionGroupService iMissionGroupService;

    /**
     * 查询任务组列表
     */
    @SaCheckPermission("zlyyh:missionGroup:list")
    @GetMapping("/list")
    public TableDataInfo<MissionGroupVo> list(MissionGroupBo bo, PageQuery pageQuery) {
        return iMissionGroupService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询平台下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(MissionGroupBo bo){
        List<MissionGroupVo> platformVoList = iMissionGroupService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(platformVoList, ColumnUtil.getFieldName(MissionGroupVo::getMissionGroupId),ColumnUtil.getFieldName(MissionGroupVo::getMissionGroupName),null));
    }

    /**
     * 导出任务组列表
     */
    @SaCheckPermission("zlyyh:missionGroup:export")
    @Log(title = "任务组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MissionGroupBo bo, HttpServletResponse response) {
        List<MissionGroupVo> list = iMissionGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "任务组", MissionGroupVo.class, response);
    }

    /**
     * 获取任务组详细信息
     *
     * @param missionGroupId 主键
     */
    @SaCheckPermission("zlyyh:missionGroup:query")
    @GetMapping("/{missionGroupId}")
    public R<MissionGroupVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long missionGroupId) {
        return R.ok(iMissionGroupService.queryById(missionGroupId));
    }

    /**
     * 新增任务组
     */
    @SaCheckPermission("zlyyh:missionGroup:add")
    @Log(title = "任务组", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MissionGroupBo bo) {
        return toAjax(iMissionGroupService.insertByBo(bo));
    }

    /**
     * 修改任务组
     */
    @SaCheckPermission("zlyyh:missionGroup:edit")
    @Log(title = "任务组", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MissionGroupBo bo) {
        return toAjax(iMissionGroupService.updateByBo(bo));
    }

    /**
     * 删除任务组
     *
     * @param missionGroupIds 主键串
     */
    @SaCheckPermission("zlyyh:missionGroup:remove")
    @Log(title = "任务组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{missionGroupIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] missionGroupIds) {
        return toAjax(iMissionGroupService.deleteWithValidByIds(Arrays.asList(missionGroupIds), true));
    }
}
