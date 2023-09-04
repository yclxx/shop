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
import com.ruoyi.zlyyh.domain.bo.MissionBo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyhadmin.service.IMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 任务配置控制器
 * 前端访问路由地址为:/zlyyh/mission
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mission")
public class MissionController extends BaseController {

    private final IMissionService iMissionService;

    /**
     * 查询任务配置列表
     */
    @SaCheckPermission("zlyyh:mission:list")
    @GetMapping("/list")
    public TableDataInfo<MissionVo> list(MissionBo bo, PageQuery pageQuery) {
        return iMissionService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询平台下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(MissionBo bo){
        List<MissionVo> missionVos = iMissionService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(missionVos, ColumnUtil.getFieldName(MissionVo::getMissionId),ColumnUtil.getFieldName(MissionVo::getMissionName),null));
    }

    /**
     * 导出任务配置列表
     */
    @SaCheckPermission("zlyyh:mission:export")
    @Log(title = "任务配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MissionBo bo, HttpServletResponse response) {
        List<MissionVo> list = iMissionService.queryList(bo);
        ExcelUtil.exportExcel(list, "任务配置", MissionVo.class, response);
    }

    /**
     * 获取任务配置详细信息
     *
     * @param missionId 主键
     */
    @SaCheckPermission("zlyyh:mission:query")
    @GetMapping("/{missionId}")
    public R<MissionVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long missionId) {
        return R.ok(iMissionService.queryById(missionId));
    }

    /**
     * 新增任务配置
     */
    @SaCheckPermission("zlyyh:mission:add")
    @Log(title = "任务配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MissionBo bo) {
        return toAjax(iMissionService.insertByBo(bo));
    }

    /**
     * 修改任务配置
     */
    @SaCheckPermission("zlyyh:mission:edit")
    @Log(title = "任务配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MissionBo bo) {
        return toAjax(iMissionService.updateByBo(bo));
    }

    /**
     * 删除任务配置
     *
     * @param missionIds 主键串
     */
    @SaCheckPermission("zlyyh:mission:remove")
    @Log(title = "任务配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{missionIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] missionIds) {
        return toAjax(iMissionService.deleteWithValidByIds(Arrays.asList(missionIds), true));
    }
}
