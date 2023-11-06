package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.InviteUserLogBo;
import com.ruoyi.zlyyh.domain.vo.InviteUserLogMobileOrderVo;
import com.ruoyi.zlyyh.domain.vo.InviteUserLogVo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;

/**
 * 邀请记录Service接口
 *
 * @author yzg
 * @date 2023-08-08
 */
public interface IInviteUserLogService {

    void inviteBind(InviteUserLogBo bo);

    /**
     * 查询邀请记录
     */
    InviteUserLogVo queryById(Long id);

    /**
     * 查询邀请记录列表
     */
    TableDataInfo<InviteUserLogVo> queryPageList(InviteUserLogBo bo, PageQuery pageQuery);

    /**
     * 修改邀请记录
     */
    void insertByBo(InviteUserLogBo bo,Long platformId,Long userId,String channel,String cityName,String adCode);

    void check(MissionVo missionVo);
    /**
     * 查询用户今日已获奖励次数
     *
     * @param userId    用户ID
     * @param missionId 任务ID
     * @return 今日已获奖励次数
     */
    Long getUserInviteLogCount(Long userId, Long missionId);

    TableDataInfo<String> getUserInviteLogMobile(Long userId,Long missionId,PageQuery pageQuery);

    TableDataInfo<InviteUserLogMobileOrderVo> getInviteLong(Long userId, Long missionId, PageQuery pageQuery);
}
