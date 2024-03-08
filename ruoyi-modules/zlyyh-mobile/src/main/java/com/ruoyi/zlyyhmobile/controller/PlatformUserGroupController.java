package com.ruoyi.zlyyhmobile.controller;


import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.PlatformUserGroupBo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IPlatformUserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信用户来源管理控制器
 * 前端访问路由地址为:/zlyyh-mobile/platformUserGroup/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/platformUserGroup/ignore")
public class PlatformUserGroupController extends BaseController {

    private final IPlatformUserGroupService platformUserGroupService;

    /**
     * 新增
     */
    @GetMapping("/insert")
    public R<Void> insert(PlatformUserGroupBo bo){
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setUserId(LoginHelper.getUserId());
        platformUserGroupService.insert(bo);
        return R.ok();
    }


}
