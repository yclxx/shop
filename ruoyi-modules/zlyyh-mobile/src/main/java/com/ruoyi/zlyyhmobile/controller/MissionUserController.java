package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.MissionUserBo;
import com.ruoyi.zlyyh.domain.vo.MissionUserVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IMissionUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 任务用户控制器
 * 前端访问路由地址为:/zlyyh-mobile/missionUser
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/missionUser")
public class MissionUserController extends BaseController {

    private final IMissionUserService iMissionUserService;

    /**
     * 获取任务用户详细信息
     *
     * @param missionGroupId 任务组ID
     */
    @GetMapping("/{missionGroupId}")
    public R<MissionUserVo> getInfo(@NotNull(message = "缺少任务组编号") @PathVariable Long missionGroupId) {
        return R.ok(iMissionUserService.queryByUserIdAndGroupId(missionGroupId, LoginHelper.getUserId(), ZlyyhUtils.getPlatformId()));
    }

    /**
     * 新增任务用户
     */
    @Log(title = "任务用户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@RequestBody MissionUserBo bo) {
        if (null == bo.getMissionGroupId()) {
            return R.fail("缺少任务组编号");
        }
        bo.setUserId(LoginHelper.getUserId());
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return toAjax(iMissionUserService.insertByBo(bo));
    }
}
