package com.ruoyi.auth.controller;

import com.ruoyi.auth.form.AppLoginBody;
import com.ruoyi.auth.form.WxMobileLoginBody;
import com.ruoyi.auth.service.VerifierTokenService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.log.enums.OperatorType;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * token 微信登录配置（商户端）
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/verifier")
public class VerifierTokenController {
    private final VerifierTokenService verifierTokenService;

    /**
     * 微信小程序 静默登录
     *
     * @param ysfLoginBody 登录信息
     * @return 结果
     */
    @Log(title = "用户登录", businessType = BusinessType.GRANT, operatorType = OperatorType.MOBILE, isSaveSuccessLog = false)
    @PostMapping("/wxLogin")
    public R<String> wxLogin(@Validated @RequestBody AppLoginBody ysfLoginBody, HttpServletRequest request) {
        return R.ok("操作成功", verifierTokenService.wxLogin(ysfLoginBody, request));
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
        return R.ok("操作成功", verifierTokenService.wxLoginByMobile(ysfLoginBody, request));
    }

    /**
     * 登出方法
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        verifierTokenService.logout();
        return R.ok();
    }
}
