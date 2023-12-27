package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.BrowseBo;
import com.ruoyi.zlyyh.domain.vo.BrowseVo;

import java.util.List;

/**
 * 浏览任务Service接口
 *
 * @author yzg
 * @date 2023-12-14
 */
public interface IBrowseService {

    /**
     * 查询浏览任务列表
     */
    List<BrowseVo> queryList(BrowseBo bo);

    BrowseVo queryById(Long browseId);

    void sendReward(Long userId, BrowseVo browseVo, String adCode, String cityName, String channel);

    /**
     * 查询用户今日是否可参与浏览任务
     *
     * @param userId   用户ID
     * @param browseVo 浏览任务
     * @return true 可参与，false 不可参与
     */
    boolean queryUserWhetherToParticipateToday(Long userId, BrowseVo browseVo);
}
