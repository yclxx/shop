package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.domain.bo.UserBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 用户信息Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IUserService {

    /**
     * 查询用户信息
     */
    UserVo queryById(Long userId);

    /**
     * 查询用户信息列表
     */
    TableDataInfo<UserVo> queryPageList(UserBo bo, PageQuery pageQuery);

    /**
     * 查询用户信息列表
     */
    List<UserVo> queryList(UserBo bo);

    /**
     * 修改用户信息
     */
    Boolean insertByBo(UserBo bo);

    /**
     * 修改用户信息
     */
    Boolean updateByBo(UserBo bo);

    /**
     * 校验并批量删除用户信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
