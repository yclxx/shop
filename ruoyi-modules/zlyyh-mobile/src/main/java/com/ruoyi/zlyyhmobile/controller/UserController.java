package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.vo.MemberVipBalanceVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyhmobile.domain.bo.UserRecordLog;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登录控制器
 * 前端访问路由地址为:/zlyyh-mobile/user
 *
 * @author ruoyi
 * @date 2023-03-18
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    private final IUserService userService;
    private final IOrderService orderService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    public R<UserVo> getUserInfo() {
        UserVo userVo = userService.queryById(LoginHelper.getUserId());
        // 校验是否权益会员，权益会员是否到期
        if (null != userVo && "1".equals(userVo.getVipUser()) && null != userVo.getVipExpiryDate()) {
            if (DateUtils.compare(userVo.getVipExpiryDate()) < 1) {
                // 已过期
                userVo.setVipUser("2");
                userService.vipExpiry(userVo.getUserId());
            }
        }
        if (null != userVo) {
            // 超时订单取消
            orderService.cancelOrder(userVo.getUserId());
        }
        return R.ok(userVo);
    }

    /**
     * 获取用户云闪付62vip信息
     *
     * @return 用户信息
     */
    @GetMapping("/getUser62VipInfo")
    public R<MemberVipBalanceVo> getUser62VipInfo(String isCache) {
        boolean b = "false".equals(isCache);
        MemberVipBalanceVo user62VipInfo = userService.getUser62VipInfo(true, LoginHelper.getUserId());
        if (b) {
            if (null != user62VipInfo && ("01".equals(user62VipInfo.getStatus()) || "03".equals(user62VipInfo.getStatus()))) {
                return R.ok(user62VipInfo);
            } else {
                return R.ok(userService.getUser62VipInfo(false, LoginHelper.getUserId()));
            }
        }
        return R.ok(user62VipInfo);
    }

    /**
     * 用户关注修改关注状态
     *
     * @return 用户信息
     */
    @GetMapping("/userFollow/{code}")
    public R<Void> userFollow(@PathVariable("code") String code) {
        userService.userFollow(code);
        return R.ok();
    }

    /**
     * 用户点击记录,必须有参数
     */
    @PostMapping("/ignore/userLog")
    public R<Void> userLog(@RequestBody UserRecordLog recordLog) {
        userService.userLog(recordLog);
        return R.ok();
    }

}
