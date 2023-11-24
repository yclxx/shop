package com.ruoyi.zlyyhmobile.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.log.enums.OperatorType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.utils.CloudRechargeEntity;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyh.utils.sdk.AcpService;
import com.ruoyi.zlyyh.utils.sdk.PayUtils;
import com.ruoyi.zlyyh.utils.sdk.SDKConstants;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.domain.vo.PayResultVo;
import com.ruoyi.zlyyhmobile.service.IHistoryOrderService;
import com.ruoyi.zlyyhmobile.service.IOrderBackTransService;
import com.ruoyi.zlyyhmobile.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * 订单控制器
 * 前端访问路由地址为:/zlyyh-mobile/order
 *
 * @author ruoyi
 * @date 2023-03-18
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final IOrderService orderService;
    private final IOrderBackTransService orderBackTransService;
    private final IHistoryOrderService historyOrderService;

    /**
     * 创建订单
     *
     * @return 订单信息
     */
    @Log(title = "用户订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/createOrder")
    public R<CreateOrderResult> createOrder(@Validated @RequestBody CreateOrderBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        bo.setAdcode(ZlyyhUtils.getAdCode());
        bo.setCityName(ZlyyhUtils.getCityName());
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setChannel(ZlyyhUtils.getPlatformChannel());
        bo.setShareUserId(ZlyyhUtils.getShareUserId());
        return R.ok(orderService.createOrder(bo, false));
    }

    /**
     * 购物车创建订单
     *
     * @return 订单信息
     */
    @Log(title = "用户订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/createCarOrder")
    public R<CreateOrderResult> createCarOrder(@Validated @RequestBody CreateOrderBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        bo.setAdcode(ZlyyhUtils.getAdCode());
        bo.setCityName(ZlyyhUtils.getCityName());
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setChannel(ZlyyhUtils.getPlatformChannel());
        bo.setShareUserId(ZlyyhUtils.getShareUserId());
        return R.ok(orderService.createCarOrder(bo, false));
    }

    /**
     * 订单支付
     *
     * @return 订单信息
     */
    @Log(title = "用户订单", businessType = BusinessType.PAY, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/payOrder/{collectiveNumber}")
    public R<PayResultVo> payOrder(@NotNull(message = "请求错误")
                                   @PathVariable("collectiveNumber") Long collectiveNumber) {
        return R.ok("操作成功", orderService.payOrder(collectiveNumber, LoginHelper.getUserId()));
    }

    /**
     * 查询订单支付状态
     *
     * @param collectiveNumber 订单号
     * @return 返回结果
     */
    @GetMapping(value = "/queryOrderPay/{collectiveNumber}")
    public R<Void> queryOrderPay(@NotNull(message = "缺失必要参数")
                                 @PathVariable("collectiveNumber") Long collectiveNumber) {
        return R.ok(orderService.queryOrderPay(collectiveNumber));
    }

    /**
     * 微信支付回调
     */
    @RequestMapping("/ignore/wxCallBack/{merchantId}")
    public R<Void> wxCallBack(@PathVariable("merchantId") Long merchantId, HttpServletRequest request) {
        boolean b = orderService.wxCallBack(merchantId, request);
        if (!b) {
            R.fail("业务处理失败");
        }
        return R.ok();
    }

    /**
     * 微信退款回调
     */
    @SaIgnore
    @RequestMapping("/ignore/wxRefundCallBack/{merchantId}")
    public R<Void> wxRefundCallBack(@PathVariable("merchantId") Long merchantId, HttpServletRequest request) {
        orderBackTransService.wxRefundCallBack(merchantId, request);
        return R.ok();
    }

    /**
     * 订单支付回调 云闪付
     */
    @RequestMapping("/ignore/payCallBack")
    public void payCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("银联支付回调接收开始");
        String encoding = request.getParameter(SDKConstants.param_encoding);
        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> valideData = PayUtils.getAllRequestParam(request, encoding);
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(valideData, encoding, false)) {
            log.info("验证签名结果[失败].");
            //验签失败，需解决验签问题
            // 直接返回错误状态码，不解决
            response.sendError(417, "验签失败");
            return;
        } else {
            log.info("验证签名结果[成功].");
            //【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
            // 处理业务
            orderService.payCallBack(valideData);
        }
        log.info("BackRcvResponse接收后台通知结束");
        //返回给银联服务器http 200  状态码
        response.getWriter().print("ok");
    }

    /**
     * 订单支付回调 银联分销  (直销产品)
     */
    @RequestMapping("/ignore/unionPayBack")
    public void unionPayBack(@RequestBody JSONObject data) {
        log.info("银联分销支付回调接收开始：{}", data);
        if (ObjectUtil.isEmpty(data)) {
            log.info("银联分销支付回调结果[失败].");
            return;
        } else {
            orderService.unionPayBack(data);
        }
        log.info("银联分销支付回调结束");
    }

    /**
     * 订单支付回调 美食套餐
     */
    @RequestMapping("/ignore/foodPayCallBack")
    public void foodPayCallBack(@RequestBody JSONObject jsonObject) {
        log.info("美食订单支付回调接收开始");
        JSONObject data = jsonObject.getJSONObject("data");
        if (ObjectUtil.isEmpty(data)) {
            log.info("美食订单支付回调结果[失败].");
            return;
        } else {
            orderService.foodPayCallBack(data);
        }
        log.info("美食订单支付回调结束");
    }

    /**
     * 订单支付回调 携程订单
     */
    @RequestMapping("/ignore/ctripOrderCallBack")
    public void ctripOrderCallBack(@RequestBody JSONObject jsonObject) {
        log.info("携程订单支付回调接收开始");
        String orderId = jsonObject.getString("orderId");
        if (ObjectUtil.isEmpty(orderId)) {
            log.info("美食订单支付回调结果[失败].");
            return;
        } else {
            orderService.ctripOrderCallBack(jsonObject);
        }
        log.info("携程订单支付回调结束");
    }

    /**
     * 退款成功回调 美食套餐
     */
    @RequestMapping("/ignore/cancelCallBack")
    public void cancelCallBack(@RequestBody JSONObject jsonObject) {
        log.info("美食订单退款回调接收开始");
        JSONObject data = jsonObject.getJSONObject("data");
        if (ObjectUtil.isEmpty(data)) {
            log.info("美食订单退款回调结果[失败].");
            return;
        } else {
            orderService.foodCancelCallBack(data);
        }
        log.info("美食订单退款回调结束");
    }

    /**
     * 获取订单详情
     *
     * @return 订单信息
     */
    @GetMapping("/getOrderInfo/{number}")
    public R<OrderVo> getOrderInfo(@NotNull(message = "请求错误")
                                   @PathVariable("number") Long number) {
        OrderVo orderVo = orderService.queryById(number);
        if (null != orderVo && !orderVo.getUserId().equals(LoginHelper.getUserId())) {
            throw new ServiceException("登录超时,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        return R.ok(orderVo);
    }

    /**
     * 订单回调通知
     */
    @RequestMapping("/ignore/orderCallback")
    public R<Void> orderCallback(@RequestBody CloudRechargeEntity huiguyunEntity) {
        orderService.cloudRechargeCallback(huiguyunEntity);
        return R.ok();
    }

    /**
     * 获取订单列表
     *
     * @return 订单列表
     */
    @GetMapping("/getOrderList")
    public TableDataInfo<OrderVo> getOrderList(OrderBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        //暂不需要支持端查订单
        //bo.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        return orderService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询未支付订单数量
     *
     * @return 返回结果
     */
    @GetMapping("/queryUserOrderCount")
    public R<Long> queryUserOrderCount() {
        return R.ok(orderService.queryUserOrderCount(LoginHelper.getUserId()));
    }

    /**
     * 取消订单
     *
     * @return 订单列表
     */
    @Log(title = "用户订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    @PostMapping("/cancel/{collectiveNumber}")
    public R<Void> cancel(@NotNull(message = "请求错误")
                          @PathVariable("collectiveNumber") Long collectiveNumber) {
        orderService.cancel(collectiveNumber, LoginHelper.getUserId());
        return R.ok();
    }

    @Log(title = "用户订单", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    @PostMapping("/del/{numbers}")
    public R<Integer> remove(@NotEmpty(message = "主键不能为空")
                             @PathVariable String[] numbers) {
        return R.ok(orderService.deleteWithValidByIds(Arrays.asList(numbers), LoginHelper.getUserId(), true) ? 1 : 0);
    }

    @PostMapping("/orderRefund/{number}")
    public R<Void> orderRefund(@NotNull(message = "请求错误")
                               @PathVariable("number") Long number) {
        orderService.orderRefund(number, LoginHelper.getUserId());
        return R.ok();
    }

    @PostMapping("/historyOrderRefund/{number}")
    public R<Void> historyOrderRefund(@NotNull(message = "请求错误")
                                      @PathVariable("number") Long number) {
        historyOrderService.historyOrderRefund(number, LoginHelper.getUserId(), ZlyyhUtils.getPlatformChannel());
        return R.ok();
    }

    /**
     * 退款回调 云闪付
     *
     * @param request
     * @param response
     */
    @RequestMapping("/ignore/refundCallBack")
    public void backUrlTrans(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("银联退款回调BackRcvResponse接收后台通知开始");
        String encoding = request.getParameter(SDKConstants.param_encoding);
        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> valideData = PayUtils.getAllRequestParam(request, encoding);
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(valideData, encoding, false)) {
            log.info("验证签名结果[失败].");
            //验签失败，需解决验签问题
            // 直接返回错误状态码，不解决
            response.sendError(417, "验签失败");
            return;
        } else {
            log.info("验证签名结果[成功].");
            //【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
            String respCode = valideData.get("respCode");//获取后台通知的数据，其他字段也可用类似方式获取
            //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
            if ("00".equals(respCode)) {  // 00 交易成功
                // 此处暂时不做业务处理失败处理，因为就算失败，银联重新发起回调还是会失败
                orderBackTransService.refundCallBack(valideData);
            }
        }
        log.info("银联退款回调BackRcvResponse接收后台通知结束");
        //返回给银联服务器http 200  状态码
        response.getWriter().print("ok");
    }

    /**
     * 联联自动发码订单回调接口
     *
     * @param param
     * @return
     */
    @PostMapping("/ignore/lianOrderCodeCall")
    public R<Void> lianOrderCodeCall(@RequestBody JSONObject param) {
        log.info("新联联自动发码订单回调通知接收参数解密前：{}", param);
        orderService.lianOrderBack(param, 1);
        return R.ok();
    }

    /**
     * 联联订单退款 回调
     *
     * @param param
     * @return
     */
    @PostMapping("/ignore/lianOrderReturn")
    public R<Void> lianOrderReturn(@RequestBody JSONObject param) {
        log.info("联联订单退款通知接收参数解密前：{}", param);
        orderService.lianOrderBack(param, 2);
        return R.ok();
    }

    /**
     * 联联订单核销
     */
    @PostMapping("/ignore/lianOrderCall")
    public R<Void> lianOrderCall(@RequestBody JSONObject param) {
        log.info("新联联订单核销回调通知接收参数解密前：{}", param);
        orderService.lianOrderBack(param, 3);
        return R.ok();
    }

    /**
     * 银联票券状态变动通知
     */
    @RequestMapping("/ignore/couponStatusCallback")
    public R<Void> couponStatusCallback(HttpServletRequest request) {
        String body = ServletUtils.getParamJson(request);
        String headers = JsonUtils.toJsonString(ServletUtils.getHeaderMap(request));
        log.info("银联票券状态变动通知，请求头：{}，接收参数：{}", headers, body);
        return R.ok();
    }
}
