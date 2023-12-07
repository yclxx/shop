package com.ruoyi.auth.controller;

import Union.DecryptAndCheck;
import com.ruoyi.auth.form.AppLoginBody;
import com.ruoyi.auth.form.MsLoginBody;
import com.ruoyi.auth.form.WxMobileLoginBody;
import com.ruoyi.auth.service.AppLoginService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.log.enums.OperatorType;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * token 控制
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
public class AppTokenController {

    private final AppLoginService appLoginService;

    /**
     * 微信小程序 静默登录
     *
     * @param ysfLoginBody 登录信息
     * @return 结果
     */
    @Log(title = "用户登录", businessType = BusinessType.GRANT, operatorType = OperatorType.MOBILE, isSaveSuccessLog = false)
    @PostMapping("/wxLogin")
    public R<String> wxLogin(@Validated @RequestBody AppLoginBody ysfLoginBody, HttpServletRequest request) {
        return R.ok("操作成功", appLoginService.wxLogin(ysfLoginBody, request));
    }

    /**
     * 微信小程序 手机号登录
     *
     * @param ysfLoginBody 小程序code
     * @return 结果
     */
    @Log(title = "用户登录", businessType = BusinessType.GRANT, operatorType = OperatorType.MOBILE, isSaveSuccessLog = false)
    @PostMapping("/wxLoginByMobile")
    public R<String> wxLoginByMobile(@Validated @RequestBody WxMobileLoginBody ysfLoginBody, HttpServletRequest request) {
        return R.ok("操作成功", appLoginService.wxLoginByMobile(ysfLoginBody, request));
    }

    /**
     * 云闪付小程序 测试登录
     *
     * @param mobile 登录信息
     * @return 结果
     */
    @Log(title = "用户登录", businessType = BusinessType.GRANT, operatorType = OperatorType.MOBILE, isSaveSuccessLog = false)
    @PostMapping("/ysfTestLogin/{mobile}")
    public R<String> ysfTestLogin(@PathVariable("mobile") String mobile, HttpServletRequest request) {
        return R.ok("操作成功", appLoginService.ysfTestLogin(mobile, request));
    }

    /**
     * 云闪付小程序 静默登录
     *
     * @param ysfLoginBody 登录信息
     * @return 结果
     */
    @Log(title = "用户登录", businessType = BusinessType.GRANT, operatorType = OperatorType.MOBILE, isSaveSuccessLog = false)
    @PostMapping("/ysfLogin")
    public R<String> ysfLogin(@Validated @RequestBody AppLoginBody ysfLoginBody, HttpServletRequest request) {
        return R.ok("操作成功", appLoginService.ysfLogin(ysfLoginBody, request));
    }

    /**
     * 云闪付小程序 手机号登录
     *
     * @param ysfLoginBody 小程序code
     * @return 结果
     */
    @Log(title = "用户登录", businessType = BusinessType.GRANT, operatorType = OperatorType.MOBILE, isSaveSuccessLog = false)
    @PostMapping("/ysfLoginByMobile")
    public R<String> ysfLoginByMobile(@Validated @RequestBody AppLoginBody ysfLoginBody, HttpServletRequest request) {
        return R.ok("操作成功", appLoginService.ysfLoginByMobile(ysfLoginBody, request));
    }

    /**
     * 登出方法
     */
    @Log(title = "用户登出", businessType = BusinessType.OTHER, operatorType = OperatorType.MOBILE)
    @PostMapping("/ysfLogout")
    public R<Void> logout(HttpServletRequest request) {
        appLoginService.logout(request);
        return R.ok();
    }

    /**
     * 民生银行H5 静默登录
     *
     * @param msLoginBody 民生app返回后缀参数
     * @return 结果
     */
    @Log(title = "用户登录", businessType = BusinessType.GRANT, operatorType = OperatorType.MOBILE, isSaveSuccessLog = false)
    @PostMapping("/msLogin")
    public R<String> msLogin(MsLoginBody msLoginBody, HttpServletRequest request) {
        return R.ok("操作成功", appLoginService.msLogin(msLoginBody,request));
    }

}
