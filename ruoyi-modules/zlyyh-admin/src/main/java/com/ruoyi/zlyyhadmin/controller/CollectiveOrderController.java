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
import com.ruoyi.zlyyhadmin.service.ICollectiveOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.CollectiveOrderVo;
import com.ruoyi.zlyyh.domain.bo.CollectiveOrderBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 大订单控制器
 * 前端访问路由地址为:/zlyyh/collectiveOrder
 *
 * @author yzg
 * @date 2023-10-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/collectiveOrder")
public class CollectiveOrderController extends BaseController {

    private final ICollectiveOrderService iCollectiveOrderService;

    /**
     * 查询大订单列表
     */
    @SaCheckPermission("zlyyh:collectiveOrder:list")
    @GetMapping("/list")
    public TableDataInfo<CollectiveOrderVo> list(CollectiveOrderBo bo, PageQuery pageQuery) {
        return iCollectiveOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出大订单列表
     */
    @SaCheckPermission("zlyyh:collectiveOrder:export")
    @Log(title = "大订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CollectiveOrderBo bo, HttpServletResponse response) {
        List<CollectiveOrderVo> list = iCollectiveOrderService.queryList(bo);
        ExcelUtil.exportExcel(list, "大订单", CollectiveOrderVo.class, response);
    }

    /**
     * 获取大订单详细信息
     *
     * @param collectiveNumber 主键
     */
    @SaCheckPermission("zlyyh:collectiveOrder:query")
    @GetMapping("/{collectiveNumber}")
    public R<CollectiveOrderVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long collectiveNumber) {
        return R.ok(iCollectiveOrderService.queryById(collectiveNumber));
    }

    /**
     * 新增大订单
     */
    @SaCheckPermission("zlyyh:collectiveOrder:add")
    @Log(title = "大订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CollectiveOrderBo bo) {
        return toAjax(iCollectiveOrderService.insertByBo(bo));
    }

    /**
     * 修改大订单
     */
    @SaCheckPermission("zlyyh:collectiveOrder:edit")
    @Log(title = "大订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CollectiveOrderBo bo) {
        return toAjax(iCollectiveOrderService.updateByBo(bo));
    }

    /**
     * 删除大订单
     *
     * @param collectiveNumbers 主键串
     */
    @SaCheckPermission("zlyyh:collectiveOrder:remove")
    @Log(title = "大订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{collectiveNumbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] collectiveNumbers) {
        return toAjax(iCollectiveOrderService.deleteWithValidByIds(Arrays.asList(collectiveNumbers), true));
    }
}
