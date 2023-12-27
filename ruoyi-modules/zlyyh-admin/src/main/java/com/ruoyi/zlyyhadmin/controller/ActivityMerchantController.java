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
import com.ruoyi.zlyyh.domain.bo.ActivityMerchantBo;
import com.ruoyi.zlyyh.domain.vo.ActivityMerchantVo;
import com.ruoyi.zlyyhadmin.service.IActivityMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 活动商户号控制器
 * 前端访问路由地址为:/zlyyh/activityMerchant
 *
 * @author yzg
 * @date 2023-12-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/activityMerchant")
public class ActivityMerchantController extends BaseController {

    private final IActivityMerchantService iActivityMerchantService;

    /**
     * 查询活动商户号列表
     */
    @SaCheckPermission("zlyyh:activityMerchant:list")
    @GetMapping("/list")
    public TableDataInfo<ActivityMerchantVo> list(ActivityMerchantBo bo, PageQuery pageQuery) {
        return iActivityMerchantService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出活动商户号列表
     */
    @SaCheckPermission("zlyyh:activityMerchant:export")
    @Log(title = "活动商户号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ActivityMerchantBo bo, HttpServletResponse response) {
        List<ActivityMerchantVo> list = iActivityMerchantService.queryList(bo);
        ExcelUtil.exportExcel(list, "活动商户号", ActivityMerchantVo.class, response);
    }

    /**
     * 获取活动商户号详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:activityMerchant:query")
    @GetMapping("/{id}")
    public R<ActivityMerchantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iActivityMerchantService.queryById(id));
    }

    /**
     * 新增活动商户号
     */
    @SaCheckPermission("zlyyh:activityMerchant:add")
    @Log(title = "活动商户号", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ActivityMerchantBo bo) {
        return toAjax(iActivityMerchantService.insertByBo(bo));
    }

    /**
     * 修改活动商户号
     */
    @SaCheckPermission("zlyyh:activityMerchant:edit")
    @Log(title = "活动商户号", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ActivityMerchantBo bo) {
        return toAjax(iActivityMerchantService.updateByBo(bo));
    }

    /**
     * 删除活动商户号
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:activityMerchant:remove")
    @Log(title = "活动商户号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iActivityMerchantService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
