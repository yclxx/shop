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
import com.ruoyi.zlyyhadmin.service.IRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.RefundVo;
import com.ruoyi.zlyyh.domain.bo.RefundBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 退款订单登记控制器
 * 前端访问路由地址为:/zlyyh/refund
 *
 * @author yzg
 * @date 2023-08-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/refund")
public class RefundController extends BaseController {

    private final IRefundService iRefundService;

    /**
     * 查询退款订单登记列表
     */
    @SaCheckPermission("zlyyh:refund:list")
    @GetMapping("/list")
    public TableDataInfo<RefundVo> list(RefundBo bo, PageQuery pageQuery) {
        return iRefundService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出退款订单登记列表
     */
    @SaCheckPermission("zlyyh:refund:export")
    @Log(title = "退款订单登记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RefundBo bo, HttpServletResponse response) {
        List<RefundVo> list = iRefundService.queryList(bo);
        ExcelUtil.exportExcel(list, "退款订单登记", RefundVo.class, response);
    }

    /**
     * 获取退款订单登记详细信息
     *
     * @param refundId 主键
     */
    @SaCheckPermission("zlyyh:refund:query")
    @GetMapping("/{refundId}")
    public R<RefundVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long refundId) {
        return R.ok(iRefundService.queryById(refundId));
    }

    /**
     * 新增退款订单登记
     */
    @SaCheckPermission("zlyyh:refund:add")
    @Log(title = "退款订单登记", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody RefundBo bo) {
        return toAjax(iRefundService.insertByBo(bo));
    }

    /**
     * 修改退款订单登记
     */
    @SaCheckPermission("zlyyh:refund:edit")
    @Log(title = "退款订单登记", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RefundBo bo) {
        return toAjax(iRefundService.updateByBo(bo));
    }

    /**
     * 删除退款订单登记
     *
     * @param refundIds 主键串
     */
    @SaCheckPermission("zlyyh:refund:remove")
    @Log(title = "退款订单登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{refundIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] refundIds) {
        return toAjax(iRefundService.deleteWithValidByIds(Arrays.asList(refundIds), true));
    }
    /**
     * 退款订单登记审核通过退款
     */
    @SaCheckPermission("zlyyh:refund:edit")
    @PostMapping("/agreeSubmit")
    public R<Void> agreeSubmit(Long refundId) {
        iRefundService.agreeSubmit(refundId);
        return R.ok();
    }

    /**
     * 退款订单登记审核拒绝
     */
    @SaCheckPermission("zlyyh:refund:edit")
    @PostMapping("/refuseSubmit")
    public R<Void> refuseSubmit(Long refundId) {
        iRefundService.refuseSubmit(refundId);
        return R.ok();
    }

}
