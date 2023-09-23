package com.ruoyi.zlyyhmobile.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.zlyyh.domain.vo.UnionPayContentOrderVo;
import com.ruoyi.zlyyhmobile.domain.bo.UnionPayCreateBo;

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
     * @param unionPayCreateBo 请求参数
     * @return 结果
     */
    JSONObject unionPay(HttpServletRequest request, HttpServletResponse response, UnionPayCreateBo unionPayCreateBo);

    /**
     * 查询内容分销订单
     * @param number 订单号
     * @return 订单信息
     */
    UnionPayContentOrderVo queryByNumber(Long number);
}
