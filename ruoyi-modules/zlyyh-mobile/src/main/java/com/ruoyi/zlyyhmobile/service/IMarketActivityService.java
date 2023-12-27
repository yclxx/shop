package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.MarketActivityBo;
import com.ruoyi.zlyyh.domain.bo.VerifierActivityBo;
import com.ruoyi.zlyyh.domain.vo.MarketActivityVo;

import java.util.List;

public interface IMarketActivityService {
    List<MarketActivityVo> getMarketActivity(MarketActivityBo bo);

    Boolean addVerifierActivity(VerifierActivityBo bo);
}
