package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.OrderInfo;
import com.ruoyi.zlyyh.domain.vo.OrderInfoVo;
import org.apache.ibatis.annotations.Param;

/**
 * 订单扩展信息Mapper接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface OrderInfoMapper extends BaseMapperPlus<OrderInfoMapper, OrderInfo, OrderInfoVo> {
    /**
     * 物理删除订单详情
     * @param number
     * @return
     */
    Long deleteByNumber(@Param("number") String number);
}
