package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyhmobile.service.IOrderYzService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 有赞订单迁移
 * 前端访问路由地址为:/zlyyh-mobile/yz/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/yz/ignore")
public class OrderYzController extends BaseController {

    private final IOrderYzService orderYzService;

    /**
     * 查询广告管理列表
     */
    @GetMapping("/qy/{platformKey}")
    public R<Void> qy(@PathVariable("platformKey") Long platformKey) {
        if (null == platformKey) {
            return R.fail();
        }
        orderYzService.qy(platformKey);
        return R.ok();
    }
}
