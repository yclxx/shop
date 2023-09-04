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
import com.ruoyi.zlyyhadmin.service.IOrderInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.OrderInfoVo;
import com.ruoyi.zlyyh.domain.bo.OrderInfoBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单扩展信息控制器
 * 前端访问路由地址为:/zlyyh/orderInfo
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController extends BaseController {

    private final IOrderInfoService iOrderInfoService;

    /**
     * 查询订单扩展信息列表
     */
    @SaCheckPermission("zlyyh:orderInfo:list")
    @GetMapping("/list")
    public TableDataInfo<OrderInfoVo> list(OrderInfoBo bo, PageQuery pageQuery) {
        return iOrderInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出订单扩展信息列表
     */
    @SaCheckPermission("zlyyh:orderInfo:export")
    @Log(title = "订单扩展信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderInfoBo bo, HttpServletResponse response) {
        List<OrderInfoVo> list = iOrderInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "订单扩展信息", OrderInfoVo.class, response);
    }

    /**
     * 获取订单扩展信息详细信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:orderInfo:query")
    @GetMapping("/{number}")
    public R<OrderInfoVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iOrderInfoService.queryById(number));
    }

    /**
     * 新增订单扩展信息
     */
    @SaCheckPermission("zlyyh:orderInfo:add")
    @Log(title = "订单扩展信息", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderInfoBo bo) {
        return toAjax(iOrderInfoService.insertByBo(bo));
    }

    /**
     * 修改订单扩展信息
     */
    @SaCheckPermission("zlyyh:orderInfo:edit")
    @Log(title = "订单扩展信息", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderInfoBo bo) {
        return toAjax(iOrderInfoService.updateByBo(bo));
    }

    /**
     * 删除订单扩展信息
     *
     * @param numbers 主键串
     */
    @SaCheckPermission("zlyyh:orderInfo:remove")
    @Log(title = "订单扩展信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{numbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] numbers) {
        return toAjax(iOrderInfoService.deleteWithValidByIds(Arrays.asList(numbers), true));
    }
}
