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
import com.ruoyi.zlyyh.domain.bo.MissionUserBo;
import com.ruoyi.zlyyh.domain.vo.MissionUserVo;
import com.ruoyi.zlyyhadmin.service.IMissionUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 任务用户控制器
 * 前端访问路由地址为:/zlyyh/missionUser
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/missionUser")
public class MissionUserController extends BaseController {

    private final IMissionUserService iMissionUserService;

    /**
     * 查询任务用户列表
     */
    @SaCheckPermission("zlyyh:missionUser:list")
    @GetMapping("/list")
    public TableDataInfo<MissionUserVo> list(MissionUserBo bo, PageQuery pageQuery) {
        return iMissionUserService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出任务用户列表
     */
    @SaCheckPermission("zlyyh:missionUser:export")
    @Log(title = "任务用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MissionUserBo bo, HttpServletResponse response) {
        List<MissionUserVo> list = iMissionUserService.queryList(bo);
        ExcelUtil.exportExcel(list, "任务用户", MissionUserVo.class, response);
    }

    /**
     * 获取任务用户详细信息
     *
     * @param missionUserId 主键
     */
    @SaCheckPermission("zlyyh:missionUser:query")
    @GetMapping("/{missionUserId}")
    public R<MissionUserVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long missionUserId) {
        return R.ok(iMissionUserService.queryById(missionUserId));
    }

    /**
     * 新增任务用户
     */
    @SaCheckPermission("zlyyh:missionUser:add")
    @Log(title = "任务用户", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MissionUserBo bo) {
        return toAjax(iMissionUserService.insertByBo(bo));
    }

    /**
     * 修改任务用户
     */
    @SaCheckPermission("zlyyh:missionUser:edit")
    @Log(title = "任务用户", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MissionUserBo bo) {
        return toAjax(iMissionUserService.updateByBo(bo));
    }

    /**
     * 删除任务用户
     *
     * @param missionUserIds 主键串
     */
    @SaCheckPermission("zlyyh:missionUser:remove")
    @Log(title = "任务用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{missionUserIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] missionUserIds) {
        return toAjax(iMissionUserService.deleteWithValidByIds(Arrays.asList(missionUserIds), true));
    }
}
