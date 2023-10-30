package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.MemberVipBalanceVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyhmobile.domain.bo.UserRecordLog;

/**
 * 用户信息Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IUserService {

    /**
     * 查询单个用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVo queryById(Long userId, String channel);

    /**
     * 查询用户是否62会员
     *
     * @param isCache 是否优先查询缓存，true是，false否
     * @return 结果
     */
    MemberVipBalanceVo getUser62VipInfo(boolean isCache, Long userId);

    void userFollow(String code);

    /**
     * 权益会员到期
     *
     * @param userId 用户ID
     */
    void vipExpiry(Long userId);

    /**
     * 根据平台id和手机号获取openId
     *
     * @param platformKey 平台id
     * @param mobile      手机号
     * @return
     */
    String getOpenIdByMobile(Long platformKey, String mobile, String channel);

    /**
     * 根据平台id和手机号获取用户信息
     *
     * @param platformKey 平台id
     * @param mobile      手机号
     * @return 用户信息
     */
    Long getUserIdByMobile(Long platformKey, String mobile);

    /**
     * 缓存用户进入首页数据
     *
     * @param recordLog 缓存参数
     */
    void userLog(UserRecordLog recordLog);

    /**
     * 根据用户手机号创建用户
     *
     * @param platformKey 平台
     * @param mobile      用户手机号
     */
    boolean insertUserByMobile(Long platformKey, String mobile);
}
