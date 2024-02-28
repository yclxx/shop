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
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionGroupVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionGroupBo;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionGroupService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 银联任务组控制器
 * 前端访问路由地址为:/zlyyh/unionpayMissionGroup
 *
 * @author yzg
 * @date 2024-02-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionpayMissionGroup")
public class UnionpayMissionGroupController extends BaseController {

    private final IUnionpayMissionGroupService iUnionpayMissionGroupService;

    /**
     * 查询银联任务组列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroup:list")
    @GetMapping("/list")
    public TableDataInfo<UnionpayMissionGroupVo> list(UnionpayMissionGroupBo bo, PageQuery pageQuery) {
        return iUnionpayMissionGroupService.queryPageList(bo, pageQuery);
    }


    /**
     * 查询下拉列表  (任务组)
     */
    @GetMapping("/selectMissionGroupList")
    public R<List<SelectListEntity>> selectMissionGroupList(UnionpayMissionGroupBo bo){
        List<UnionpayMissionGroupVo> missionGroupVoList = iUnionpayMissionGroupService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(missionGroupVoList, ColumnUtil.getFieldName(UnionpayMissionGroupVo::getUpMissionGroupId),ColumnUtil.getFieldName(UnionpayMissionGroupVo::getUpMissionGroupName),null));
    }

    /**
     * 导出银联任务组列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroup:export")
    @Log(title = "银联任务组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionpayMissionGroupBo bo, HttpServletResponse response) {
        List<UnionpayMissionGroupVo> list = iUnionpayMissionGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联任务组", UnionpayMissionGroupVo.class, response);
    }

    /**
     * 获取银联任务组详细信息
     *
     * @param upMissionGroupId 主键
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroup:query")
    @GetMapping("/{upMissionGroupId}")
    public R<UnionpayMissionGroupVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long upMissionGroupId) {
        return R.ok(iUnionpayMissionGroupService.queryById(upMissionGroupId));
    }

    /**
     * 新增银联任务组
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroup:add")
    @Log(title = "银联任务组", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionpayMissionGroupBo bo) {
        return toAjax(iUnionpayMissionGroupService.insertByBo(bo));
    }

    /**
     * 修改银联任务组
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroup:edit")
    @Log(title = "银联任务组", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionpayMissionGroupBo bo) {
        return toAjax(iUnionpayMissionGroupService.updateByBo(bo));
    }

    /**
     * 删除银联任务组
     *
     * @param upMissionGroupIds 主键串
     */
    @SaCheckPermission("zlyyh:unionpayMissionGroup:remove")
    @Log(title = "银联任务组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{upMissionGroupIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] upMissionGroupIds) {
        return toAjax(iUnionpayMissionGroupService.deleteWithValidByIds(Arrays.asList(upMissionGroupIds), true));
    }
}
