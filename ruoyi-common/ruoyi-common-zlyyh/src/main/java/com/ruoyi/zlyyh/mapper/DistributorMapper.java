package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Distributor;
import com.ruoyi.zlyyh.domain.vo.DistributorVo;

/**
 * 分销商信息Mapper接口
 *
 * @author yzg
 * @date 2023-09-15
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface DistributorMapper extends BaseMapperPlus<DistributorMapper, Distributor, DistributorVo> {

}
