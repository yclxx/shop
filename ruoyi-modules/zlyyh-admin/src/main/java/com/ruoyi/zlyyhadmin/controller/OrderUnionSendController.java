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
import com.ruoyi.zlyyh.domain.bo.OrderUnionSendBo;
import com.ruoyi.zlyyh.domain.vo.OrderUnionSendVo;
import com.ruoyi.zlyyhadmin.service.IOrderUnionSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 银联分销订单卡券控制器
 * 前端访问路由地址为:/zlyyh/orderUnionSend
 *
 * @author yzg
 * @date 2023-08-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderUnionSend")
public class OrderUnionSendController extends BaseController {

    private final IOrderUnionSendService iOrderUnionSendService;

    /**
     * 查询银联分销订单卡券列表
     */
    @SaCheckPermission("zlyyh:orderUnionSend:list")
    @GetMapping("/list")
    public TableDataInfo<OrderUnionSendVo> list(OrderUnionSendBo bo, PageQuery pageQuery) {
        return iOrderUnionSendService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出银联分销订单卡券列表
     */
    @SaCheckPermission("zlyyh:orderUnionSend:export")
    @Log(title = "银联分销订单卡券", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderUnionSendBo bo, HttpServletResponse response) {
        List<OrderUnionSendVo> list = iOrderUnionSendService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联分销订单卡券", OrderUnionSendVo.class, response);
    }

    /**
     * 获取银联分销订单卡券详细信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:orderUnionSend:query")
    @GetMapping("/{number}")
    public R<OrderUnionSendVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iOrderUnionSendService.queryById(number));
    }

    /**
     * 新增银联分销订单卡券
     */
    @SaCheckPermission("zlyyh:orderUnionSend:add")
    @Log(title = "银联分销订单卡券", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderUnionSendBo bo) {
        return toAjax(iOrderUnionSendService.insertByBo(bo));
    }

    /**
     * 修改银联分销订单卡券
     */
    @SaCheckPermission("zlyyh:orderUnionSend:edit")
    @Log(title = "银联分销订单卡券", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderUnionSendBo bo) {
        return toAjax(iOrderUnionSendService.updateByBo(bo));
    }

    /**
     * 删除银联分销订单卡券
     *
     * @param numbers 主键串
     */
    @SaCheckPermission("zlyyh:orderUnionSend:remove")
    @Log(title = "银联分销订单卡券", businessType = BusinessType.DELETE)
    @DeleteMapping("/{numbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] numbers) {
        return toAjax(iOrderUnionSendService.deleteWithValidByIds(Arrays.asList(numbers), true));
    }
}
