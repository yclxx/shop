package com.ruoyi.zlyyhmobile.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IUnionpayMissionService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 银联任务配置控制器
 * 前端访问路由地址为:/zlyyh/unionpayMission
 *
 * @author yzg
 * @date 2024-02-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionpayMission/ignore")
public class UnionpayMissionController extends BaseController {

    private final IUnionpayMissionService iUnionpayMissionService;
    private final IUserService userService;

    /**
     * 查询银联任务配置列表
     */
    @SaCheckPermission("zlyyh:unionpayMission:list")
    @GetMapping("/list")
    public TableDataInfo<UnionpayMissionVo> list(UnionpayMissionBo bo, PageQuery pageQuery) {
        return iUnionpayMissionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出银联任务配置列表
     */
    @SaCheckPermission("zlyyh:unionpayMission:export")
    @Log(title = "银联任务配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionpayMissionBo bo, HttpServletResponse response) {
        List<UnionpayMissionVo> list = iUnionpayMissionService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联任务配置", UnionpayMissionVo.class, response);
    }

    /**
     * 获取银联任务配置详细信息
     *
     * @param upMissionId 主键
     */
    @SaCheckPermission("zlyyh:unionpayMission:query")
    @GetMapping("/{upMissionId}")
    public R<UnionpayMissionVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long upMissionId) {
        return R.ok(iUnionpayMissionService.queryById(upMissionId));
    }

    /**
     * 新增银联任务配置
     */
    @SaCheckPermission("zlyyh:unionpayMission:add")
    @Log(title = "银联任务配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionpayMissionBo bo) {
        return toAjax(iUnionpayMissionService.insertByBo(bo));
    }

    /**
     * 修改银联任务配置
     */
    @SaCheckPermission("zlyyh:unionpayMission:edit")
    @Log(title = "银联任务配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionpayMissionBo bo) {
        return toAjax(iUnionpayMissionService.updateByBo(bo));
    }

    /**
     * 删除银联任务配置
     *
     * @param upMissionIds 主键串
     */
    @SaCheckPermission("zlyyh:unionpayMission:remove")
    @Log(title = "银联任务配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{upMissionIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] upMissionIds) {
        return toAjax(iUnionpayMissionService.deleteWithValidByIds(Arrays.asList(upMissionIds), true));
    }

    /**
     * 用户报名
     */

    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/userSingUp")
    public R<Void> userSingUp(@RequestBody UnionpayMissionUserBo bo) {
        if (null == bo.getUpMissionGroupId()) {
            return R.fail("缺少任务组编号");
        }
        bo.setUserId(LoginHelper.getUserId());
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        iUnionpayMissionService.userSingUp(bo);
        return R.ok();
    }

    /**
     * 用户报名校验
     */
    @PostMapping("/signUpVerify")
    public R<Object> signUpVerify(@RequestBody UnionpayMissionUserBo bo) {
        if (null == bo.getUpMissionGroupId()) {
            return R.fail("缺少任务组编号");
        }
        bo.setUserId(LoginHelper.getUserId());
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return iUnionpayMissionService.signUpVerify(bo);
    }

    /**
     * 查询任务进度
     */
    @PostMapping("/getMissionProgress")
    public R<Void> getMissionProgress(@RequestBody UnionpayMissionUserBo bo) {
        if (null == bo.getUpMissionGroupId()) {
            return R.fail("缺少任务组编号");
        }
        bo.setUserId(LoginHelper.getUserId());
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        iUnionpayMissionService.getMissionProgress(bo);
        return R.ok();
    }

    /**
     * 查询银联任务奖励列表
     */
    //@PostMapping("/rewardList")
    //public R<List<UnionpayMissionUserLogVo>> rewardList(@RequestBody UnionpayMissionUserLogBo bo) {
    //    return R.ok(iUnionpayMissionService.rewardList(bo));
    //}

    @GetMapping("/rewardList")
    public TableDataInfo<UnionpayMissionUserLogVo> rewardList(UnionpayMissionUserLogBo bo, PageQuery pageQuery) {
        return iUnionpayMissionService.queryPageRewardList(bo, pageQuery);
    }

    /**
     * 查询银联任务进度列表
     */
    @PostMapping("/getProgressList")
    public R<List<UnionpayMissionProgressVo>> getProgressList(@RequestBody UnionpayMissionProgressBo bo) {
        return R.ok(iUnionpayMissionService.getProgressList(bo));
    }

    /**
     * 查询任务组背景
     */
    @PostMapping("/getUpMissionGroupBg")
    public R<List<UnionpayMissionGroupBgVo>> getUpMissionGroupBg(@RequestBody UnionpayMissionUserBo bo) {
        if (null == bo.getUpMissionGroupId()) {
            return R.fail("缺少任务组编号");
        }
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return R.ok(iUnionpayMissionService.getUpMissionGroupBg(bo));
    }
}
