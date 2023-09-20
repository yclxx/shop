package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Code;
import com.ruoyi.zlyyh.domain.vo.CodeVo;

/**
 * 商品券码Mapper接口
 *
 * @author yzg
 * @date 2023-09-20
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface CodeMapper extends BaseMapperPlus<CodeMapper, Code, CodeVo> {

}
