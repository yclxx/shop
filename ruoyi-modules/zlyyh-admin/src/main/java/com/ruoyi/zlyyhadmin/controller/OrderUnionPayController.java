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
import com.ruoyi.zlyyh.domain.bo.OrderUnionPayBo;
import com.ruoyi.zlyyh.domain.vo.OrderUnionPayVo;
import com.ruoyi.zlyyhadmin.service.IOrderUnionPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 银联分销订单详情控制器
 * 前端访问路由地址为:/zlyyh/orderUnionPay
 *
 * @author yzg
 * @date 2023-08-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderUnionPay")
public class OrderUnionPayController extends BaseController {

    private final IOrderUnionPayService iOrderUnionPayService;

    /**
     * 查询银联分销订单详情列表
     */
    @SaCheckPermission("zlyyh:orderUnionPay:list")
    @GetMapping("/list")
    public TableDataInfo<OrderUnionPayVo> list(OrderUnionPayBo bo, PageQuery pageQuery) {
        return iOrderUnionPayService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出银联分销订单详情列表
     */
    @SaCheckPermission("zlyyh:orderUnionPay:export")
    @Log(title = "银联分销订单详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderUnionPayBo bo, HttpServletResponse response) {
        List<OrderUnionPayVo> list = iOrderUnionPayService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联分销订单详情", OrderUnionPayVo.class, response);
    }

    /**
     * 获取银联分销订单详情详细信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:orderUnionPay:query")
    @GetMapping("/{number}")
    public R<OrderUnionPayVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iOrderUnionPayService.queryById(number));
    }

    /**
     * 新增银联分销订单详情
     */
    @SaCheckPermission("zlyyh:orderUnionPay:add")
    @Log(title = "银联分销订单详情", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderUnionPayBo bo) {
        return toAjax(iOrderUnionPayService.insertByBo(bo));
    }

    /**
     * 修改银联分销订单详情
     */
    @SaCheckPermission("zlyyh:orderUnionPay:edit")
    @Log(title = "银联分销订单详情", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderUnionPayBo bo) {
        return toAjax(iOrderUnionPayService.updateByBo(bo));
    }

    /**
     * 删除银联分销订单详情
     *
     * @param numbers 主键串
     */
    @SaCheckPermission("zlyyh:orderUnionPay:remove")
    @Log(title = "银联分销订单详情", businessType = BusinessType.DELETE)
    @DeleteMapping("/{numbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] numbers) {
        return toAjax(iOrderUnionPayService.deleteWithValidByIds(Arrays.asList(numbers), true));
    }
}
