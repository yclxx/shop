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
import com.ruoyi.zlyyh.domain.bo.PageBlockBo;
import com.ruoyi.zlyyh.domain.vo.PageBlockVo;
import com.ruoyi.zlyyhadmin.service.IPageBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 版块模板控制器
 * 前端访问路由地址为:/zlyyh/pageBlock
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/pageBlock")
public class PageBlockController extends BaseController {

    private final IPageBlockService iPageBlockService;

    /**
     * 查询版块模板列表
     */
    @SaCheckPermission("zlyyh:pageBlock:list")
    @GetMapping("/list")
    public TableDataInfo<PageBlockVo> list(PageBlockBo bo, PageQuery pageQuery) {
        return iPageBlockService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询板块模版下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(PageBlockBo bo){
        List<PageBlockVo> pageBlockVos = iPageBlockService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(pageBlockVos, ColumnUtil.getFieldName(PageBlockVo::getId),ColumnUtil.getFieldName(PageBlockVo::getBlockName),null));
    }

    /**
     * 导出版块模板列表
     */
    @SaCheckPermission("zlyyh:pageBlock:export")
    @Log(title = "版块模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PageBlockBo bo, HttpServletResponse response) {
        List<PageBlockVo> list = iPageBlockService.queryList(bo);
        ExcelUtil.exportExcel(list, "版块模板", PageBlockVo.class, response);
    }

    /**
     * 获取版块模板详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:pageBlock:query")
    @GetMapping("/{id}")
    public R<PageBlockVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iPageBlockService.queryById(id));
    }

    /**
     * 新增版块模板
     */
    @SaCheckPermission("zlyyh:pageBlock:add")
    @Log(title = "版块模板", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PageBlockBo bo) {
        return toAjax(iPageBlockService.insertByBo(bo));
    }

    /**
     * 修改版块模板
     */
    @SaCheckPermission("zlyyh:pageBlock:edit")
    @Log(title = "版块模板", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PageBlockBo bo) {
        return toAjax(iPageBlockService.updateByBo(bo));
    }

    /**
     * 删除版块模板
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:pageBlock:remove")
    @Log(title = "版块模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iPageBlockService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
