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
import com.ruoyi.system.api.RemoteAppOrderService;
import com.ruoyi.zlyyhadmin.service.IHistoryOrderService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderVo;
import com.ruoyi.zlyyh.domain.bo.HistoryOrderBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 历史订单控制器
 * 前端访问路由地址为:/zlyyh/historyOrder
 *
 * @author yzg
 * @date 2023-08-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/historyOrder")
public class HistoryOrderController extends BaseController {

    private final IHistoryOrderService iHistoryOrderService;

    @DubboReference(retries = 0)
    private RemoteAppOrderService appOrderService;


    /**
     * 查询历史订单列表
     */
    @SaCheckPermission("zlyyh:historyOrder:list")
    @GetMapping("/list")
    public TableDataInfo<HistoryOrderVo> list(HistoryOrderBo bo, PageQuery pageQuery) {
        return iHistoryOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出历史订单列表
     */
    @SaCheckPermission("zlyyh:historyOrder:export")
    @Log(title = "历史订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HistoryOrderBo bo, HttpServletResponse response) {
        List<HistoryOrderVo> list = iHistoryOrderService.queryList(bo);
        ExcelUtil.exportExcel(list, "历史订单", HistoryOrderVo.class, response);
    }

    /**
     * 获取历史订单详细信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:historyOrder:query")
    @GetMapping("/{number}")
    public R<HistoryOrderVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iHistoryOrderService.queryById(number));
    }

    /**
     * 新增历史订单
     */
    @SaCheckPermission("zlyyh:historyOrder:add")
    @Log(title = "历史订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HistoryOrderBo bo) {
        return toAjax(iHistoryOrderService.insertByBo(bo));
    }

    /**
     * 修改历史订单
     */
    @SaCheckPermission("zlyyh:historyOrder:edit")
    @Log(title = "历史订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HistoryOrderBo bo) {
        return toAjax(iHistoryOrderService.updateByBo(bo));
    }

    /**
     * 美食订单退款
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:order:refund")
    @GetMapping("/cancelFoodOrder/{number}")
    public void cancelFoodOrder(@PathVariable Long number) {
        appOrderService.cancelHistoryFoodOrder(number);
    }


    /**
     * 删除历史订单
     *
     * @param numbers 主键串
     */
    @SaCheckPermission("zlyyh:historyOrder:remove")
    @Log(title = "历史订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{numbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] numbers) {
        return toAjax(iHistoryOrderService.deleteWithValidByIds(Arrays.asList(numbers), true));
    }
}
