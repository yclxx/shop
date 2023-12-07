package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.log.enums.OperatorType;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.vo.OrderTicketVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderTicketBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.service.OrderTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderTicket")
public class OrderTicketController {
    private final OrderTicketService orderTicketService;


    /**
     * 创建订单
     *
     * @return 订单信息
     */
    @Log(title = "用户订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/createTicketOrder")
    public R<CreateOrderResult> createTicketOrder(@Validated @RequestBody CreateOrderTicketBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        bo.setAdcode(ZlyyhUtils.getAdCode());
        bo.setCityName(ZlyyhUtils.getCityName());
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setShareUserId(ZlyyhUtils.getShareUserId());
        return R.ok(orderTicketService.createTicketOrder(bo));
    }

    /**
     * 查看订单详情
     *
     * @return 订单信息
     */
    @Log(title = "用户订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @GetMapping("/getOrderInfo/{number}")
    public R<OrderTicketVo> getOrderInfo(@NotNull(message = "请求错误")
                                         @PathVariable("number") Long number) {
        OrderTicketVo orderTicketVo = orderTicketService.orderInfo(number);
        if (ObjectUtil.isNotEmpty(orderTicketVo)) {
            // 数据脱敏
            orderTicketVo.setMobile(DesensitizedUtil.mobilePhone(orderTicketVo.getMobile()));
            orderTicketVo.setTel(DesensitizedUtil.mobilePhone(orderTicketVo.getTel()));
            if (ObjectUtil.isNotEmpty(orderTicketVo.getOrderIdCardVos())) {
                orderTicketVo.getOrderIdCardVos().forEach(o -> {
                    String s = DesensitizedUtil.idCardNum(o.getIdCard(), 6, 2);
                    o.setIdCard(s);
                });
            }
        }
        return R.ok(orderTicketVo);
    }

    /**
     * 查看历史订单信息
     */
    @Log(title = "用户订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @GetMapping("/getHistoryOrderInfo/{number}")
    public R<OrderTicketVo> getHistoryOrderInfo(@NotNull(message = "请求错误")
                                                @PathVariable("number") Long number) {
        return R.ok(orderTicketService.getHistoryOrderInfo(number));
    }
}
