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
import com.ruoyi.zlyyh.domain.bo.LianlianCityBo;
import com.ruoyi.zlyyh.domain.vo.LianlianCityVo;
import com.ruoyi.zlyyhadmin.service.ILianlianCityService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 联联市级城市控制器
 * 前端访问路由地址为:/zlyyh/lianlianCity
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/lianlianCity")
public class LianlianCityController extends BaseController {

    private final ILianlianCityService iLianlianCityService;

    /**
     * 查询联联市级城市列表
     */
    @SaCheckPermission("zlyyh:lianlianCity:list")
    @GetMapping("/list")
    public TableDataInfo<LianlianCityVo> list(LianlianCityBo bo, PageQuery pageQuery) {
        return iLianlianCityService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出联联市级城市列表
     */
    @SaCheckPermission("zlyyh:lianlianCity:export")
    @Log(title = "联联市级城市", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(LianlianCityBo bo, HttpServletResponse response) {
        List<LianlianCityVo> list = iLianlianCityService.queryList(bo);
        ExcelUtil.exportExcel(list, "联联市级城市", LianlianCityVo.class, response);
    }

    /**
     * 获取联联市级城市详细信息
     *
     * @param cityId 主键
     */
    @SaCheckPermission("zlyyh:lianlianCity:query")
    @GetMapping("/{cityId}")
    public R<LianlianCityVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long cityId) {
        return R.ok(iLianlianCityService.queryById(cityId));
    }

    /**
     * 新增联联市级城市
     */
    @SaCheckPermission("zlyyh:lianlianCity:add")
    @Log(title = "联联市级城市", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody LianlianCityBo bo) {
        return toAjax(iLianlianCityService.insertByBo(bo));
    }

    /**
     * 修改联联市级城市
     */
    @SaCheckPermission("zlyyh:lianlianCity:edit")
    @Log(title = "联联市级城市", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody LianlianCityBo bo) {
        return toAjax(iLianlianCityService.updateByBo(bo));
    }

    /**
     * 删除联联市级城市
     *
     * @param cityIds 主键串
     */
    @SaCheckPermission("zlyyh:lianlianCity:remove")
    @Log(title = "联联市级城市", businessType = BusinessType.DELETE)
    @DeleteMapping("/{cityIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] cityIds) {
        return toAjax(iLianlianCityService.deleteWithValidByIds(Arrays.asList(cityIds), true));
    }
}
