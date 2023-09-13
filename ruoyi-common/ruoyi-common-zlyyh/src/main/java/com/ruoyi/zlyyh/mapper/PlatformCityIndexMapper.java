package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.PlatformCityIndex;
import com.ruoyi.zlyyh.domain.vo.PlatformCityIndexVo;

/**
 * 自定义首页Mapper接口
 *
 * @author yzg
 * @date 2023-08-07
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface PlatformCityIndexMapper extends BaseMapperPlus<PlatformCityIndexMapper, PlatformCityIndex, PlatformCityIndexVo> {

}
