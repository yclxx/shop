package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.vo.PlatformCityIndexVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IPlatformCityIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 自定义首页控制器
 * 前端访问路由地址为:/zlyyh-mobile/platformCityIndex
 *
 * @author yzg
 * @date 2023-08-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/platformCityIndex/ignore")
public class PlatformCityIndexController extends BaseController {

    private final IPlatformCityIndexService iPlatformCityIndexService;

    /**
     * 查询自定义首页列表
     */
    @GetMapping("/infoByCity/{cityCode}")
    public R<PlatformCityIndexVo> info(@PathVariable("cityCode") String cityCode) {
        List<PlatformCityIndexVo> platformCityIndexVos = iPlatformCityIndexService.queryByCityCode(cityCode, ZlyyhUtils.getPlatformId());
        for (PlatformCityIndexVo platformCityIndexVo : platformCityIndexVos) {
            if (null != platformCityIndexVo.getStartTime() && DateUtils.compare(platformCityIndexVo.getStartTime()) > 0) {
                continue;
            }
            if (null != platformCityIndexVo.getEndTime() && DateUtils.compare(platformCityIndexVo.getEndTime()) < 0) {
                continue;
            }
            return R.ok(platformCityIndexVo);
        }
        return R.ok();
    }
}
