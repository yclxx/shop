package com.ruoyi.zlyyhmobile.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.zlyyh.domain.vo.OrderUnionSendVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface OrderUnionSendService {
    /**
     * 查询订单核销码
     */
    List<OrderUnionSendVo> queryListByNumber(Long number);

    void insertList(Long number, String prodTn, String prodTp, JSONArray bondLst, String certPath, String certPwd);

    /**
     * 核销通知
     */
    void couponNotification(HttpServletResponse response, JSONObject data) throws IOException;

    /**
     * 退券通知
     */
    void couponRefundNotification(HttpServletResponse response, JSONObject data) throws IOException;

    /**
     * 卡券返还通知
     */
    void couponRestitutionNotification(HttpServletResponse response, JSONObject data) throws IOException;

    void refundNotification(HttpServletResponse response, JSONObject data) throws IOException;
}
