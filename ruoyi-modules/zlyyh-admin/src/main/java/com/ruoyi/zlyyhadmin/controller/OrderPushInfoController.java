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
import com.ruoyi.zlyyhadmin.service.IOrderPushInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.OrderPushInfoVo;
import com.ruoyi.zlyyh.domain.bo.OrderPushInfoBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单取码记录控制器
 * 前端访问路由地址为:/zlyyh/orderPushInfo
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderPushInfo")
public class OrderPushInfoController extends BaseController {

    private final IOrderPushInfoService iOrderPushInfoService;

    /**
     * 查询订单取码记录列表
     */
    @SaCheckPermission("zlyyh:orderPushInfo:list")
    @GetMapping("/list")
    public TableDataInfo<OrderPushInfoVo> list(OrderPushInfoBo bo, PageQuery pageQuery) {
        return iOrderPushInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出订单取码记录列表
     */
    @SaCheckPermission("zlyyh:orderPushInfo:export")
    @Log(title = "订单取码记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderPushInfoBo bo, HttpServletResponse response) {
        List<OrderPushInfoVo> list = iOrderPushInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "订单取码记录", OrderPushInfoVo.class, response);
    }

    /**
     * 获取订单取码记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:orderPushInfo:query")
    @GetMapping("/{id}")
    public R<OrderPushInfoVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iOrderPushInfoService.queryById(id));
    }

    /**
     * 新增订单取码记录
     */
    @SaCheckPermission("zlyyh:orderPushInfo:add")
    @Log(title = "订单取码记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderPushInfoBo bo) {
        return toAjax(iOrderPushInfoService.insertByBo(bo));
    }

    /**
     * 修改订单取码记录
     */
    @SaCheckPermission("zlyyh:orderPushInfo:edit")
    @Log(title = "订单取码记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderPushInfoBo bo) {
        return toAjax(iOrderPushInfoService.updateByBo(bo));
    }

    /**
     * 删除订单取码记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:orderPushInfo:remove")
    @Log(title = "订单取码记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iOrderPushInfoService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
