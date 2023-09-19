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
import com.ruoyi.zlyyh.domain.bo.DistributorBo;
import com.ruoyi.zlyyh.domain.vo.DistributorVo;
import com.ruoyi.zlyyhadmin.service.IDistributorService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 分销商信息控制器
 * 前端访问路由地址为:/zlyyh/distributor
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/distributor")
public class DistributorController extends BaseController {

    private final IDistributorService iDistributorService;

    /**
     * 查询分销商信息列表
     */
    @SaCheckPermission("zlyyh:distributor:list")
    @GetMapping("/list")
    public TableDataInfo<DistributorVo> list(DistributorBo bo, PageQuery pageQuery) {
        return iDistributorService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询分销商下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(DistributorBo bo) {
        List<DistributorVo> distributorVos = iDistributorService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(distributorVos, ColumnUtil.getFieldName(DistributorVo::getDistributorId), ColumnUtil.getFieldName(DistributorVo::getDistributorName), null));
    }

    /**
     * 导出分销商信息列表
     */
    @SaCheckPermission("zlyyh:distributor:export")
    @Log(title = "分销商信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DistributorBo bo, HttpServletResponse response) {
        List<DistributorVo> list = iDistributorService.queryList(bo);
        ExcelUtil.exportExcel(list, "分销商信息", DistributorVo.class, response);
    }

    /**
     * 获取分销商信息详细信息
     *
     * @param distributorId 主键
     */
    @SaCheckPermission("zlyyh:distributor:query")
    @GetMapping("/{distributorId}")
    public R<DistributorVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable String distributorId) {
        return R.ok(iDistributorService.queryById(distributorId));
    }

    /**
     * 新增分销商信息
     */
    @SaCheckPermission("zlyyh:distributor:add")
    @Log(title = "分销商信息", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DistributorBo bo) {
        return toAjax(iDistributorService.insertByBo(bo));
    }

    /**
     * 修改分销商信息
     */
    @SaCheckPermission("zlyyh:distributor:edit")
    @Log(title = "分销商信息", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DistributorBo bo) {
        return toAjax(iDistributorService.updateByBo(bo));
    }

    /**
     * 删除分销商信息
     *
     * @param distributorIds 主键串
     */
    @SaCheckPermission("zlyyh:distributor:remove")
    @Log(title = "分销商信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{distributorIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] distributorIds) {
        return toAjax(iDistributorService.deleteWithValidByIds(Arrays.asList(distributorIds), true));
    }
}
