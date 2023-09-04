package com.ruoyi.zlyyhadmin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.ruoyi.zlyyh.domain.CustOrder;
import com.ruoyi.zlyyh.mapper.CustOrderMapper;
import com.ruoyi.zlyyhadmin.service.ICustOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
@DS("location")
public class CustOrderServiceImpl implements ICustOrderService {

    private final CustOrderMapper baseMapper;

    /**
     * 同步订单数据
     */
    @Override
    public List<CustOrder> syncOrderData(String startTime,String endTime,int pageNum,int pageSize){
        return baseMapper.syncOrderData(startTime,endTime,pageNum,pageSize);
    }

}
