package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ColumnUtil;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.SelectListEntity;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.zlyyh.domain.bo.PlatformBo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ShopTourActivityVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourActivityBo;
import com.ruoyi.zlyyhadmin.service.IShopTourActivityService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 巡检活动控制器
 * 前端访问路由地址为:/zlyyh/shopTourActivity
 *
 * @author yzg
 * @date 2024-03-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopTourActivity")
public class ShopTourActivityController extends BaseController {

    private final IShopTourActivityService iShopTourActivityService;

    /**
     * 查询巡检活动列表
     */
    @SaCheckPermission("zlyyh:shopTourActivity:list")
    @GetMapping("/list")
    public TableDataInfo<ShopTourActivityVo> list(ShopTourActivityBo bo, PageQuery pageQuery) {
        return iShopTourActivityService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询巡检活动下拉列表
     */
    @GetMapping("/selectListTourActivity")
    public R<List<SelectListEntity>> selectListTourActivity(ShopTourActivityBo bo){
        List<ShopTourActivityVo> tourActivityVoList = iShopTourActivityService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(tourActivityVoList, ColumnUtil.getFieldName(ShopTourActivityVo::getTourActivityId),ColumnUtil.getFieldName(ShopTourActivityVo::getTourActivityName),null));
    }

    /**
     * 导出巡检活动列表
     */
    @SaCheckPermission("zlyyh:shopTourActivity:export")
    @Log(title = "巡检活动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopTourActivityBo bo, HttpServletResponse response) {
        List<ShopTourActivityVo> list = iShopTourActivityService.queryList(bo);
        ExcelUtil.exportExcel(list, "巡检活动", ShopTourActivityVo.class, response);
    }

    /**
     * 获取巡检活动详细信息
     *
     * @param tourActivityId 主键
     */
    @SaCheckPermission("zlyyh:shopTourActivity:query")
    @GetMapping("/{tourActivityId}")
    public R<ShopTourActivityVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long tourActivityId) {
        return R.ok(iShopTourActivityService.queryById(tourActivityId));
    }

    /**
     * 新增巡检活动
     */
    @SaCheckPermission("zlyyh:shopTourActivity:add")
    @Log(title = "巡检活动", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopTourActivityBo bo) {
        return toAjax(iShopTourActivityService.insertByBo(bo));
    }

    /**
     * 修改巡检活动
     */
    @SaCheckPermission("zlyyh:shopTourActivity:edit")
    @Log(title = "巡检活动", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopTourActivityBo bo) {
        return toAjax(iShopTourActivityService.updateByBo(bo));
    }

    /**
     * 删除巡检活动
     *
     * @param tourActivityIds 主键串
     */
    @SaCheckPermission("zlyyh:shopTourActivity:remove")
    @Log(title = "巡检活动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tourActivityIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] tourActivityIds) {
        return toAjax(iShopTourActivityService.deleteWithValidByIds(Arrays.asList(tourActivityIds), true));
    }
}
