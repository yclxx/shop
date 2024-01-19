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
import com.ruoyi.zlyyh.mapper.MerchantTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.MerchantTypeVo;
import com.ruoyi.zlyyh.domain.bo.MerchantTypeBo;
import com.ruoyi.zlyyhadmin.service.IMerchantTypeService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 商户门店类别控制器
 * 前端访问路由地址为:/zlyyh/merchantType
 *
 * @author yzg
 * @date 2024-01-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/merchantType")
public class MerchantTypeController extends BaseController {

    private final IMerchantTypeService iMerchantTypeService;

    /**
     * 查询商户门店类别列表
     */
    @SaCheckPermission("zlyyh:merchantType:list")
    @GetMapping("/list")
    public TableDataInfo<MerchantTypeVo> list(MerchantTypeBo bo, PageQuery pageQuery) {
        return iMerchantTypeService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询商户类别下拉列表
     */
    @GetMapping("/selectMerTypeList")
    public R<List<SelectListEntity>> selectMerTypeList(MerchantTypeBo bo){
        List<MerchantTypeVo> merchantTypeVoList = iMerchantTypeService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(merchantTypeVoList, ColumnUtil.getFieldName(MerchantTypeVo::getMerchantTypeId),ColumnUtil.getFieldName(MerchantTypeVo::getTypeName),null));
    }

    /**
     * 导出商户门店类别列表
     */
    @SaCheckPermission("zlyyh:merchantType:export")
    @Log(title = "商户门店类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MerchantTypeBo bo, HttpServletResponse response) {
        List<MerchantTypeVo> list = iMerchantTypeService.queryList(bo);
        ExcelUtil.exportExcel(list, "商户门店类别", MerchantTypeVo.class, response);
    }

    /**
     * 获取商户门店类别详细信息
     *
     * @param merchantTypeId 主键
     */
    @SaCheckPermission("zlyyh:merchantType:query")
    @GetMapping("/{merchantTypeId}")
    public R<MerchantTypeVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long merchantTypeId) {
        return R.ok(iMerchantTypeService.queryById(merchantTypeId));
    }

    /**
     * 新增商户门店类别
     */
    @SaCheckPermission("zlyyh:merchantType:add")
    @Log(title = "商户门店类别", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MerchantTypeBo bo) {
        return toAjax(iMerchantTypeService.insertByBo(bo));
    }

    /**
     * 修改商户门店类别
     */
    @SaCheckPermission("zlyyh:merchantType:edit")
    @Log(title = "商户门店类别", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MerchantTypeBo bo) {
        return toAjax(iMerchantTypeService.updateByBo(bo));
    }

    /**
     * 删除商户门店类别
     *
     * @param merchantTypeIds 主键串
     */
    @SaCheckPermission("zlyyh:merchantType:remove")
    @Log(title = "商户门店类别", businessType = BusinessType.DELETE)
    @DeleteMapping("/{merchantTypeIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] merchantTypeIds) {
        return toAjax(iMerchantTypeService.deleteWithValidByIds(Arrays.asList(merchantTypeIds), true));
    }
}
