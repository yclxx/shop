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
import com.ruoyi.zlyyhadmin.service.ITagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.TagsVo;
import com.ruoyi.zlyyh.domain.bo.TagsBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 标签控制器
 * 前端访问路由地址为:/zlyyh/tags
 *
 * @author yzg
 * @date 2023-10-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tags")
public class TagsController extends BaseController {

    private final ITagsService iTagsService;

    /**
     * 查询标签列表
     */
    @SaCheckPermission("zlyyh:tags:list")
    @GetMapping("/list")
    public TableDataInfo<TagsVo> list(TagsBo bo, PageQuery pageQuery) {
        return iTagsService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询标签列表
     */
    //@SaCheckPermission("zlyyh:tags:l")
    //@Log(title = "标签", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public R<List<TagsVo>> export(TagsBo bo, HttpServletResponse response) {
        List<TagsVo> list = iTagsService.queryList(bo);
        return R.ok(list);
    }

    /**
     * 获取标签详细信息
     *
     * @param tagsId 主键
     */
    @SaCheckPermission("zlyyh:tags:query")
    @GetMapping("/{tagsId}")
    public R<TagsVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long tagsId) {
        return R.ok(iTagsService.queryById(tagsId));
    }

    /**
     * 新增标签
     */
    @SaCheckPermission("zlyyh:tags:add")
    @Log(title = "标签", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TagsBo bo) {
        return toAjax(iTagsService.insertByBo(bo));
    }

    /**
     * 修改标签
     */
    @SaCheckPermission("zlyyh:tags:edit")
    @Log(title = "标签", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TagsBo bo) {
        return toAjax(iTagsService.updateByBo(bo));
    }

    /**
     * 删除标签
     *
     * @param tagsIds 主键串
     */
    @SaCheckPermission("zlyyh:tags:remove")
    @Log(title = "标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tagsIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] tagsIds) {
        return toAjax(iTagsService.deleteWithValidByIds(Arrays.asList(tagsIds), true));
    }
}
