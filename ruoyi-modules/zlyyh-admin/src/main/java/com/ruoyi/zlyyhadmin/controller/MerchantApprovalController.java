package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.vo.MerchantApprovalVo;
import com.ruoyi.zlyyhadmin.service.IMerchantApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 商户申请审批控制器
 * 前端访问路由地址为:/zlyyh/merchantApproval
 *
 * @author yzg
 * @date 2023-10-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/merchantApproval")
public class MerchantApprovalController extends BaseController {

    private final IMerchantApprovalService iMerchantApprovalService;

    /**
     * 查询商户申请审批列表
     */
    @SaCheckPermission("zlyyh:merchantApproval:list")
    @GetMapping("/list")
    public TableDataInfo<MerchantApprovalVo> list(MerchantApprovalBo bo, PageQuery pageQuery) {
        return iMerchantApprovalService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取商户申请审批详细信息
     *
     * @param approvalId 主键
     */
    @SaCheckPermission("zlyyh:merchantApproval:query")
    @GetMapping("/{approvalId}")
    public R<MerchantApprovalVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long approvalId) {
        return R.ok(iMerchantApprovalService.queryById(approvalId));
    }

    /**
     * 修改商户申请审批
     */
    @SaCheckPermission("zlyyh:merchantApproval:edit")
    @Log(title = "商户申请审批", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@RequestBody MerchantApprovalBo bo) {
        return toAjax(iMerchantApprovalService.updateByBo(bo));
    }
}
