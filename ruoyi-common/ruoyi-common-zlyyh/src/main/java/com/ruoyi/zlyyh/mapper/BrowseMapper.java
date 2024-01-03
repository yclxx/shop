package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Browse;
import com.ruoyi.zlyyh.domain.vo.BrowseVo;

/**
 * 浏览任务Mapper接口
 *
 * @author yzg
 * @date 2023-12-14
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface BrowseMapper extends BaseMapperPlus<BrowseMapper, Browse, BrowseVo> {

}
