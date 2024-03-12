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
import com.ruoyi.zlyyhadmin.service.IThirdPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ThirdPlatformVo;
import com.ruoyi.zlyyh.domain.bo.ThirdPlatformBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 第三方平台信息配置控制器
 * 前端访问路由地址为:/zlyyh/thirdPlatform
 *
 * @author yzg
 * @date 2024-03-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thirdPlatform")
public class ThirdPlatformController extends BaseController {

    private final IThirdPlatformService iThirdPlatformService;

    /**
     * 查询第三方平台信息配置列表
     */
    @SaCheckPermission("zlyyh:thirdPlatform:list")
    @GetMapping("/list")
    public TableDataInfo<ThirdPlatformVo> list(ThirdPlatformBo bo, PageQuery pageQuery) {
        return iThirdPlatformService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出第三方平台信息配置列表
     */
    @SaCheckPermission("zlyyh:thirdPlatform:export")
    @Log(title = "第三方平台信息配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ThirdPlatformBo bo, HttpServletResponse response) {
        List<ThirdPlatformVo> list = iThirdPlatformService.queryList(bo);
        ExcelUtil.exportExcel(list, "第三方平台信息配置", ThirdPlatformVo.class, response);
    }

    /**
     * 获取第三方平台信息配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:thirdPlatform:query")
    @GetMapping("/{id}")
    public R<ThirdPlatformVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iThirdPlatformService.queryById(id));
    }

    /**
     * 新增第三方平台信息配置
     */
    @SaCheckPermission("zlyyh:thirdPlatform:add")
    @Log(title = "第三方平台信息配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ThirdPlatformBo bo) {
        return toAjax(iThirdPlatformService.insertByBo(bo));
    }

    /**
     * 修改第三方平台信息配置
     */
    @SaCheckPermission("zlyyh:thirdPlatform:edit")
    @Log(title = "第三方平台信息配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ThirdPlatformBo bo) {
        return toAjax(iThirdPlatformService.updateByBo(bo));
    }

    /**
     * 删除第三方平台信息配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:thirdPlatform:remove")
    @Log(title = "第三方平台信息配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iThirdPlatformService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
