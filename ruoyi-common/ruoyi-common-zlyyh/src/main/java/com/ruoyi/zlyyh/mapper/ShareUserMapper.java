package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.ShareUser;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分销员Mapper接口
 *
 * @author yzg
 * @date 2023-11-09
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface ShareUserMapper extends BaseMapperPlus<ShareUserMapper, ShareUser, ShareUserVo> {

    /**
     * 查询集合
     *
     * @param wrapper 查询条件
     * @param user    带手机号查询
     * @return 结果
     */
    List<User> selectListIncludeMobile(@Param(Constants.WRAPPER) Wrapper<ShareUser> wrapper, @Param("user") ShareUser user);
}
