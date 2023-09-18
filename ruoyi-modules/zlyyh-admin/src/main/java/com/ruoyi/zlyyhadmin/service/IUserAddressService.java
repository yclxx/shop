package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.UserAddress;
import com.ruoyi.zlyyh.domain.vo.UserAddressVo;
import com.ruoyi.zlyyh.domain.bo.UserAddressBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 用户地址Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface IUserAddressService {

    /**
     * 查询用户地址
     */
    UserAddressVo queryById(Long userAddressId);

    /**
     * 查询用户地址列表
     */
    TableDataInfo<UserAddressVo> queryPageList(UserAddressBo bo, PageQuery pageQuery);

    /**
     * 查询用户地址列表
     */
    List<UserAddressVo> queryList(UserAddressBo bo);

    /**
     * 修改用户地址
     */
    Boolean insertByBo(UserAddressBo bo);

    /**
     * 修改用户地址
     */
    Boolean updateByBo(UserAddressBo bo);

    /**
     * 校验并批量删除用户地址信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
