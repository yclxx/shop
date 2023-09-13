package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.MissionUser;
import com.ruoyi.zlyyh.domain.vo.MissionUserVo;

/**
 * 任务用户Mapper接口
 *
 * @author yzg
 * @date 2023-05-10
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface MissionUserMapper extends BaseMapperPlus<MissionUserMapper, MissionUser, MissionUserVo> {

}
