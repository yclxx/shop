package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.OrderBackTransVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 退款订单Service接口
 *
 * @author yzg
 * @date 2023-04-03
 */
public interface IOrderBackTransService {

    /**
     * 查询退款订单
     */
    OrderBackTransVo queryById(String thNumber);

    /**
     * 退款订单回调 云闪付
     *
     * @param data 通知参数
     */
    void refundCallBack(Map<String, String> data);

    /**
     * 新增退款订单
     */
    Boolean insertByBo(OrderBackTransBo bo);

    /**
     * 微信退款回调业务处理
     */
    void wxRefundCallBack(Long merchantId, HttpServletRequest request);
}
