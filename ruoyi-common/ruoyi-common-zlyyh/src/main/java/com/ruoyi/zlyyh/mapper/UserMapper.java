package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息Mapper接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface UserMapper extends BaseMapperPlus<UserMapper, User, UserVo> {

    /**
     * 查询单个对象
     *
     * @param wrapper 查询条件
     * @param user    手机号
     * @return 对象信息
     */
    User selectOneIncludeMobile(@Param(Constants.WRAPPER) Wrapper<User> wrapper, @Param("user") User user);

    /**
     * 分页查询
     *
     * @param page    分页参数
     * @param wrapper 查询条件
     * @param user    带手机号查询
     * @return 结果
     */
    Page<User> selectPageIncludeMobile(@Param("page") IPage<User> page, @Param(Constants.WRAPPER) Wrapper<User> wrapper, @Param("user") User user);

    /**
     * 查询集合
     *
     * @param wrapper 查询条件
     * @param user    带手机号查询
     * @return 结果
     */
    List<User> selectListIncludeMobile(@Param(Constants.WRAPPER) Wrapper<User> wrapper, @Param("user") User user);
}
