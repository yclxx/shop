package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.MarketLog;
import com.ruoyi.zlyyh.domain.bo.MarketBo;
import com.ruoyi.zlyyh.domain.vo.MarketVo;

/**
 * 新用户营销功能
 */
public interface IMarketService {
    /**
     * 查询新用户营销信息
     */
    MarketVo queryMarketVo(MarketBo bo);

    /**
     * 查询奖励记录日志问题。
     */
    MarketLog queryMarketLogVo(MarketBo bo, Long userId);

    /**
     * 领取奖励
     */
    MarketLog insertUserMarket(MarketBo bo, Long userId);
}
