package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.ICommercialTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户控制器
 * 前端访问路由地址为:/zlyyh-mobile/commercialTenant/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/commercialTenant/ignore")
public class CommercialTenantController {
    private final ICommercialTenantService commercialTenantService;

    /**
     * 获取商户列表
     *
     * @return 商户列表
     */
    @GetMapping("/getCommercialTenantList")
    public TableDataInfo<CommercialTenantVo> getCommercialTenantList(CommercialTenantBo bo, PageQuery pageQuery) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        // 如果是今日特惠
        if (StringUtils.isNotBlank(bo.getWeekDate()) || null != bo.getCategoryId()) {
            return commercialTenantService.queryPageListByDayProduct(bo, pageQuery);
        } else {
            return commercialTenantService.getShopCommercialTenantList(bo, pageQuery);
        }
    }

    /**
     * 获取商户详情
     *
     * @return 商户信息
     */
    @GetMapping("/getCommercialTenantInfo")
    public R<CommercialTenantVo> getCommercialTenantInfo(CommercialTenantBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return R.ok(commercialTenantService.queryById(bo));
    }

}
