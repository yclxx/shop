package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.zlyyhadmin.service.ISearchGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.SearchGroupVo;
import com.ruoyi.zlyyh.domain.bo.SearchGroupBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 搜索彩蛋配置控制器
 * 前端访问路由地址为:/zlyyh/searchGroup
 *
 * @author yzg
 * @date 2023-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/searchGroup")
public class SearchGroupController extends BaseController {

    private final ISearchGroupService iSearchGroupService;

    /**
     * 查询搜索彩蛋配置列表
     */
    @SaCheckPermission("zlyyh:searchGroup:list")
    @GetMapping("/list")
    public TableDataInfo<SearchGroupVo> list(SearchGroupBo bo, PageQuery pageQuery) {
        return iSearchGroupService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出搜索彩蛋配置列表
     */
    @SaCheckPermission("zlyyh:searchGroup:export")
    @Log(title = "搜索彩蛋配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SearchGroupBo bo, HttpServletResponse response) {
        List<SearchGroupVo> list = iSearchGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "搜索彩蛋配置", SearchGroupVo.class, response);
    }

    /**
     * 获取搜索彩蛋配置详细信息
     *
     * @param searchId 主键
     */
    @SaCheckPermission("zlyyh:searchGroup:query")
    @GetMapping("/{searchId}")
    public R<SearchGroupVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long searchId) {
        return R.ok(iSearchGroupService.queryById(searchId));
    }

    /**
     * 新增搜索彩蛋配置
     */
    @SaCheckPermission("zlyyh:searchGroup:add")
    @Log(title = "搜索彩蛋配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SearchGroupBo bo) {
        return toAjax(iSearchGroupService.insertByBo(bo));
    }

    /**
     * 修改搜索彩蛋配置
     */
    @SaCheckPermission("zlyyh:searchGroup:edit")
    @Log(title = "搜索彩蛋配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SearchGroupBo bo) {
        return toAjax(iSearchGroupService.updateByBo(bo));
    }

    /**
     * 删除搜索彩蛋配置
     *
     * @param searchIds 主键串
     */
    @SaCheckPermission("zlyyh:searchGroup:remove")
    @Log(title = "搜索彩蛋配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{searchIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] searchIds) {
        return toAjax(iSearchGroupService.deleteWithValidByIds(Arrays.asList(searchIds), true));
    }
}
