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
import com.ruoyi.zlyyh.domain.bo.CodeBo;
import com.ruoyi.zlyyh.domain.vo.CodeVo;
import com.ruoyi.zlyyhadmin.service.ICodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 商品券码控制器
 * 前端访问路由地址为:/zlyyh/code
 *
 * @author yzg
 * @date 2023-09-20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/code")
public class CodeController extends BaseController {

    private final ICodeService iCodeService;

    /**
     * 查询商品券码列表
     */
    @SaCheckPermission("zlyyh:code:list")
    @GetMapping("/list")
    public TableDataInfo<CodeVo> list(CodeBo bo, PageQuery pageQuery) {
        return iCodeService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商品券码列表
     */
    @SaCheckPermission("zlyyh:code:export")
    @Log(title = "商品券码", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CodeBo bo, HttpServletResponse response) {
        List<CodeVo> list = iCodeService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品券码", CodeVo.class, response);
    }

    /**
     * 获取商品券码详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:code:query")
    @GetMapping("/{id}")
    public R<CodeVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iCodeService.queryById(id));
    }

    /**
     * 新增商品券码
     */
    @SaCheckPermission("zlyyh:code:add")
    @Log(title = "商品券码", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CodeBo bo) {
        return toAjax(iCodeService.insertByBo(bo));
    }

    /**
     * 修改商品券码
     */
    @SaCheckPermission("zlyyh:code:edit")
    @Log(title = "商品券码", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CodeBo bo) {
        return toAjax(iCodeService.updateByBo(bo));
    }

    /**
     * 删除商品券码
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:code:remove")
    @Log(title = "商品券码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iCodeService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
