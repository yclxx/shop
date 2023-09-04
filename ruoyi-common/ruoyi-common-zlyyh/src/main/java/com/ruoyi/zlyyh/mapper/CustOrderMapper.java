package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.CustOrder;
import com.ruoyi.zlyyh.domain.vo.CustOrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustOrderMapper extends BaseMapperPlus<CustOrderMapper, CustOrder, CustOrderVo> {

    List<CustOrder> syncOrderData(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("pageNum")int pageNum,@Param("pageSize")int pageSize);
}
