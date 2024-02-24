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
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.bo.VerifierBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;
import com.ruoyi.zlyyhadmin.service.IVerifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 核销人员管理控制器
 * 前端访问路由地址为:/zlyyh/verifier
 *
 * @author yzg
 * @date 2023-11-06
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/verifier")
public class VerifierController extends BaseController {

    private final IVerifierService iVerifierService;

    /**
     * 查询核销人员管理列表
     */
    @SaCheckPermission("zlyyh:verifier:list")
    @GetMapping("/list")
    public TableDataInfo<VerifierVo> list(VerifierBo bo, PageQuery pageQuery) {
        return iVerifierService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询核销员下拉列表
     */
    @GetMapping("/selectListVerifier")
    public R<List<SelectListEntity>> selectListVerifier(VerifierBo bo){
        List<VerifierVo> verifierVos = iVerifierService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(verifierVos, ColumnUtil.getFieldName(VerifierVo::getId),ColumnUtil.getFieldName(VerifierVo::getMobile),ColumnUtil.getFieldName(VerifierVo::getUsername)));
    }

    /**
     * 导出核销人员管理列表
     */
    @SaCheckPermission("zlyyh:verifier:export")
    @Log(title = "核销人员管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(VerifierBo bo, HttpServletResponse response) {
        List<VerifierVo> list = iVerifierService.queryList(bo);
        ExcelUtil.exportExcel(list, "核销人员管理", VerifierVo.class, response);
    }

    /**
     * 获取核销人员管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:verifier:query")
    @GetMapping("/{id}")
    public R<VerifierVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iVerifierService.queryById(id));
    }

    /**
     * 新增核销人员管理
     */
    @SaCheckPermission("zlyyh:verifier:add")
    @Log(title = "核销人员管理", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody VerifierBo bo) {
        return toAjax(iVerifierService.insertByBo(bo));
    }

    /**
     * 修改核销人员管理
     */
    @SaCheckPermission("zlyyh:verifier:edit")
    @Log(title = "核销人员管理", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody VerifierBo bo) {
        return toAjax(iVerifierService.updateByBo(bo));
    }

    /**
     * 删除核销人员管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:verifier:remove")
    @Log(title = "核销人员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iVerifierService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
