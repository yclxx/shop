package com.ruoyi.zlyyhmobile.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.vo.OrderAndUserNumber;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.utils.CloudRechargeEntity;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 25487
 */
public interface IOrderService {

    /**
     * 保存订单至数据库
     *
     * @param number 订单号
     */
    void saveOrderToMySql(Long number);

    /**
     * 订单发券
     *
     * @param number 订单号
     */
    void sendCoupon(Long number);

    /**
     * 查询发放状态
     *
     * @param pushNumber 取码订单号
     */
    void queryOrderSendStatus(String pushNumber);

    /**
     * 创建订单
     *
     * @return 返回结果
     */
    CreateOrderResult createOrder(CreateOrderBo bo, boolean system);

    /**
     * 查询订单详情
     *
     * @param number 订单number
     * @return 用户信息
     */
    OrderVo queryById(Long number);

    /**
     * 查询订单详情
     *
     * @param number 订单number
     * @return 用户信息
     */
    OrderVo queryBaseOrderById(Long number);

    /**
     * 查询订单列表
     */
    TableDataInfo<OrderVo> queryPageList(OrderBo bo, PageQuery pageQuery);

    /**
     * 取消订单
     *
     * @param number 订单号
     * @param userId 用户ID
     */
    void cancel(Long number, Long userId);

    /**
     * 手机端订单退款
     *
     * @param number 订单号
     * @param userId 用户ID
     */
    void orderRefund(Long number, Long userId);

    /**
     * 订单支付
     *
     * @param number 订单号
     * @return 返回tn号，已支付返回 ok
     */
    String payOrder(Long number, Long userId);

    Order updateOrder(Order order);

    /**
     * 查询订单支付状态
     *
     * @param number 订单号
     * @return 支付结果
     */
    String queryOrderPay(Long number);

    /**
     * 查询用户未支付订单数量
     *
     * @param userId 用户ID
     * @return 订单号
     */
    long queryUserOrderCount(Long userId);

    /**
     * 校验并删除数据
     *
     * @param ids     主键集合
     * @param isValid 是否校验,true-删除前校验,false-不校验
     * @return true
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Long userId, Boolean isValid);

    /**
     * 定时任务取消订单
     */
    void cancelOrder();

    /**
     * 定时任务取消订单
     */
    void cancelOrder(Long userId);

    /**
     * 发放状态
     */
    List<OrderVo> sendStatusOrder();

    /**
     * 云闪付支付回调
     *
     * @param data 通知信息
     */
    void payCallBack(Map<String, String> data);

    void foodPayCallBack(JSONObject data);

    void ctripOrderCallBack(JSONObject data);

    void unionPayBack(JSONObject data);

    void foodCancelCallBack(JSONObject data);

    /**
     * 查询产品发放额度
     *
     * @param productIds 产品ID
     * @return 发放额度
     */
    BigDecimal sumSendValueByProductIds(List<Long> productIds, Long userId);

    /**
     * 查询产品支付金额
     *
     * @param productIds 产品ID
     * @return 发放额度
     */
    BigDecimal sumOutAmountByProductIds(List<Long> productIds, Long userId);

    /**
     * 充值中心订单回调
     *
     * @param cloudRechargeEntity 通知参数
     */
    void cloudRechargeCallback(CloudRechargeEntity cloudRechargeEntity);

    /**
     * 获取最后购买的产品订单
     *
     * @param productIds 产品ID集合 从这里面找
     * @param userId     用户ID
     * @return 最后购买的产品订单
     */
    OrderVo getLastOrder(List<Long> productIds, Long userId);

    /**
     * 查询订单列表
     *
     * @return 订单集合
     */
    TableDataInfo<Order> queryHistoryPageList(Integer day, PageQuery pageQuery);

    /**
     * 订单数据统计查询
     *
     * @param startTime
     * @param endTime
     * @param indexNum
     * @param indexPage
     * @return
     */
    List<OrderAndUserNumber> queryUserAndOrderNum(Date startTime, Date endTime, Integer indexNum, Integer indexPage);

    /**
     * 订单发送预警信息
     *
     * @param platformId
     * @param backendToken
     */
    void ysfForewarningMessage(Long platformId, String backendToken, String desc_Details, String template_Value);

    /**
     * 根据用户id查询订单数量
     */
    Long queryNumberByUserId(List<Long> userIds, String status, Integer type, Date dateTime);

    /**
     * 查询用户多少天内的订单数量
     *
     * @param userId 用户ID
     * @param day    多少天内
     * @return 订单数量
     */
    Long countByUserId(Long userId, Integer day);

    /**
     * 查询订单信息
     *
     * @param externalOrderNumber 供应商订单号
     * @return 订单信息
     */
    OrderVo queryByExternalOrderNumber(String externalOrderNumber);
}
