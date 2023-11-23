package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.PromotionTaskBo;
import com.ruoyi.zlyyh.domain.vo.PromotionTaskVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IPromotionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推广任务控制器
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotionTask")
public class PromotionTaskController extends BaseController {

    private final IPromotionTaskService promotionTaskService;

    /**
     * 查询推广任务列表
     */
    @GetMapping("/ignore/list")
    public R<List<PromotionTaskVo>> list(PromotionTaskBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setShowCity(ZlyyhUtils.getCityCode());
        return R.ok(promotionTaskService.list(bo));
    }

    /**
     * 查询任务详情
     */
    @GetMapping("/getPromotionTask/{taskId}")
    public R<PromotionTaskVo> getPromotionTask(@PathVariable("taskId") Long taskId) {
        return R.ok(promotionTaskService.getPromotionTask(taskId));
    }

    /**
     * 推广记录统计查询
     */
    @GetMapping("/today")
    public R<Map<String, Object>> today() {
        Map<String, Object> map = new HashMap<>();
        map.put("todayNumber", 3);
        map.put("number", 10);
        return R.ok(map);
    }

    /**
     * 商户录入
     */
    @PostMapping("/merchantApprovalInsert")
    public R<Boolean> merchantApprovalInsert(@RequestBody MerchantApprovalBo bo) {
        return R.ok(promotionTaskService.merchantApprovalInsert(bo));
    }
}
