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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.ShopTourRewardVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourRewardBo;
import com.ruoyi.zlyyhadmin.service.IShopTourRewardService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 巡检奖励控制器
 * 前端访问路由地址为:/zlyyh/shopTourReward
 *
 * @author yzg
 * @date 2024-01-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shopTourReward")
public class ShopTourRewardController extends BaseController {

    private final IShopTourRewardService iShopTourRewardService;

    /**
     * 查询巡检奖励列表
     */
    @SaCheckPermission("zlyyh:shopTourReward:list")
    @GetMapping("/list")
    public TableDataInfo<ShopTourRewardVo> list(ShopTourRewardBo bo, PageQuery pageQuery) {
        return iShopTourRewardService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出巡检奖励列表
     */
    @SaCheckPermission("zlyyh:shopTourReward:export")
    @Log(title = "巡检奖励", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopTourRewardBo bo, HttpServletResponse response) {
        List<ShopTourRewardVo> list = iShopTourRewardService.queryList(bo);
        ExcelUtil.exportExcel(list, "巡检奖励", ShopTourRewardVo.class, response);
    }

    /**
     * 获取巡检奖励详细信息
     *
     * @param tourRewardId 主键
     */
    @SaCheckPermission("zlyyh:shopTourReward:query")
    @GetMapping("/{tourRewardId}")
    public R<ShopTourRewardVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long tourRewardId) {
        return R.ok(iShopTourRewardService.queryById(tourRewardId));
    }

    /**
     * 新增巡检奖励
     */
    @SaCheckPermission("zlyyh:shopTourReward:add")
    @Log(title = "巡检奖励", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopTourRewardBo bo) {
        return toAjax(iShopTourRewardService.insertByBo(bo));
    }

    /**
     * 修改巡检奖励
     */
    @SaCheckPermission("zlyyh:shopTourReward:edit")
    @Log(title = "巡检奖励", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopTourRewardBo bo) {
        return toAjax(iShopTourRewardService.updateByBo(bo));
    }

    /**
     * 删除巡检奖励
     *
     * @param tourRewardIds 主键串
     */
    @SaCheckPermission("zlyyh:shopTourReward:remove")
    @Log(title = "巡检奖励", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tourRewardIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] tourRewardIds) {
        return toAjax(iShopTourRewardService.deleteWithValidByIds(Arrays.asList(tourRewardIds), true));
    }
}
