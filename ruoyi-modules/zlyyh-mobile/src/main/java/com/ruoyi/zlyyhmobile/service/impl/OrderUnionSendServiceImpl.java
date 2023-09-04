package com.ruoyi.zlyyhmobile.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderUnionSend;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.OrderUnionSendVo;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.mapper.OrderUnionSendMapper;
import com.ruoyi.zlyyh.utils.sdk.UnionPayMerchantUtil;
import com.ruoyi.zlyyhmobile.service.IOrderBackTransService;
import com.ruoyi.zlyyhmobile.service.OrderUnionSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OrderUnionSendServiceImpl implements OrderUnionSendService {
    private final OrderUnionSendMapper baseMapper;
    private final OrderMapper orderMapper;
    private final IOrderBackTransService orderBackTransService;

    @Override
    public List<OrderUnionSendVo> queryListByNumber(Long number) {
        LambdaQueryWrapper<OrderUnionSend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderUnionSend::getNumber, number);
        return baseMapper.selectVoList(queryWrapper);
    }

    @Override
    public void insertList(Long number, String prodTn, String prodTp, JSONArray bondLst, String certPath, String certPwd) {
        List<OrderUnionSend> sendList = new ArrayList<>();
        for (int i = 0; i < bondLst.size(); i++) {
            JSONObject bond = bondLst.getJSONObject(i);
            OrderUnionSend send = new OrderUnionSend();
            send.setNumber(number);
            send.setProdTn(prodTn);
            send.setProdTp(prodTp);
            send.setBondSerlNo(bond.getString("bondSerlNo"));
            send.setBondNo(UnionPayMerchantUtil.sm2Decrypt(bond.getString("bondNo"), certPath, certPwd));
            send.setBondEncNo(bond.getString("bondEncNo"));
            send.setCusIstr(bond.getString("cusIstr"));
            send.setBondSt(bond.getString("bondSt"));
            String effectDtTm = bond.getString("effectDtTm");
            if (StringUtils.isNotEmpty(effectDtTm))
                send.setEffectDtTm(DateUtils.dateTime(DateUtils.YYYYMMDDHHMMSS, effectDtTm));
            String exprDtTm = bond.getString("exprDtTm");
            if (StringUtils.isNotEmpty(exprDtTm))
                send.setExprDtTm(DateUtils.dateTime(DateUtils.YYYYMMDDHHMMSS, exprDtTm));
            sendList.add(send);
        }
        if (!sendList.isEmpty()) {
            baseMapper.insertBatch(sendList);
        }
    }

    /**
     * 核销通知 （银联分销）
     */
    @Override
    public void couponNotification(HttpServletResponse response, JSONObject data) throws IOException {
        // 券码流水号
        String bondSerlNo = data.getString("bondSerlNo");
        LambdaQueryWrapper<OrderUnionSend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderUnionSend::getBondSerlNo, bondSerlNo);
        OrderUnionSend send = baseMapper.selectOne(queryWrapper);
        if (send != null) {
            send.setBondSt("06");
            baseMapper.updateById(send);
            response.getWriter().print("ok");
        } else {
            Map<String, String> result = new HashMap<>();
            result.put("code", "40040000");
            result.put("msg", "请求业务失败");
            response.getWriter().print(result);
        }
    }

    /**
     * 退券通知
     * @param response
     * @param data
     * @throws IOException
     */
    @Override
    public void couponRefundNotification(HttpServletResponse response, JSONObject data) throws IOException {
        String prodTn = data.getString("prodTn");
        String bondSt = data.getString("bondSt");
        String rfdTn = data.getString("rfdTn");
        LambdaQueryWrapper<OrderUnionSend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderUnionSend::getProdTn, prodTn);
        OrderUnionSend send = baseMapper.selectOne(queryWrapper);
        if (send != null) {
            Order order = orderMapper.selectById(send.getNumber());
            // 代销退款
            if (!order.getPickupMethod().equals("0")) {
                if (order.getOrderType().equals("11")) {
                    OrderBackTransBo bo = new OrderBackTransBo();
                    bo.setNumber(order.getNumber());
                    bo.setOrderBackTransState("0");
                    bo.setPickupMethod(order.getPickupMethod());
                    bo.setRefund(order.getWantAmount());
                    orderBackTransService.insertByBo(bo);
                }
            }
            order.setStatus("4");
            orderMapper.updateById(order);

            send.setBondSt(bondSt);
            send.setRfdTn(rfdTn);
            baseMapper.updateById(send);
            response.getWriter().print("ok");
        } else {
            Map<String, String> result = new HashMap<>();
            result.put("code", "40040000");
            result.put("msg", "请求业务失败");
            response.getWriter().print(result);
        }
    }

    /**
     * 卡券返还通知
     */
    @Override
    public void couponRestitutionNotification(HttpServletResponse response, JSONObject data) throws IOException {
        String bondSerlNo = data.getString("bondSerlNo");
        String bondSt = data.getString("bondSt");
        LambdaQueryWrapper<OrderUnionSend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderUnionSend::getBondSerlNo, bondSerlNo);
        OrderUnionSend send = baseMapper.selectOne(queryWrapper);
        if (send != null) {
            send.setBondSt(bondSt);
            baseMapper.updateById(send);
            response.getWriter().print("ok");
        } else {
            Map<String, String> result = new HashMap<>();
            result.put("code", "40040000");
            result.put("msg", "请求业务失败");
            response.getWriter().print(result);
        }
    }

    /**
     * 退款通知
     */
    @Override
    public void refundNotification(HttpServletResponse response, JSONObject data) throws IOException {
        String prodTn = data.getString("prodTn");
        String bondSerlNo = data.getString("bondSerlNo");
        String bondSt = data.getString("bondSt");
        LambdaQueryWrapper<OrderUnionSend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderUnionSend::getProdTn, prodTn);
        queryWrapper.eq(OrderUnionSend::getBondSerlNo, bondSerlNo);
        OrderUnionSend send = baseMapper.selectOne(queryWrapper);
        if (send != null) {
            Order order = orderMapper.selectById(send.getNumber());
            if (bondSt.equals("15")) {
                order.setStatus("4");
            } else if (bondSt.equals("16")) {
                order.setStatus("5");
            }
            orderMapper.updateById(order);

            send.setBondSt(bondSt);
            baseMapper.updateById(send);
            response.getWriter().print("ok");
        } else {
            Map<String, String> result = new HashMap<>();
            result.put("code", "40040000");
            result.put("msg", "请求业务失败");
            response.getWriter().print(result);
        }
    }
}
