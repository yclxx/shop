package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.MissionBo;
import com.ruoyi.zlyyh.domain.vo.MissionGroupVo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.enumd.DateType;
import com.ruoyi.zlyyh.utils.DrawRedisCacheUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IMissionGroupService;
import com.ruoyi.zlyyhmobile.service.IMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 任务配置控制器
 * 前端访问路由地址为:/zlyyh-mobile/mission
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mission/ignore")
public class MissionController extends BaseController {

    private final IMissionService iMissionService;
    private final IMissionGroupService missionGroupService;

    /**
     * 查询任务配置列表
     */
    @GetMapping("/list")
    public R<List<MissionVo>> list(MissionBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setShowCity(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        return R.ok(iMissionService.queryList(bo));
    }

    /**
     * 查询任务组信息
     */
    @GetMapping("/missionGroupInfo/{missionGroupId}")
    public R<MissionGroupVo> missionGroupInfo(@NotNull(message = "缺少任务组编号") @PathVariable Long missionGroupId) {
        return R.ok(missionGroupService.queryById(missionGroupId));
    }

    /**
     * 查询任务信息
     */
    @GetMapping("/missionInfo/{missionId}")
    public R<MissionVo> missionInfo(@NotNull(message = "缺少任务编号") @PathVariable Long missionId) {
        return R.ok(iMissionService.queryById(missionId));
    }

    /**
     * 查询任务发放数量
     */
    @GetMapping("/missionInfoSendQuota/{missionId}")
    public R<Double> missionInfoSendQuota(@NotNull(message = "缺少任务编号") @PathVariable Long missionId) {
        return R.ok(DrawRedisCacheUtils.getMissionQuotaCount(missionId, DateType.TOTAL));
    }

    /**
     * 查询任务组可兑换商品
     */
    @GetMapping("/missionProduct/{missionGroupId}")
    public R<List<ProductVo>> missionProduct(@NotNull(message = "缺少任务编号") @PathVariable Long missionGroupId) {
        return R.ok(missionGroupService.missionProduct(missionGroupId));
    }
}
