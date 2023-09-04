package com.ruoyi.zlyyh.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Cust;
import com.ruoyi.zlyyh.domain.vo.CustVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustMapper extends BaseMapperPlus<CustMapper, Cust, CustVo> {

    List<Cust> selectAll(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("pageNum") int pageNum,@Param("pageSize") int pageSize);
}
