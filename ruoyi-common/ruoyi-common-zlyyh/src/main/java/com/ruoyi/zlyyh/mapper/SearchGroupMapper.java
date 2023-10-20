package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.SearchGroup;
import com.ruoyi.zlyyh.domain.vo.SearchGroupVo;

/**
 * 搜索彩蛋配置Mapper接口
 *
 * @author yzg
 * @date 2023-07-24
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface SearchGroupMapper extends BaseMapperPlus<SearchGroupMapper, SearchGroup, SearchGroupVo> {}
