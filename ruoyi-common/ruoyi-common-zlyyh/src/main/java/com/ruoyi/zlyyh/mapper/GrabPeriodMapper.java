package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.GrabPeriod;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodVo;

/**
 * 秒杀配置Mapper接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface GrabPeriodMapper extends BaseMapperPlus<GrabPeriodMapper, GrabPeriod, GrabPeriodVo> {

}
