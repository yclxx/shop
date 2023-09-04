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
import com.ruoyi.zlyyh.domain.bo.BannerBo;
import com.ruoyi.zlyyh.domain.vo.BannerVo;
import com.ruoyi.zlyyhadmin.service.IBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 广告管理控制器
 * 前端访问路由地址为:/zlyyh-admin/banner
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/banner")
public class BannerController extends BaseController {

    private final IBannerService iBannerService;

    /**
     * 查询广告管理列表
     */
    @SaCheckPermission("zlyyh:banner:list")
    @GetMapping("/list")
    public TableDataInfo<BannerVo> list(BannerBo bo, PageQuery pageQuery) {
        return iBannerService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出广告管理列表
     */
    @SaCheckPermission("zlyyh:banner:export")
    @Log(title = "广告管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BannerBo bo, HttpServletResponse response) {
        List<BannerVo> list = iBannerService.queryList(bo);
        ExcelUtil.exportExcel(list, "广告管理", BannerVo.class, response);
    }

    /**
     * 获取广告管理详细信息
     *
     * @param bannerId 主键
     */
    @SaCheckPermission("zlyyh:banner:query")
    @GetMapping("/{bannerId}")
    public R<BannerVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long bannerId) {
        return R.ok(iBannerService.queryById(bannerId));
    }

    /**
     * 新增广告管理
     */
    @SaCheckPermission("zlyyh:banner:add")
    @Log(title = "广告管理", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BannerBo bo) {
        return toAjax(iBannerService.insertByBo(bo));
    }

    /**
     * 修改广告管理
     */
    @SaCheckPermission("zlyyh:banner:edit")
    @Log(title = "广告管理", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BannerBo bo) {
        return toAjax(iBannerService.updateByBo(bo));
    }

    /**
     * 删除广告管理
     *
     * @param bannerIds 主键串
     */
    @SaCheckPermission("zlyyh:banner:remove")
    @Log(title = "广告管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bannerIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] bannerIds) {
        return toAjax(iBannerService.deleteWithValidByIds(Arrays.asList(bannerIds), true));
    }
}
