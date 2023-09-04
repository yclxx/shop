package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.Cust;
import java.util.List;

public interface ICustService {

    List<Cust> selectAll(String startTime,String endTime,int pageNum,int pageSize);
}
