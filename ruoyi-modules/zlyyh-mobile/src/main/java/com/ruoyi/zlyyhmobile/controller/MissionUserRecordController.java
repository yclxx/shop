package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.log.enums.OperatorType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.MissionUserRecord;
import com.ruoyi.zlyyh.domain.bo.MissionGroupProductBo;
import com.ruoyi.zlyyh.domain.vo.MissionUserRecordVo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.utils.CloudRechargeEntity;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.domain.vo.UserProductCount;
import com.ruoyi.zlyyhmobile.service.AsyncService;
import com.ruoyi.zlyyhmobile.service.IMissionService;
import com.ruoyi.zlyyhmobile.service.IMissionUserRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
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
    private final IMissionService missionService;

    /**
     * 获取抽奖记录 只显示最近50条
     *
     * @param missionGroupId 任务组ID
     */
    @GetMapping("/ignore/getRecordList/{missionGroupId}")
    public R<List<MissionUserRecordVo>> getRecordList(@NotNull(message = "缺少任务组编号") @PathVariable Long missionGroupId) {
        return R.ok(iMissionUserRecordService.getRecordList(missionGroupId));
    }

    /**
     * 获取抽奖记录 只显示最近50条
     *
     * @param missionGroupId 任务组ID
     */
    @GetMapping("/ignore/getRecordStringList/{missionGroupId}")
    public R<List<String>> getRecordStringList(@NotNull(message = "缺少任务组编号") @PathVariable Long missionGroupId) {
        return R.ok(iMissionUserRecordService.getRecordStringList(missionGroupId));
    }

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
     * 订单回调通知
     */
    @RequestMapping("/ignore/orderCallback")
    public R<Void> orderCallback(@RequestBody CloudRechargeEntity huiguyunEntity) {
        iMissionUserRecordService.cloudRechargeCallback(huiguyunEntity);
        return R.ok();
    }

    /**
     * 获取用户剩余抽奖机会
     *
     * @param missionGroupId 任务组ID
     */
    @GetMapping("/getUserDrawCount/{missionGroupId}")
    public R<Long> getUserDrawCount(@NotNull(message = "缺少任务组编号") @PathVariable Long missionGroupId) {
        return R.ok(iMissionUserRecordService.getUserDrawCount(missionGroupId, LoginHelper.getUserId()));
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
        return R.ok(iMissionUserRecordService.getUserProductPayCount(missionGroupId, missionGroupId, LoginHelper.getUserId(), ServletUtils.getHeader(ZlyyhConstants.CITY_CODE)));
    }

    /**
     * 购买产品
     */
    @Log(title = "购买产品", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/payMissionGroupProduct/{missionId}")
    public R<CreateOrderResult> payMissionGroupProduct(@NotNull(message = "缺少任务编号") @PathVariable Long missionId) {
        Long platformId = ZlyyhUtils.getPlatformId();
        String adCode = ZlyyhUtils.getAdCode();
        String cityName = ZlyyhUtils.getCityName();
        String platformChannel = ZlyyhUtils.getPlatformChannel();
        //判断位置是否在定位城市
        MissionVo missionVo = missionService.queryById(missionId);
        if (null == missionVo) {
            throw new ServiceException("任务不存在");
        }
        ZlyyhUtils.checkCity(missionVo.getShowCity());
        return R.ok(iMissionUserRecordService.payMissionGroupProduct(missionId, LoginHelper.getUserId(),platformId,platformChannel,cityName,adCode));
    }
}
