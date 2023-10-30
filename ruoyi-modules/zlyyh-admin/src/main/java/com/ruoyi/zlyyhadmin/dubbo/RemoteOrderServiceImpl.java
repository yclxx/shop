package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.system.api.RemoteOrderService;
import com.ruoyi.zlyyh.domain.ProductComputeDay;
import com.ruoyi.zlyyh.domain.ProductComputeMonth;
import com.ruoyi.zlyyh.domain.bo.OrderBackTransBo;
import com.ruoyi.zlyyh.domain.vo.OrderAndUserNumber;
import com.ruoyi.zlyyh.mapper.ProductComputeDayMapper;
import com.ruoyi.zlyyh.mapper.ProductComputeMonthMapper;
import com.ruoyi.zlyyhadmin.service.IOrderBackTransService;
import com.ruoyi.zlyyhadmin.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteOrderServiceImpl implements RemoteOrderService {

    private final ProductComputeMonthMapper productComputeMonthMapper;
    private final ProductComputeDayMapper productComputeDayMapper;
    private final IOrderBackTransService orderBackTransService;

    @Autowired
    private IOrderService orderService;

    @Override
    public void dataStatisticsDay() {
        int pageNum = 0;
        int pageSize = 10;
        Date yesterday = DateUtil.yesterday();
        Date startTime = DateUtil.beginOfDay(yesterday);
        Date endTime = DateUtil.endOfDay(yesterday);

        while (true) {
            List<OrderAndUserNumber> orderAndUserNumbers = orderService.queryUserAndOrderNum(startTime, endTime, pageNum, pageSize);
            if (orderAndUserNumbers.size() != 0) {
                List<ProductComputeDay> days = new ArrayList<>();
                if (orderAndUserNumbers.size() != 0) {
                    for (OrderAndUserNumber order : orderAndUserNumbers) {
                        ProductComputeDay month = new ProductComputeDay();
                        month.setId(IdUtil.getSnowflake().nextId());
                        month.setDayTime(yesterday);
                        month.setProductId(order.getProductId());
                        month.setCityCode(order.getCityCode());
                        month.setCityName(order.getCityName());
                        month.setCityUserNumber(order.getUserNumber());
                        month.setCityOrderNumber(order.getOrderNumber());
                        month.setDelFlag("0");
                        days.add(month);
                    }
                    if (days.size() != 0) {
                        productComputeDayMapper.insertBatch(days);
                    }
                }
                if (orderAndUserNumbers.size() < 10) break;
                pageNum += pageSize;
            } else {
                break;
            }

        }
    }

    @Override
    public void dataStatisticsMonth() {
        int pageNum = 0;
        int pageSize = 10;
        Date lastMonth = DateUtil.lastMonth();
        String lastMonthStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM, lastMonth);
        Date startTime = DateUtil.beginOfMonth(lastMonth);
        Date endTime = DateUtil.endOfMonth(lastMonth);
        while (true) {
            List<OrderAndUserNumber> orderAndUserNumbers = orderService.queryUserAndOrderNum(startTime, endTime, pageNum, pageSize);
            if (orderAndUserNumbers.size() != 0) {
                List<ProductComputeMonth> months = new ArrayList<>();
                for (OrderAndUserNumber order : orderAndUserNumbers) {
                    ProductComputeMonth month = new ProductComputeMonth();
                    month.setId(IdUtil.getSnowflake().nextId());
                    month.setMonth(lastMonthStr);
                    month.setProductId(order.getProductId());
                    month.setCityCode(order.getCityCode());
                    month.setCityName(order.getCityName());
                    month.setCityUserNumber(order.getUserNumber());
                    month.setCityOrderNumber(order.getOrderNumber());
                    month.setDelFlag("0");
                    months.add(month);
                }
                if (months.size() != 0) {
                    productComputeMonthMapper.insertBatch(months);
                }
                if (orderAndUserNumbers.size() < 10) break;
                pageNum += pageSize;
            } else {
                break;
            }
        }
    }

    /**
     * 订单退款
     *
     * @param number       订单号
     * @param amount       退款金额
     * @param refundRemark 退款原因
     */
    @Async
    public void refundOrder(Long number, BigDecimal amount, String refundRemark) {
        OrderBackTransBo bo = new OrderBackTransBo();
        bo.setNumber(number);
        bo.setRefund(amount);
        bo.setRefundReason(refundRemark);
        orderBackTransService.insertByBo(bo, true);
    }
}
