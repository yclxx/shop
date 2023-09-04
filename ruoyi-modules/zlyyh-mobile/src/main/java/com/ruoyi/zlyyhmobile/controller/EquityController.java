package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.vo.EquityVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IEquityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权益包控制器
 * 前端访问路由地址为:/zlyyh-mobile/equity
 *
 * @author yzg
 * @date 2023-06-06
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/equity")
public class EquityController extends BaseController {

    private final IEquityService iEquityService;

    /**
     * 获取权益包详细信息
     *
     * @param equityId 主键
     */
    @GetMapping("/ignore/getInfo/{equityId}")
    public R<EquityVo> getInfo(@PathVariable Long equityId) {
        return R.ok(iEquityService.queryById(equityId));
    }

    /**
     * 获取权益包详细信息
     */
    @GetMapping("/ignore/getInfo")
    public R<EquityVo> getInfo() {
        return R.ok(iEquityService.queryByPlatformId(ZlyyhUtils.getPlatformId()));
    }
}
