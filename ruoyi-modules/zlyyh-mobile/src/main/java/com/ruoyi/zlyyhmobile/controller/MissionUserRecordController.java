package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.log.enums.OperatorType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.MissionUserRecord;
import com.ruoyi.zlyyh.domain.bo.MissionGroupProductBo;
import com.ruoyi.zlyyh.domain.vo.MissionUserRecordVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.domain.vo.UserProductCount;
import com.ruoyi.zlyyhmobile.service.AsyncService;
import com.ruoyi.zlyyhmobile.service.IMissionUserRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 活动记录控制器
 * 前端访问路由地址为:/zlyyh/missionUserRecord
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/missionUserRecord")
public class MissionUserRecordController extends BaseController {

    private final IMissionUserRecordService iMissionUserRecordService;
    private final AsyncService asyncService;

    /**
     * 获取用户抽奖记录
     *
     * @param missionGroupId 任务组ID
     */
    @GetMapping("/getUserRecordList/{missionGroupId}")
    public TableDataInfo<MissionUserRecordVo> getUserRecordPageList(@NotNull(message = "缺少任务组编号") @PathVariable Long missionGroupId, PageQuery pageQuery) {
        return iMissionUserRecordService.getUserRecordPageList(missionGroupId, pageQuery);
    }

    /**
     * 抽奖
     *
     * @param missionGroupId 任务组ID
     */
    @Log(title = "活动记录", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/draw/{missionGroupId}")
    public R<Long> getDrawId(@NotNull(message = "缺少任务组编号") @PathVariable Long missionGroupId) {
        MissionUserRecord missionUserRecord = iMissionUserRecordService.getDraw(missionGroupId, LoginHelper.getUserId(), ZlyyhUtils.getPlatformId(), ZlyyhUtils.getPlatformChannel());
        // 异步进行发放奖品操作
        asyncService.sendDraw(missionUserRecord.getMissionUserRecordId());
        return R.ok(missionUserRecord.getDrawId());
    }

    /**
     * 获取用户剩余抽奖机会
     *
     * @param missionGroupId 任务组ID
     */
    @GetMapping("/getUserDrawCount/{missionGroupId}")
    public R<Long> getUserDrawCount(@NotNull(message = "缺少任务组编号") @PathVariable Long missionGroupId) {
        return R.ok(iMissionUserRecordService.getUserDrawCount(missionGroupId, LoginHelper.getUserId(), ZlyyhUtils.getPlatformId()));
    }

    /**
     * 获取用户剩余积点
     *
     * @param missionId 任务组ID
     */
    @GetMapping("/getUserQuota/{missionId}")
    public R<Map<String, Double>> getUserQuota(@NotNull(message = "缺少任务编号") @PathVariable Long missionId) {
        return R.ok(iMissionUserRecordService.getUserQuota(missionId));
    }

    /**
     * 兑换红包
     */
    @Log(title = "兑换红包", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/convertProduct")
    public R<CreateOrderResult> convertProduct(@Validated(AddGroup.class) @RequestBody MissionGroupProductBo missionGroupProductBo) {
        return R.ok(iMissionUserRecordService.convertProduct(missionGroupProductBo));
    }

    /**
     * 获取用户购买产品次数
     *
     * @param missionGroupId 任务组ID
     */
    @GetMapping("/getUserProductPayCount/{missionGroupId}")
    public R<UserProductCount> getUserProductPayCount(@NotNull(message = "缺少任务编号") @PathVariable Long missionGroupId) {
        return R.ok(iMissionUserRecordService.getUserProductPayCount(missionGroupId, LoginHelper.getUserId()));
    }

    /**
     * 购买产品
     */
    @Log(title = "购买产品", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/payMissionGroupProduct/{missionId}")
    public R<CreateOrderResult> payMissionGroupProduct(@NotNull(message = "缺少任务编号") @PathVariable Long missionId) {
        return R.ok(iMissionUserRecordService.payMissionGroupProduct(missionId, LoginHelper.getUserId()));
    }
}
