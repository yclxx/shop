package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.vo.OrderAndUserNumber;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单Mapper接口
 *
 * @author yzgnet
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface OrderMapper extends BaseMapperPlus<OrderMapper, Order, OrderVo> {

    BigDecimal sumSendValue(@Param(Constants.WRAPPER) Wrapper<Order> wrapper);

    BigDecimal sumOutAmount(@Param(Constants.WRAPPER) Wrapper<Order> wrapper);


    @InterceptorIgnore(tenantLine = "1")
    List<OrderAndUserNumber> queryUserAndOrderNum(@Param("startDateTime") Date startDateTime, @Param("endDateTime") Date endDateTime,
                                                  @Param("indexNum") Integer indexNum, @Param("indexPage") Integer indexPage);

    /**
     * 物理删除订单信息
     */
    Long deleteByNumber(@Param("number") String number);

}
