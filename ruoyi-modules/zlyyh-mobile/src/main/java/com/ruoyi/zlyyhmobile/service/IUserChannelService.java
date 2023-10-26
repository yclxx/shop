package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.UserChannelBo;
import com.ruoyi.zlyyh.domain.vo.UserChannelVo;

import java.util.Collection;
import java.util.List;

/**
 * 用户渠道信息Service接口
 *
 * @author yzg
 * @date 2023-10-13
 */
public interface IUserChannelService {

    /**
     * 查询用户渠道信息
     */
    UserChannelVo queryById(Long id);

    /**
     * 查询用户渠道信息列表
     */
    TableDataInfo<UserChannelVo> queryPageList(UserChannelBo bo, PageQuery pageQuery);

    /**
     * 查询用户渠道信息列表
     */
    List<UserChannelVo> queryList(UserChannelBo bo);

    /**
     * 修改用户渠道信息
     */
    Boolean insertByBo(UserChannelBo bo);

    /**
     * 修改用户渠道信息
     */
    Boolean updateByBo(UserChannelBo bo);

    /**
     * 校验并批量删除用户渠道信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    UserChannelVo queryByOpenId(String channel, String openId, Long platformKey);

    UserChannelVo queryByUserId(String channel, Long userId, Long platformKey);
}
