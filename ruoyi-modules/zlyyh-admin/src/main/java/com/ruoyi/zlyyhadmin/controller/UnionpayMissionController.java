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
import com.ruoyi.zlyyh.domain.bo.PlatformBo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionBo;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 银联任务配置控制器
 * 前端访问路由地址为:/zlyyh/unionpayMission
 *
 * @author yzg
 * @date 2024-02-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionpayMission")
public class UnionpayMissionController extends BaseController {

    private final IUnionpayMissionService iUnionpayMissionService;

    /**
     * 查询银联任务配置列表
     */
    @SaCheckPermission("zlyyh:unionpayMission:list")
    @GetMapping("/list")
    public TableDataInfo<UnionpayMissionVo> list(UnionpayMissionBo bo, PageQuery pageQuery) {
        return iUnionpayMissionService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询任务下拉列表
     */
    @GetMapping("/selectMissionList")
    public R<List<SelectListEntity>> selectMissionList(UnionpayMissionBo bo){
        List<UnionpayMissionVo> missionVoList = iUnionpayMissionService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(missionVoList, ColumnUtil.getFieldName(UnionpayMissionVo::getUpMissionId),ColumnUtil.getFieldName(UnionpayMissionVo::getUpMissionName),null));
    }

    /**
     * 导出银联任务配置列表
     */
    @SaCheckPermission("zlyyh:unionpayMission:export")
    @Log(title = "银联任务配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionpayMissionBo bo, HttpServletResponse response) {
        List<UnionpayMissionVo> list = iUnionpayMissionService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联任务配置", UnionpayMissionVo.class, response);
    }

    /**
     * 获取银联任务配置详细信息
     *
     * @param upMissionId 主键
     */
    @SaCheckPermission("zlyyh:unionpayMission:query")
    @GetMapping("/{upMissionId}")
    public R<UnionpayMissionVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long upMissionId) {
        return R.ok(iUnionpayMissionService.queryById(upMissionId));
    }

    /**
     * 新增银联任务配置
     */
    @SaCheckPermission("zlyyh:unionpayMission:add")
    @Log(title = "银联任务配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionpayMissionBo bo) {
        return toAjax(iUnionpayMissionService.insertByBo(bo));
    }

    /**
     * 修改银联任务配置
     */
    @SaCheckPermission("zlyyh:unionpayMission:edit")
    @Log(title = "银联任务配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionpayMissionBo bo) {
        return toAjax(iUnionpayMissionService.updateByBo(bo));
    }

    /**
     * 删除银联任务配置
     *
     * @param upMissionIds 主键串
     */
    @SaCheckPermission("zlyyh:unionpayMission:remove")
    @Log(title = "银联任务配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{upMissionIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] upMissionIds) {
        return toAjax(iUnionpayMissionService.deleteWithValidByIds(Arrays.asList(upMissionIds), true));
    }
}
