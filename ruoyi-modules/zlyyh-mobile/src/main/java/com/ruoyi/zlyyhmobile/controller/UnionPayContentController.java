package com.ruoyi.zlyyhmobile.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.zlyyhmobile.domain.bo.UnionPayCreateBo;
import com.ruoyi.zlyyhmobile.service.IUnionPayContentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 银联分销内容方控制器
 * 前端访问路由地址为:/zlyyh-mobile/unionPay
 *
 * @author yzg
 * @date 2023-09-15
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionPay")
public class UnionPayContentController {
    private final IUnionPayContentOrderService publisherPayContentOrderService;

    /**
     * 内容分销 发券
     */
    @PostMapping("/order")
    public JSONObject distributionSend(HttpServletRequest request, HttpServletResponse response, @RequestBody UnionPayCreateBo unionPayCreateBo) {
        return publisherPayContentOrderService.unionPay(request, response, unionPayCreateBo);
    }
}
