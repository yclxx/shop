package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyhadmin.domain.bo.HsmBo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 银联加密机
 * 前端访问路由地址为:/zlyyh-admin/hsm
 *
 * @author ruoyi
 * @date 2023-03-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/hsm")
public class HsmController extends BaseController {

    /**
     * 加密
     */
    @SaCheckPermission("zlyyh:hsm:add")
    @PostMapping()
    public R<String> add(@Validated(AddGroup.class) @RequestBody HsmBo bo) {
        return R.ok();
//        return R.ok("操作成功", HsmEncryptor.getInstance().encrypt(bo.getStr()));
    }
}
