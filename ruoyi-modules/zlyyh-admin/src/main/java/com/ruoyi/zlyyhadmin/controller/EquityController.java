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
import com.ruoyi.zlyyh.domain.bo.EquityBo;
import com.ruoyi.zlyyh.domain.vo.EquityVo;
import com.ruoyi.zlyyhadmin.service.IEquityService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 权益包控制器
 * 前端访问路由地址为:/zlyyh/equity
 *
 * @author yzg
 * @date 2023-06-06
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/equity")
public class EquityController extends BaseController {

    private final IEquityService iEquityService;

    /**
     * 查询权益包列表
     */
    @SaCheckPermission("zlyyh:equity:list")
    @GetMapping("/list")
    public TableDataInfo<EquityVo> list(EquityBo bo, PageQuery pageQuery) {
        return iEquityService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(EquityBo bo) {
        List<EquityVo> equityVos = iEquityService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(equityVos, ColumnUtil.getFieldName(EquityVo::getEquityId), ColumnUtil.getFieldName(EquityVo::getEquityName), null));
    }

    /**
     * 导出权益包列表
     */
    @SaCheckPermission("zlyyh:equity:export")
    @Log(title = "权益包", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(EquityBo bo, HttpServletResponse response) {
        List<EquityVo> list = iEquityService.queryList(bo);
        ExcelUtil.exportExcel(list, "权益包", EquityVo.class, response);
    }

    /**
     * 获取权益包详细信息
     *
     * @param equityId 主键
     */
    @SaCheckPermission("zlyyh:equity:query")
    @GetMapping("/{equityId}")
    public R<EquityVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long equityId) {
        return R.ok(iEquityService.queryById(equityId));
    }

    /**
     * 新增权益包
     */
    @SaCheckPermission("zlyyh:equity:add")
    @Log(title = "权益包", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody EquityBo bo) {
        return toAjax(iEquityService.insertByBo(bo));
    }

    /**
     * 修改权益包
     */
    @SaCheckPermission("zlyyh:equity:edit")
    @Log(title = "权益包", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody EquityBo bo) {
        return toAjax(iEquityService.updateByBo(bo));
    }

    /**
     * 删除权益包
     *
     * @param equityIds 主键串
     */
    @SaCheckPermission("zlyyh:equity:remove")
    @Log(title = "权益包", businessType = BusinessType.DELETE)
    @DeleteMapping("/{equityIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] equityIds) {
        return toAjax(iEquityService.deleteWithValidByIds(Arrays.asList(equityIds), true));
    }
}
