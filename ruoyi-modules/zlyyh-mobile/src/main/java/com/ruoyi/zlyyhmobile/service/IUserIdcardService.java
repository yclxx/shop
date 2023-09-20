package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.UserIdcard;
import com.ruoyi.zlyyh.domain.vo.UserIdcardVo;
import com.ruoyi.zlyyh.domain.bo.UserIdcardBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 观影用户信息Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface IUserIdcardService {

    /**
     * 查询观影用户信息
     */
    UserIdcardVo queryById(Long userIdcardId);

    /**
     * 查询观影用户信息列表
     */
    TableDataInfo<UserIdcardVo> queryPageList(UserIdcardBo bo);

    /**
     * 查询观影用户信息列表
     */
    List<UserIdcardVo> queryList(UserIdcardBo bo);

    /**
     * 修改观影用户信息
     */
    Boolean insertByBo(UserIdcardBo bo);

    /**
     * 修改观影用户信息
     */
    Boolean updateByBo(UserIdcardBo bo);

    /**
     * 校验并批量删除观影用户信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    boolean removeByMap(Map<String, Object> map);

}
