package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.MissionVo;

public interface IMissionUserDrawService {

    /**
     * 赠送抽奖机会
     */
    String sendDrawCount(Long userId, Long productId,String exProductId);

    /**
     * 赠送奖励
     *
     * @param missionVo
     * @param userId
     * @return 奖励ID
     */
    Long sendRecord(MissionVo missionVo, Long userId);
}
