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
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.OrderBackTransVo;
import com.ruoyi.zlyyhadmin.service.IOrderBackTransService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 退款订单控制器
 * 前端访问路由地址为:/zlyyh/orderBackTrans
 *
 * @author yzg
 * @date 2023-04-03
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderBackTrans")
public class OrderBackTransController extends BaseController {

    private final IOrderBackTransService iOrderBackTransService;

    /**
     * 查询退款订单列表
     */
    @SaCheckPermission("zlyyh:orderBackTrans:list")
    @GetMapping("/list")
    public TableDataInfo<OrderBackTransVo> list(OrderBackTransBo bo, PageQuery pageQuery) {
        return iOrderBackTransService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出退款订单列表
     */
    @SaCheckPermission("zlyyh:orderBackTrans:export")
    @Log(title = "退款订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderBackTransBo bo, HttpServletResponse response) {
        List<OrderBackTransVo> list = iOrderBackTransService.queryList(bo);
        ExcelUtil.exportExcel(list, "退款订单", OrderBackTransVo.class, response);
    }

    /**
     * 获取退款订单详细信息
     *
     * @param thNumber 主键
     */
    @SaCheckPermission("zlyyh:orderBackTrans:query")
    @GetMapping("/{thNumber}")
    public R<OrderBackTransVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable String thNumber) {
        return R.ok(iOrderBackTransService.queryById(thNumber));
    }

    /**
     * 新增退款订单
     */
    @SaCheckPermission("zlyyh:orderBackTrans:add")
    @Log(title = "退款订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderBackTransBo bo) {
        return toAjax(iOrderBackTransService.insertByBo(bo, false));
    }

    /**
     * 新增退款订单
     */
    @SaCheckPermission("zlyyh:orderBackTrans:add")
    @PostMapping("/insertDirectByBo")
    public R<Void> insertDirectByBo(@Validated(AddGroup.class) @RequestBody OrderBackTransBo bo) {
        return toAjax(iOrderBackTransService.insertDirectByBo(bo));
    }

    /**
     * 新增退款历史订单
     */
    @SaCheckPermission("zlyyh:orderBackTrans:add")
    @PostMapping("/insertByBoHistory")
    public R<Void> insertByBoHistory(@Validated(AddGroup.class) @RequestBody OrderBackTransBo bo) {
        return toAjax(iOrderBackTransService.insertByBoHistory(bo, false));
    }

    /**
     * 新增退款历史订单（直接退款）
     */
    @SaCheckPermission("zlyyh:orderBackTrans:add")
    @PostMapping("/insertDirectByBoHistory")
    public R<Void> insertDirectByBoHistory(@Validated(AddGroup.class) @RequestBody OrderBackTransBo bo) {
        return toAjax(iOrderBackTransService.insertDirectByBoHistory(bo));
    }

    /**
     * 修改退款订单
     */
    @SaCheckPermission("zlyyh:orderBackTrans:edit")
    @Log(title = "退款订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderBackTransBo bo) {
        return toAjax(iOrderBackTransService.updateByBo(bo));
    }

    /**
     * 删除退款订单
     *
     * @param thNumbers 主键串
     */
    @SaCheckPermission("zlyyh:orderBackTrans:remove")
    @Log(title = "退款订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{thNumbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] thNumbers) {
        return toAjax(iOrderBackTransService.deleteWithValidByIds(Arrays.asList(thNumbers), true));
    }
}
