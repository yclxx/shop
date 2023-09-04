package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.api.model.XcxLoginUser;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.BannerBo;
import com.ruoyi.zlyyh.domain.vo.BannerVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 广告管理控制器
 * 前端访问路由地址为:/zlyyh-mobile/banner/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/banner/ignore")
public class BannerController extends BaseController {

    private final IBannerService iBannerService;

    /**
     * 查询广告管理列表
     */
    @GetMapping("/list")
    public R<List<BannerVo>> list(BannerBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        List<BannerVo> bannerVos = iBannerService.queryList(bo);
        if (ObjectUtil.isEmpty(bannerVos)) {
            return R.ok(bannerVos);
        }
        bo.setShowDimension("0");
        bo.setShowCity(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        // 新老用户筛选
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (loginUser instanceof XcxLoginUser) {
                XcxLoginUser xcxLoginUser = (XcxLoginUser) loginUser;
                long datePoorDay = DateUtils.getDatePoorDay(new Date(), xcxLoginUser.getCreateTime());
                if (datePoorDay < 15) {
                    // 新用户
                    bo.setShowDimension("1");
                } else {
                    // 老用户
                    bo.setShowDimension("2");
                }
            }
        } catch (Exception ignored) {
        }
        List<BannerVo> collect = bannerVos.stream().filter(item -> {
            // 展示纬度筛选
            if (!item.getShowDimension().equals(bo.getShowDimension()) && !"0".equals(item.getShowDimension())) {
                return false;
            }
            // 周几筛选
            if ("1".equals(item.getAssignDate()) && StringUtils.isNotBlank(item.getWeekDate()) && !item.getWeekDate().contains(bo.getWeekDate())) {
                return false;
            }
            // 城市筛选
            return StringUtils.isBlank(bo.getShowCity()) || item.getShowCity().contains(bo.getShowCity()) || "ALL".equalsIgnoreCase(item.getShowCity());
        }).collect(Collectors.toList());
        return R.ok(collect);
    }
}
