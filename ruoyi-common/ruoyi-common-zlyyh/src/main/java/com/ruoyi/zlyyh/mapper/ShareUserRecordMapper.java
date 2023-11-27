package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.ShareUserRecord;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;

import java.math.BigDecimal;

/**
 * 分销记录Mapper接口
 *
 * @author yzg
 * @date 2023-11-09
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface ShareUserRecordMapper extends BaseMapperPlus<ShareUserRecordMapper, ShareUserRecord, ShareUserRecordVo> {

    BigDecimal sumAwardAmount(Long userId);
}
