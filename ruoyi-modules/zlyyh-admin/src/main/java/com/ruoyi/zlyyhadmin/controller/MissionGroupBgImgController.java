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
import com.ruoyi.zlyyhadmin.service.IMissionGroupBgImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.MissionGroupBgImgVo;
import com.ruoyi.zlyyh.domain.bo.MissionGroupBgImgBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 任务组背景图片配置控制器
 * 前端访问路由地址为:/zlyyh/missionGroupBgImg
 *
 * @author yzg
 * @date 2024-01-03
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/missionGroupBgImg")
public class MissionGroupBgImgController extends BaseController {

    private final IMissionGroupBgImgService iMissionGroupBgImgService;

    /**
     * 查询任务组背景图片配置列表
     */
    @SaCheckPermission("zlyyh:missionGroupBgImg:list")
    @GetMapping("/list")
    public TableDataInfo<MissionGroupBgImgVo> list(MissionGroupBgImgBo bo, PageQuery pageQuery) {
        return iMissionGroupBgImgService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出任务组背景图片配置列表
     */
    @SaCheckPermission("zlyyh:missionGroupBgImg:export")
    @Log(title = "任务组背景图片配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MissionGroupBgImgBo bo, HttpServletResponse response) {
        List<MissionGroupBgImgVo> list = iMissionGroupBgImgService.queryList(bo);
        ExcelUtil.exportExcel(list, "任务组背景图片配置", MissionGroupBgImgVo.class, response);
    }

    /**
     * 获取任务组背景图片配置详细信息
     *
     * @param missionGroupId 主键
     */
    @SaCheckPermission("zlyyh:missionGroupBgImg:query")
    @GetMapping("/{missionGroupId}")
    public R<MissionGroupBgImgVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long missionGroupId) {
        return R.ok(iMissionGroupBgImgService.queryById(missionGroupId));
    }

    /**
     * 新增任务组背景图片配置
     */
    @SaCheckPermission("zlyyh:missionGroupBgImg:add")
    @Log(title = "任务组背景图片配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MissionGroupBgImgBo bo) {
        return toAjax(iMissionGroupBgImgService.insertByBo(bo));
    }

    /**
     * 修改任务组背景图片配置
     */
    @SaCheckPermission("zlyyh:missionGroupBgImg:edit")
    @Log(title = "任务组背景图片配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MissionGroupBgImgBo bo) {
        return toAjax(iMissionGroupBgImgService.updateByBo(bo));
    }

    /**
     * 删除任务组背景图片配置
     *
     * @param missionGroupIds 主键串
     */
    @SaCheckPermission("zlyyh:missionGroupBgImg:remove")
    @Log(title = "任务组背景图片配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{missionGroupIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] missionGroupIds) {
        return toAjax(iMissionGroupBgImgService.deleteWithValidByIds(Arrays.asList(missionGroupIds), true));
    }
}
