package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.OrderPushInfo;
import com.ruoyi.zlyyh.domain.vo.OrderPushInfoVo;
import org.apache.ibatis.annotations.Param;

/**
 * 订单取码记录Mapper接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface OrderPushInfoMapper extends BaseMapperPlus<OrderPushInfoMapper, OrderPushInfo, OrderPushInfoVo> {
    /**
     * 物理删除发送订单详情
     * @param id
     * @return
     */
    Long deleteById(@Param("id") Long id);
}
