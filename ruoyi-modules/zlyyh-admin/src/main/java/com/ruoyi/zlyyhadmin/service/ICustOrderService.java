package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.CustOrder;

import java.util.List;

public interface ICustOrderService {

    List<CustOrder> syncOrderData(String startTime,String endTime,int pageNum,int pageSize);
}
