package com.ruoyi.zlyyhmobile.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 25487
 */
public interface IUnionPayContentOrderService {

    /**
     * 银联内容分销 内容方 发券-退券-订单查询
     *
     * @param request          请求
     * @param response         响应
     * @return 结果
     */
    JSONObject unionPay(HttpServletRequest request, HttpServletResponse response);

}
