package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.OrderTicket;
import com.ruoyi.zlyyh.domain.bo.OrderTicketBo;
import com.ruoyi.zlyyh.domain.vo.OrderTicketVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 演出票订单Mapper接口
 *
 * @author yzg
 * @date 2023-09-22
 */
public interface OrderTicketMapper extends BaseMapperPlus<OrderTicketMapper, OrderTicket, OrderTicketVo> {

    Page<OrderTicketVo> selectVoPages(@Param("page") Page<OrderTicket> page, @Param("bo") OrderTicketBo bo);

    BigDecimal getOrderTicketLineNumber(Long lineId);
}
