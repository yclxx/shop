package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodVo;
import com.ruoyi.zlyyhmobile.domain.bo.GrabPeriodProductQueryBo;
import com.ruoyi.zlyyhmobile.domain.vo.AppProductVo;
import com.ruoyi.zlyyhmobile.service.IGrabPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 秒杀配置控制器
 * 前端访问路由地址为:/zlyyh-mobile/grabPeriod/ignore
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/grabPeriod")
public class GrabPeriodController extends BaseController {

    private final IGrabPeriodService iGrabPeriodService;

    /**
     * 获取秒杀配置详细信息
     *
     * @param id 主键
     */
    @GetMapping("/ignore/{id}")
    public R<GrabPeriodVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iGrabPeriodService.queryById(id));
    }

    /**
     * 获取秒杀商品信息
     *
     */
    @GetMapping("/ignore/productList")
    public R<List<AppProductVo>> getProductList(GrabPeriodProductQueryBo bo) {
        return R.ok(iGrabPeriodService.getProductList(bo));
    }
}
