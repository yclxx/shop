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
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyhadmin.service.IBusinessDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.BusinessDistrictVo;
import com.ruoyi.zlyyh.domain.bo.BusinessDistrictBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 商圈控制器
 * 前端访问路由地址为:/zlyyh/businessDistrict
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/businessDistrict")
public class BusinessDistrictController extends BaseController {

    private final IBusinessDistrictService iBusinessDistrictService;

    /**
     * 查询商圈列表
     */
    @SaCheckPermission("zlyyh:businessDistrict:list")
    @GetMapping("/list")
    public TableDataInfo<BusinessDistrictVo> list(BusinessDistrictBo bo, PageQuery pageQuery) {
        return iBusinessDistrictService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询商户下拉列表
     */
    @GetMapping("/selectListBusinessDistrict")
    public R<List<SelectListEntity>> selectListBusinessDistrict(BusinessDistrictBo bo){
        List<BusinessDistrictVo> businessDistrictVos = iBusinessDistrictService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(businessDistrictVos, ColumnUtil.getFieldName(BusinessDistrictVo::getBusinessDistrictId),ColumnUtil.getFieldName(BusinessDistrictVo::getBusinessDistrictName),null));
    }

    /**
     * 导出商圈列表
     */
    @SaCheckPermission("zlyyh:businessDistrict:export")
    @Log(title = "商圈", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BusinessDistrictBo bo, HttpServletResponse response) {
        List<BusinessDistrictVo> list = iBusinessDistrictService.queryList(bo);
        ExcelUtil.exportExcel(list, "商圈", BusinessDistrictVo.class, response);
    }

    /**
     * 获取商圈详细信息
     *
     * @param businessDistrictId 主键
     */
    @SaCheckPermission("zlyyh:businessDistrict:query")
    @GetMapping("/{businessDistrictId}")
    public R<BusinessDistrictVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long businessDistrictId) {
        return R.ok(iBusinessDistrictService.queryById(businessDistrictId));
    }

    /**
     * 新增商圈
     */
    @SaCheckPermission("zlyyh:businessDistrict:add")
    @Log(title = "商圈", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BusinessDistrictBo bo) {
        return toAjax(iBusinessDistrictService.insertByBo(bo));
    }

    /**
     * 修改商圈
     */
    @SaCheckPermission("zlyyh:businessDistrict:edit")
    @Log(title = "商圈", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BusinessDistrictBo bo) {
        return toAjax(iBusinessDistrictService.updateByBo(bo));
    }

    /**
     * 删除商圈
     *
     * @param businessDistrictIds 主键串
     */
    @SaCheckPermission("zlyyh:businessDistrict:remove")
    @Log(title = "商圈", businessType = BusinessType.DELETE)
    @DeleteMapping("/{businessDistrictIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] businessDistrictIds) {
        return toAjax(iBusinessDistrictService.deleteWithValidByIds(Arrays.asList(businessDistrictIds), true));
    }
}
