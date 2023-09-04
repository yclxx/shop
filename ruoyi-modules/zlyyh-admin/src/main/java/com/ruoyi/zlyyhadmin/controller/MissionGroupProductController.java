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
import com.ruoyi.zlyyh.domain.bo.MissionGroupProductBo;
import com.ruoyi.zlyyh.domain.vo.MissionGroupProductVo;
import com.ruoyi.zlyyhadmin.service.IMissionGroupProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 任务组可兑换商品配置控制器
 * 前端访问路由地址为:/zlyyh/missionGroupProduct
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/missionGroupProduct")
public class MissionGroupProductController extends BaseController {

    private final IMissionGroupProductService iMissionGroupProductService;

    /**
     * 查询任务组可兑换商品配置列表
     */
    @SaCheckPermission("zlyyh:missionGroupProduct:list")
    @GetMapping("/list")
    public TableDataInfo<MissionGroupProductVo> list(MissionGroupProductBo bo, PageQuery pageQuery) {
        return iMissionGroupProductService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出任务组可兑换商品配置列表
     */
    @SaCheckPermission("zlyyh:missionGroupProduct:export")
    @Log(title = "任务组可兑换商品配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MissionGroupProductBo bo, HttpServletResponse response) {
        List<MissionGroupProductVo> list = iMissionGroupProductService.queryList(bo);
        ExcelUtil.exportExcel(list, "任务组可兑换商品配置", MissionGroupProductVo.class, response);
    }

    /**
     * 获取任务组可兑换商品配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:missionGroupProduct:query")
    @GetMapping("/{id}")
    public R<MissionGroupProductVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iMissionGroupProductService.queryById(id));
    }

    /**
     * 新增任务组可兑换商品配置
     */
    @SaCheckPermission("zlyyh:missionGroupProduct:add")
    @Log(title = "任务组可兑换商品配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MissionGroupProductBo bo) {
        return toAjax(iMissionGroupProductService.insertByBo(bo));
    }

    /**
     * 修改任务组可兑换商品配置
     */
    @SaCheckPermission("zlyyh:missionGroupProduct:edit")
    @Log(title = "任务组可兑换商品配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MissionGroupProductBo bo) {
        return toAjax(iMissionGroupProductService.updateByBo(bo));
    }

    /**
     * 删除任务组可兑换商品配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:missionGroupProduct:remove")
    @Log(title = "任务组可兑换商品配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iMissionGroupProductService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
