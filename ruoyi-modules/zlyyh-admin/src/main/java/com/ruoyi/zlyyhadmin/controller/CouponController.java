package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.CouponBo;
import com.ruoyi.zlyyh.domain.vo.CouponVo;
import com.ruoyi.zlyyhadmin.service.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 优惠券控制器
 * 前端访问路由地址为:/zlyyh/coupon
 *
 * @author yzg
 * @date 2023-10-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coupon")
public class CouponController extends BaseController {

    private final ICouponService iCouponService;

    /**
     * 查询优惠券列表
     */
    @SaCheckPermission("zlyyh:coupon:list")
    @GetMapping("/list")
    public TableDataInfo<CouponVo> list(CouponBo bo, PageQuery pageQuery) {
        return iCouponService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出优惠券列表
     */
    @SaCheckPermission("zlyyh:coupon:export")
    @Log(title = "优惠券", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CouponBo bo, HttpServletResponse response) {
        List<CouponVo> list = iCouponService.queryList(bo);
        ExcelUtil.exportExcel(list, "优惠券", CouponVo.class, response);
    }

    /**
     * 获取优惠券详细信息
     *
     * @param couponId 主键
     */
    @SaCheckPermission("zlyyh:coupon:query")
    @GetMapping("/{couponId}")
    public R<CouponVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long couponId) {
        return R.ok(iCouponService.queryById(couponId));
    }

    /**
     * 指定批次作废优惠券
     */
    @SaCheckPermission("zlyyh:coupon:edit")
    @Log(title = "优惠券", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@RequestBody CouponBo bo) {
        return toAjax(iCouponService.updateByBo(bo));
    }

    /**
     * 批量作废优惠券
     *
     * @param couponIds 主键串
     */
    @SaCheckPermission("zlyyh:coupon:remove")
    @Log(title = "优惠券", businessType = BusinessType.DELETE)
    @DeleteMapping("/{couponIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] couponIds) {
        return toAjax(iCouponService.deleteWithValidByIds(Arrays.asList(couponIds), true));
    }
}
