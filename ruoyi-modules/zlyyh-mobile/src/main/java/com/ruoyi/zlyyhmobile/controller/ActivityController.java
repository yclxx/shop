package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.MarketActivityBo;
import com.ruoyi.zlyyh.domain.bo.VerifierActivityBo;
import com.ruoyi.zlyyh.domain.vo.MarketActivityVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IMarketActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/activity")
public class ActivityController extends BaseController {
    private final IMarketActivityService marketActivityService;

    /**
     * 查询营销活动列表
     */
    @GetMapping("/getMarketActivity")
    public R<List<MarketActivityVo>> getMarketActivity(MarketActivityBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return R.ok(marketActivityService.getMarketActivity(bo));
    }

    @PostMapping("/addVerifierActivity")
    public R addVerifierActivity(@RequestBody VerifierActivityBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setVerifierId(LoginHelper.getUserId());
        return R.ok(marketActivityService.addVerifierActivity(bo));
    }

}
