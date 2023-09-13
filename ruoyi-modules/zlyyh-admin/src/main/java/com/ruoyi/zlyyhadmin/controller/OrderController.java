package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.DesensitizedUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.RemoteAppOrderService;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.bo.OrderDownloadLogBo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyhadmin.service.AsyncService;
import com.ruoyi.zlyyhadmin.service.IOrderDownloadLogService;
import com.ruoyi.zlyyhadmin.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 订单控制器
 * 前端访问路由地址为:/zlyyh/order
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    private final IOrderService iOrderService;
    private final AsyncService asyncService;
    private final IOrderDownloadLogService orderDownloadLogService;

    @DubboReference(retries = 0)
    private RemoteAppOrderService appOrderService;

    /**
     * 查询订单列表
     */
    @SaCheckPermission("zlyyh:order:list")
    @GetMapping("/list")
    public TableDataInfo<OrderVo> list(OrderBo bo, PageQuery pageQuery) {
        return iOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出订单列表
     */
    @SaCheckPermission("zlyyh:order:export")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/exportOrder")
    public R<Long> exportOrder(@RequestBody OrderBo bo) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(1);
        TableDataInfo<OrderVo> orderVoTableDataInfo = iOrderService.queryPageList(bo, pageQuery);
        if (orderVoTableDataInfo.getTotal() <= 5000) {
            return R.ok(200L);
        } else {
            OrderDownloadLogBo logBo = new OrderDownloadLogBo();
            orderDownloadLogService.insertByBo(logBo);
            asyncService.orderImportExcel(bo, logBo);
            return R.ok(501L);
        }
    }

    /**
     * 导出订单列表
     */
    @SaCheckPermission("zlyyh:order:export")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderBo bo, HttpServletResponse response) {
        List<OrderVo> list = iOrderService.queryList(bo);
        for (OrderVo orderVo : list) {
            if (StringUtils.isNotBlank(orderVo.getAccount())) {
                orderVo.setAccount(DesensitizedUtil.mobilePhone(orderVo.getAccount()));
            }
        }
        ExcelUtil.exportExcel(list, "订单", OrderVo.class, response);
    }

    /**
     * 获取订单详细信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:order:query")
    @GetMapping("/{number}")
    public R<OrderVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iOrderService.queryById(number));
    }

    /**
     * 修改订单
     */
    @SaCheckPermission("zlyyh:order:edit")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderBo bo) {
        return toAjax(iOrderService.updateByBo(bo));
    }

    /**
     * 删除订单
     *
     * @param numbers 主键串
     */
    @SaCheckPermission("zlyyh:order:remove")
    @Log(title = "订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{numbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] numbers) {
        return toAjax(iOrderService.deleteWithValidByIds(Arrays.asList(numbers), true));
    }

    /**
     * 订单补发
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:order:reissue")
    @GetMapping("/orderReissue/{number}")
    public void orderReissue(@PathVariable Long number) {
        appOrderService.sendCoupon(number);
    }

    /**
     * 批量补发
     */
    @SaCheckPermission("zlyyh:order:reissue")
    @GetMapping("/batchReissue")
    public R<Void> batchReissue() {
        appOrderService.reloadOrder();
        return R.ok();
    }

    /**
     * 美食订单退款
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:order:refund")
    @GetMapping("/cancelFoodOrder/{number}")
    public void cancelFoodOrder(@PathVariable Long number) {
        appOrderService.cancelFoodOrder(number);
    }

    /**
     * 银联分销订单 退券
     */
    @SaCheckPermission("zlyyh:order:couponRefund")
    @GetMapping("/couponRefundOrder/{number}")
    public R<Void> couponRefundOrder(@PathVariable Long number) {
        return appOrderService.couponRefundOrder(number);
    }
}
