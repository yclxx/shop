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
import com.ruoyi.zlyyh.domain.bo.BrowseBo;
import com.ruoyi.zlyyh.domain.vo.BrowseVo;
import com.ruoyi.zlyyhadmin.service.IBrowseService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 浏览任务控制器
 * 前端访问路由地址为:/zlyyh/browse
 *
 * @author yzg
 * @date 2023-12-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/browse")
public class BrowseController extends BaseController {

    private final IBrowseService iBrowseService;

    /**
     * 查询浏览任务列表
     */
    @SaCheckPermission("zlyyh:browse:list")
    @GetMapping("/list")
    public TableDataInfo<BrowseVo> list(BrowseBo bo, PageQuery pageQuery) {
        return iBrowseService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出浏览任务列表
     */
    @SaCheckPermission("zlyyh:browse:export")
    @Log(title = "浏览任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BrowseBo bo, HttpServletResponse response) {
        List<BrowseVo> list = iBrowseService.queryList(bo);
        ExcelUtil.exportExcel(list, "浏览任务", BrowseVo.class, response);
    }

    /**
     * 获取浏览任务详细信息
     *
     * @param browseId 主键
     */
    @SaCheckPermission("zlyyh:browse:query")
    @GetMapping("/{browseId}")
    public R<BrowseVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long browseId) {
        return R.ok(iBrowseService.queryById(browseId));
    }

    /**
     * 新增浏览任务
     */
    @SaCheckPermission("zlyyh:browse:add")
    @Log(title = "浏览任务", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BrowseBo bo) {
        return toAjax(iBrowseService.insertByBo(bo));
    }

    /**
     * 修改浏览任务
     */
    @SaCheckPermission("zlyyh:browse:edit")
    @Log(title = "浏览任务", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BrowseBo bo) {
        return toAjax(iBrowseService.updateByBo(bo));
    }

    /**
     * 删除浏览任务
     *
     * @param browseIds 主键串
     */
    @SaCheckPermission("zlyyh:browse:remove")
    @Log(title = "浏览任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{browseIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] browseIds) {
        return toAjax(iBrowseService.deleteWithValidByIds(Arrays.asList(browseIds), true));
    }
}
