package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.OrderBackTrans;
import com.ruoyi.zlyyh.domain.vo.OrderBackTransVo;

/**
 * 退款订单Mapper接口
 *
 * @author yzg
 * @date 2023-04-03
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface OrderBackTransMapper extends BaseMapperPlus<OrderBackTransMapper, OrderBackTrans, OrderBackTransVo> {

}
