package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.HotNews;
import com.ruoyi.zlyyh.domain.vo.HotNewsVo;

/**
 * 热门搜索配置Mapper接口
 *
 * @author yzg
 * @date 2023-07-21
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface HotNewsMapper extends BaseMapperPlus<HotNewsMapper, HotNews, HotNewsVo> {

}
