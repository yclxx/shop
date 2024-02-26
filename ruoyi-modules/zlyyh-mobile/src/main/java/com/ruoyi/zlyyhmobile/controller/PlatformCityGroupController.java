package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.bo.PlatformCityGroupBo;
import com.ruoyi.zlyyh.domain.vo.PlatformCityGroupEntity;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IPlatformCityGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 企业微信二维码管理控制器
 * 前端访问路由地址为:/zlyyh-mobile/platformCityGroup/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/platformCityGroup/ignore")
public class PlatformCityGroupController extends BaseController {

    private final IPlatformCityGroupService platformCityGroupService;

    /**
     * 查询企业微信二维码管理列表
     */
    @GetMapping("/list")
    public R<List<PlatformCityGroupEntity>> list(PlatformCityGroupBo bo){
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setStatus("0");
        return R.ok(platformCityGroupService.queryList(bo));
    }
}
