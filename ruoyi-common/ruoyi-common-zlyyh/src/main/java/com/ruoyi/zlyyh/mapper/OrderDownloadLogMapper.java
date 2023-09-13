package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.OrderDownloadLog;
import com.ruoyi.zlyyh.domain.vo.OrderDownloadLogVo;

/**
 * 订单下载记录Mapper接口
 *
 * @author yzg
 * @date 2023-04-01
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface OrderDownloadLogMapper extends BaseMapperPlus<OrderDownloadLogMapper, OrderDownloadLog, OrderDownloadLogVo> {

}
