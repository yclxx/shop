package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.HotNewsBo;
import com.ruoyi.zlyyh.domain.vo.HotNewsVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IHotNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 热门搜索管理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/hotNews/ignore")
public class HotNewsController extends BaseController {
    private final IHotNewsService hotNewsService;

    /**
     * 查询广告管理列表
     */
    @GetMapping("/list")
    public R<List<HotNewsVo>> list(HotNewsBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        List<HotNewsVo> hotNewsVos = hotNewsService.queryList(bo);
        if (ObjectUtil.isEmpty(hotNewsVos)) {
            return R.ok(hotNewsVos);
        }
        bo.setShowCity(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        List<HotNewsVo> collect = hotNewsVos.stream().filter(item -> {
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
