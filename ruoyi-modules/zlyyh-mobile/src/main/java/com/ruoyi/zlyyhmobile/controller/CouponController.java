package com.ruoyi.zlyyhmobile.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.Coupon;
import com.ruoyi.zlyyh.domain.bo.CouponBo;
import com.ruoyi.zlyyh.domain.vo.CouponVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.ICouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coupon")
public class CouponController extends BaseController {

    private final ICouponService iCouponService;

    /**
     * 查询优惠券列表
     */
    @GetMapping("/list")
    public TableDataInfo<CouponVo> list(CouponBo bo, PageQuery pageQuery) {
        if(ObjectUtil.isEmpty(bo.getUseStatus())){
            throw new ServiceException("系统异常，请联系客服！");
        }
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setUserId(LoginHelper.getUserId());
        return iCouponService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取优惠券详细信息
     *
     * @param couponId 主键
     */
    @GetMapping("/{couponId}")
    public R<CouponVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long couponId) {
        Long userId = LoginHelper.getUserId();
        CouponVo couponVo = iCouponService.queryById(couponId);
        if (null == couponVo || !couponVo.getUserId().equals(userId)) {
            throw new ServiceException("用户信息不匹配,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        return R.ok(iCouponService.queryById(couponId));
    }

    /**
     * 兑换优惠券
     */
    @Log(title = "优惠券", businessType = BusinessType.UPDATE)
    @PostMapping("/conversion")
    public R<Boolean> conversion(@RequestBody Coupon coupon) {
        // 获取用户信息
        coupon.setUserId(LoginHelper.getUserId());
        return R.ok(iCouponService.conversion(coupon));
    }


}
