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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionGroupBgVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionGroupBgBo;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionGroupBgService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 任务组背景控制器
 * 前端访问路由地址为:/zlyyh/unionpayMissionGroupBg
 *
 * @author yzg
 * @date 2024-03-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionpayMissionGroupBg")
public class UnionpayMissionGroupBgController extends BaseController {

    private final IUnionpayMissionGroupBgService iUnionpayMissionGroupBgService;

    /**
     * 查询任务组背景列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroupBg:list")
    @GetMapping("/list")
    public TableDataInfo<UnionpayMissionGroupBgVo> list(UnionpayMissionGroupBgBo bo, PageQuery pageQuery) {
        return iUnionpayMissionGroupBgService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出任务组背景列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroupBg:export")
    @Log(title = "任务组背景", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionpayMissionGroupBgBo bo, HttpServletResponse response) {
        List<UnionpayMissionGroupBgVo> list = iUnionpayMissionGroupBgService.queryList(bo);
        ExcelUtil.exportExcel(list, "任务组背景", UnionpayMissionGroupBgVo.class, response);
    }

    /**
     * 获取任务组背景详细信息
     *
     * @param missionBgId 主键
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroupBg:query")
    @GetMapping("/{missionBgId}")
    public R<UnionpayMissionGroupBgVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long missionBgId) {
        return R.ok(iUnionpayMissionGroupBgService.queryById(missionBgId));
    }

    /**
     * 新增任务组背景
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroupBg:add")
    @Log(title = "任务组背景", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionpayMissionGroupBgBo bo) {
        return toAjax(iUnionpayMissionGroupBgService.insertByBo(bo));
    }

    /**
     * 修改任务组背景
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroupBg:edit")
    @Log(title = "任务组背景", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionpayMissionGroupBgBo bo) {
        return toAjax(iUnionpayMissionGroupBgService.updateByBo(bo));
    }

    /**
     * 删除任务组背景
     *
     * @param missionBgIds 主键串
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroupBg:remove")
    @Log(title = "任务组背景", businessType = BusinessType.DELETE)
    @DeleteMapping("/{missionBgIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] missionBgIds) {
        return toAjax(iUnionpayMissionGroupBgService.deleteWithValidByIds(Arrays.asList(missionBgIds), true));
    }
}
