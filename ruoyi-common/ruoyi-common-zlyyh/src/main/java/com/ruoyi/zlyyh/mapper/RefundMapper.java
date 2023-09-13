package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Refund;
import com.ruoyi.zlyyh.domain.vo.RefundVo;

/**
 * 退款订单登记Mapper接口
 *
 * @author yzg
 * @date 2023-08-07
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface RefundMapper extends BaseMapperPlus<RefundMapper, Refund, RefundVo> {

}
