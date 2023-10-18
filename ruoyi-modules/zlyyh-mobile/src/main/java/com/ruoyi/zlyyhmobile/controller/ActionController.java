package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.Coupon;
import com.ruoyi.zlyyh.domain.bo.ActionBo;
import com.ruoyi.zlyyh.domain.vo.ActionVo;
import com.ruoyi.zlyyh.domain.vo.CouponVo;
import com.ruoyi.zlyyhmobile.service.IActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



/**
 * 优惠券批次控制器
 * 前端访问路由地址为:/zlyyh/action
 *
 * @author yzg
 * @date 2023-10-12
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/action")
public class ActionController extends BaseController {

    private final IActionService actionService;

    /**
     * 查询优惠券批次列表
     */
    @GetMapping("/list")
    public TableDataInfo<ActionVo> list(ActionBo bo, PageQuery pageQuery) {
        bo.setStatus("0");
        return actionService.queryPageList(bo, pageQuery);
    }




    /**
     * 用户领取优惠券
     */
    @PostMapping("/insertUserCoupon")
    public R<Coupon> insertUserCoupon(@RequestBody ActionBo bo) {
        Long userId = LoginHelper.getUserId();
        return R.ok(actionService.insertUserCoupon(bo, userId));
    }



}
