package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.InviteUserLog;
import com.ruoyi.zlyyh.domain.vo.InviteUserLogVo;

/**
 * 邀请记录Mapper接口
 *
 * @author yzg
 * @date 2023-08-08
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface InviteUserLogMapper extends BaseMapperPlus<InviteUserLogMapper, InviteUserLog, InviteUserLogVo> {

}
