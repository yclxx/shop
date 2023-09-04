package com.ruoyi.zlyyhadmin.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.ruoyi.zlyyh.domain.Cust;
import com.ruoyi.zlyyh.mapper.CustMapper;
import com.ruoyi.zlyyhadmin.service.ICustService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@DS("location")
public class CustServiceImpl implements ICustService {

    private final CustMapper baseMapper;

    @Override
    public List<Cust> selectAll(String startTime,String endTime,int pageNum,int pageSize) {
        return baseMapper.selectAll(startTime,endTime,pageNum,pageSize);
    }
}
