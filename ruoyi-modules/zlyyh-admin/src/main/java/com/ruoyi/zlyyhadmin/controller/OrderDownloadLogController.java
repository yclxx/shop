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
import com.ruoyi.zlyyh.domain.bo.OrderDownloadLogBo;
import com.ruoyi.zlyyh.domain.vo.OrderDownloadLogVo;
import com.ruoyi.zlyyhadmin.service.IOrderDownloadLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 订单下载记录控制器
 * 前端访问路由地址为:/zlyyh/orderDownloadLog
 *
 * @author yzg
 * @date 2023-04-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderDownloadLog")
public class OrderDownloadLogController extends BaseController {

    private final IOrderDownloadLogService iOrderDownloadLogService;

    /**
     * 查询订单下载记录列表
     */
    @SaCheckPermission("zlyyh:orderDownloadLog:list")
    @GetMapping("/list")
    public TableDataInfo<OrderDownloadLogVo> list(OrderDownloadLogBo bo, PageQuery pageQuery) {
        return iOrderDownloadLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出订单下载记录列表
     */
    @SaCheckPermission("zlyyh:orderDownloadLog:export")
    @Log(title = "订单下载记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderDownloadLogBo bo, HttpServletResponse response) {
        List<OrderDownloadLogVo> list = iOrderDownloadLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "订单下载记录", OrderDownloadLogVo.class, response);
    }

    /**
     * 获取订单下载记录详细信息
     *
     * @param tOrderDownloadId 主键
     */
    @SaCheckPermission("zlyyh:orderDownloadLog:query")
    @GetMapping("/{tOrderDownloadId}")
    public R<OrderDownloadLogVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long tOrderDownloadId) {
        return R.ok(iOrderDownloadLogService.queryById(tOrderDownloadId));
    }

    /**
     * 新增订单下载记录
     */
    @SaCheckPermission("zlyyh:orderDownloadLog:add")
    @Log(title = "订单下载记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderDownloadLogBo bo) {
        return toAjax(iOrderDownloadLogService.insertByBo(bo));
    }

    /**
     * 修改订单下载记录
     */
    @SaCheckPermission("zlyyh:orderDownloadLog:edit")
    @Log(title = "订单下载记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderDownloadLogBo bo) {
        return toAjax(iOrderDownloadLogService.updateByBo(bo));
    }

    /**
     * 删除订单下载记录
     *
     * @param tOrderDownloadIds 主键串
     */
    @SaCheckPermission("zlyyh:orderDownloadLog:remove")
    @Log(title = "订单下载记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tOrderDownloadIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] tOrderDownloadIds) {
        return toAjax(iOrderDownloadLogService.deleteWithValidByIds(Arrays.asList(tOrderDownloadIds), true));
    }
}
