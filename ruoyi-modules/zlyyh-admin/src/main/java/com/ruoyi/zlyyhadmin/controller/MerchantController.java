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
import com.ruoyi.zlyyh.domain.bo.MerchantBo;
import com.ruoyi.zlyyh.domain.vo.MerchantVo;
import com.ruoyi.zlyyhadmin.service.IMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 商户号控制器
 * 前端访问路由地址为:/zlyyh/merchant
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/merchant")
public class MerchantController extends BaseController {

    private final IMerchantService iMerchantService;

    /**
     * 查询商户号列表
     */
    @SaCheckPermission("zlyyh:merchant:list")
    @GetMapping("/list")
    public TableDataInfo<MerchantVo> list(MerchantBo bo, PageQuery pageQuery) {
        return iMerchantService.queryPageList(bo, pageQuery);
    }

    /**
     * 商户下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> merchantSelectList(MerchantBo bo){
        List<MerchantVo> merchantVos = iMerchantService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(merchantVos, ColumnUtil.getFieldName(MerchantVo::getId),ColumnUtil.getFieldName(MerchantVo::getMerchantNo),ColumnUtil.getFieldName(MerchantVo::getMerchantName)));
    }

    /**
     * 导出商户号列表
     */
    @SaCheckPermission("zlyyh:merchant:export")
    @Log(title = "商户号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MerchantBo bo, HttpServletResponse response) {
        List<MerchantVo> list = iMerchantService.queryList(bo);
        ExcelUtil.exportExcel(list, "商户号", MerchantVo.class, response);
    }

    /**
     * 获取商户号详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:merchant:query")
    @GetMapping("/{id}")
    public R<MerchantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iMerchantService.queryById(id));
    }

    /**
     * 新增商户号
     */
    @SaCheckPermission("zlyyh:merchant:add")
    @Log(title = "商户号", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MerchantBo bo) {
        return toAjax(iMerchantService.insertByBo(bo));
    }

    /**
     * 修改商户号
     */
    @SaCheckPermission("zlyyh:merchant:edit")
    @Log(title = "商户号", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MerchantBo bo) {
        return toAjax(iMerchantService.updateByBo(bo));
    }

    /**
     * 删除商户号
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:merchant:remove")
    @Log(title = "商户号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iMerchantService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
