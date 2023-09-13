package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.HistoryOrder;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderVo;

/**
 * 历史订单Mapper接口
 *
 * @author yzg
 * @date 2023-08-01
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface HistoryOrderMapper extends BaseMapperPlus<HistoryOrderMapper, HistoryOrder, HistoryOrderVo> {

}
