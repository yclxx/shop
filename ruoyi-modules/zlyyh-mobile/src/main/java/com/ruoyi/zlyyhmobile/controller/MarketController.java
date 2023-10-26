package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.MarketLog;
import com.ruoyi.zlyyh.domain.bo.MarketBo;
import com.ruoyi.zlyyh.domain.vo.MarketVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/market")
public class MarketController extends BaseController {
    private final IMarketService marketService;

    @GetMapping("/ignore/getMarket")
    public R<MarketVo> list(MarketBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        MarketVo marketVo = marketService.queryMarketVo(bo);
        return R.ok(marketVo);
    }

    @GetMapping("/marketLog")
    public R<MarketLog> marketLog(MarketBo bo) {
        Long userId = LoginHelper.getUserId();
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        MarketLog marketLog = marketService.queryMarketLogVo(bo,userId);
        return R.ok(marketLog);
    }

    @PostMapping("/insertUserMarket")
    public R<MarketLog> insertUserMarket(@RequestBody MarketBo bo) {
        Long userId = LoginHelper.getUserId();
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        MarketLog marketLog = marketService.insertUserMarket(bo, userId);
        return R.ok(marketLog);
    }
}
