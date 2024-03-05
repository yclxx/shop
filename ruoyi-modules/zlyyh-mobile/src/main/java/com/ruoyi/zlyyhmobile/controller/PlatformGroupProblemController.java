package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.PlatformGroupProblemBo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IPlatformGroupProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信二维码管理控制器
 * 前端访问路由地址为:/zlyyh-mobile/platformGroupProblem/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/platformGroupProblem/ignore")
public class PlatformGroupProblemController extends BaseController {

    private final IPlatformGroupProblemService platformGroupProblemService;

    /**
     * 新增
     */
    @GetMapping("/insert")
    public R<Void> insert(PlatformGroupProblemBo bo){
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setUserId(LoginHelper.getUserId());
        platformGroupProblemService.insertByBo(bo);
        return R.ok();
    }

}
