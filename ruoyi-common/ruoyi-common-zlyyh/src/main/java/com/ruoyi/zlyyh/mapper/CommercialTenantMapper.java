package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.CommercialTenant;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;

/**
 * 商户Mapper接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface CommercialTenantMapper extends BaseMapperPlus<CommercialTenantMapper, CommercialTenant, CommercialTenantVo> {

}
