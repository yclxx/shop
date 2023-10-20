package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.vo.AreaVo;
import com.ruoyi.zlyyh.domain.vo.PlatformCityIndexVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.vo.AppPlatformVo;
import com.ruoyi.zlyyhmobile.service.IPlatformCityIndexService;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台控制器
 * 前端访问路由地址为:/zlyyh-mobile/platform
 *
 * @author ruoyi
 * @date 2023-03-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/platform")
public class PlatformController extends BaseController {

    private final IPlatformService platformService;
    private final IPlatformCityIndexService platformCityIndexService;
    private final YsfConfigService ysfConfigService;

    /**
     * 获取平台信息
     *
     * @return 平台信息
     */
    @GetMapping("/ignore/getInfo")
    public R<AppPlatformVo> getInfo() {
        PlatformVo platformVo = platformService.queryById(ZlyyhUtils.getPlatformId(), ZlyyhUtils.getPlatformType());
        AppPlatformVo result = BeanCopyUtils.copy(platformVo, AppPlatformVo.class);
        setPlatformCityIndex(result);
        return R.ok(result);
    }

    /**
     * 获取移动宠粉跳转链接
     *
     * @return 平台信息
     */
    @GetMapping("/ignore/getUrl")
    public R<String> getUrl() {
        String url = ysfConfigService.queryValueByKey(ZlyyhUtils.getPlatformId(), "tabUrl");
        return R.ok(url);
    }

    /**
     * 获取平台城市自定义首页
     *
     * @param result 平台信息
     */
    private void setPlatformCityIndex(AppPlatformVo result) {
        if (null != result && ObjectUtil.isNotEmpty(result.getPlatformCityList())) {
            Map<String, PlatformCityIndexVo> cityIndexVoMap = new HashMap<>(result.getPlatformCityList().size());
            for (AreaVo areaVo : result.getPlatformCityList()) {
                if (null != areaVo.getAdcode()) {
                    String str = areaVo.getAdcode().toString();
                    List<PlatformCityIndexVo> platformCityIndexVos = platformCityIndexService.queryByCityCode(str, result.getPlatformKey());
                    for (PlatformCityIndexVo platformCityIndexVo : platformCityIndexVos) {
                        if (null != platformCityIndexVo.getStartTime() && DateUtils.compare(platformCityIndexVo.getStartTime()) > 0) {
                            continue;
                        }
                        if (null != platformCityIndexVo.getEndTime() && DateUtils.compare(platformCityIndexVo.getEndTime()) < 0) {
                            continue;
                        }
                        cityIndexVoMap.put(str, platformCityIndexVo);
                        break;
                    }
                }
            }
            result.setCityIndex(cityIndexVoMap);
        }
    }
}
