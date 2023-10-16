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
import com.ruoyi.zlyyh.domain.bo.ActionBo;
import com.ruoyi.zlyyh.domain.vo.ActionVo;
import com.ruoyi.zlyyhadmin.service.IActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 优惠券批次控制器
 * 前端访问路由地址为:/zlyyh/action
 *
 * @author yzg
 * @date 2023-10-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/action")
public class ActionController extends BaseController {

    private final IActionService iActionService;

    /**
     * 查询优惠券批次列表
     */
    @SaCheckPermission("zlyyh:action:list")
    @GetMapping("/list")
    public TableDataInfo<ActionVo> list(ActionBo bo, PageQuery pageQuery) {
        return iActionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出优惠券批次列表
     */
    @SaCheckPermission("zlyyh:action:export")
    @Log(title = "优惠券批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ActionBo bo, HttpServletResponse response) {
        List<ActionVo> list = iActionService.queryList(bo);
        ExcelUtil.exportExcel(list, "优惠券批次", ActionVo.class, response);
    }

    /**
     * 获取优惠券批次详细信息
     *
     * @param actionId 主键
     */
    @SaCheckPermission("zlyyh:action:query")
    @GetMapping("/{actionId}")
    public R<ActionVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long actionId) {
        return R.ok(iActionService.queryById(actionId));
    }

    /**
     * 新增优惠券批次
     */
    @SaCheckPermission("zlyyh:action:add")
    @Log(title = "优惠券批次", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ActionBo bo) {
        return toAjax(iActionService.insertByBo(bo));
    }

    /**
     * 修改优惠券批次
     */
    @SaCheckPermission("zlyyh:action:edit")
    @Log(title = "优惠券批次", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ActionBo bo) {
        return toAjax(iActionService.updateByBo(bo));
    }

    /**
     * 删除优惠券批次
     *
     * @param actionIds 主键串
     */
    @SaCheckPermission("zlyyh:action:remove")
    @Log(title = "优惠券批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{actionIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] actionIds) {
        return toAjax(iActionService.deleteWithValidByIds(Arrays.asList(actionIds), true));
    }
}
