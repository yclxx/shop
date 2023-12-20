package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.BrowseBo;
import com.ruoyi.zlyyh.domain.vo.BrowseVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IBrowseService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 浏览任务控制器
 * 前端访问路由地址为:/zlyyh-mobile/browse
 *
 * @author yzg
 * @date 2023-12-14
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/browse")
public class BrowseController extends BaseController {

    private final IBrowseService iBrowseService;
    private final IUserService userService;
    private final YsfConfigService ysfConfigService;

    /**
     * 获取浏览任务配置信息
     *
     * @return 平台信息
     */
    @GetMapping("/ignore/getBrowseSetting")
    public R<String> getBrowseSetting() {
        String url = ysfConfigService.queryValueByKey(ZlyyhUtils.getPlatformId(), "browseTitle");
        return R.ok("操作成功", url);
    }

    /**
     * 查询浏览任务列表
     */
    @GetMapping("/ignore/list")
    public R<List<BrowseVo>> list(BrowseBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        bo.setShowCity(ZlyyhUtils.getUserCheckCityCode());
        List<BrowseVo> browseVos = iBrowseService.queryList(bo);
        if (ObjectUtil.isEmpty(browseVos)) {
            return R.ok(browseVos);
        }
        List<BrowseVo> collect = browseVos.stream().filter(item -> {
            // 周几筛选
            if ("1".equals(item.getAssignDate()) && StringUtils.isNotBlank(item.getWeekDate()) && !item.getWeekDate().contains(bo.getWeekDate())) {
                return false;
            }
            // 城市筛选
            return StringUtils.isBlank(bo.getShowCity()) || item.getShowCity().contains(bo.getShowCity()) || "ALL".equalsIgnoreCase(item.getShowCity());
        }).collect(Collectors.toList());
        Long userId = null;
        try {
            userId = LoginHelper.getUserId();
        } catch (Exception ignored) {
        }
        if (null != userId) {
            for (BrowseVo browseVo : collect) {
                if (!iBrowseService.queryUserWhetherToParticipateToday(userId, browseVo)) {
                    browseVo.setBrowseCount(1);
                }
            }
        }
        return R.ok(collect);
    }

    /**
     * 完成浏览任务
     */
    @RepeatSubmit
    @PostMapping("/perform/{browseId}")
    public R<Void> perform(@PathVariable("browseId") Long browseId) {
        // 获取平台ID
        Long platformId = ZlyyhUtils.getPlatformId();
        // 获取用户信息
        Long userId = LoginHelper.getUserId();
        UserVo userVo = userService.queryById(userId, ZlyyhUtils.getPlatformChannel());
        if (null == userVo || !userVo.getPlatformKey().equals(platformId)) {
            throw new ServiceException("用户不存在");
        }
        // 查询任务信息
        BrowseVo browseVo = iBrowseService.queryById(browseId);
        if (null == browseVo || !"0".equals(browseVo.getStatus())) {
            log.info("用户：{}，任务不存在或已停用{}", userId, browseId);
            return R.ok();
        }
        if (!browseVo.getPlatformKey().equals(platformId)) {
            log.info("用户：{}，任务平台不匹配{}，平台：{}", userId, browseId, platformId);
            return R.ok();
        }
        if (null != browseVo.getStartTime() && !DateUtils.validTime(browseVo.getStartTime(), 1)) {
            log.info("用户：{}，任务未开始{}", userId, browseId);
            return R.ok();
        }
        if (null != browseVo.getEndTime() && DateUtils.validTime(browseVo.getEndTime(), 1)) {
            log.info("用户：{}，任务已结束{}", userId, browseId);
            return R.ok();
        }
        // 校验城市
        try {
            ZlyyhUtils.checkCity(browseVo.getShowCity());
        } catch (Exception e) {
            log.info("用户：{}，不在活动城市，任务ID：{}，限制城市{}，用户所在城市：{},e={}", userId, browseId, browseVo.getShowCity(), ZlyyhUtils.getCityCode(), e.getMessage());
            return R.ok();
        }
        // 赠送奖励
        iBrowseService.sendReward(userVo.getUserId(), browseVo, ZlyyhUtils.getAdCode(), ZlyyhUtils.getCityName(), ZlyyhUtils.getPlatformChannel());
        return R.ok();
    }
}
