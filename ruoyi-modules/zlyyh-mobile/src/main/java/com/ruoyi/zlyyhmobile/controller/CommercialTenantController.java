package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.validate.AppEditGroup;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyh.domain.vo.OcrBizLicenseVo;
import com.ruoyi.zlyyh.domain.vo.OcrBizLicenseYsfVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.BaiduUtils;
import com.ruoyi.zlyyh.utils.WxUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.OcrBizLicenseBo;
import com.ruoyi.zlyyhmobile.service.ICommercialTenantService;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import com.ruoyi.zlyyhmobile.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 25487
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/commercialTenant")
public class CommercialTenantController {
    private final ICommercialTenantService commercialTenantService;
    private final IShopService shopService;
    private final IPlatformService platformService;
    private final YsfConfigService ysfConfigService;

    /**
     * 获取商户列表
     *
     * @return 商户列表
     */
    @GetMapping("/ignore/getCommercialTenantList")
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
     */
    @GetMapping("/ignore/getCommercialTenantInfo")
    public R<CommercialTenantVo> getCommercialTenantInfo(CommercialTenantBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return R.ok(commercialTenantService.queryById(bo));
    }

    /**
     * 查询商户分页表(商户端)
     */
    @GetMapping("/getPage")
    public TableDataInfo<CommercialTenantVo> getPage(CommercialTenantBo bo, PageQuery pageQuery) {
        bo.setVerifierId(LoginHelper.getUserId());
        TableDataInfo<CommercialTenantVo> page = commercialTenantService.getPage(bo, pageQuery);
        for (CommercialTenantVo row : page.getRows()) {
            row.setShopCount(shopService.selectCountByCommercialTenantId(row.getCommercialTenantId()));
            row.setUnExamineShopCount(shopService.selectUnExamineCountByCommercialTenantId(row.getCommercialTenantId()));
        }
        return page;
    }

    /**
     * 查询商户详情(商户端)
     */
    @GetMapping("/getDetails/{commercialTenantId}")
    public R<CommercialTenantVo> getDetails(@PathVariable("commercialTenantId") Long commercialTenantId) {
        return R.ok(commercialTenantService.getDetails(commercialTenantId));
    }

    /**
     * 查询商户详情(商户端)
     */
    @GetMapping("/getCommercialTenant")
    public R<List<CommercialTenantVo>> getCommercialTenant(CommercialTenantBo bo) {
        bo.setVerifierId(LoginHelper.getUserId());
        return R.ok(commercialTenantService.getCommercialTenant(bo));
    }

    /**
     * 商户端修改商户(商户端)
     */
    @PostMapping("/updateCommercialTenant")
    public R<Void> updateCommercialTenant(@Validated(AppEditGroup.class) @RequestBody CommercialTenantBo bo) {
        bo.setVerifierId(LoginHelper.getUserId());
        commercialTenantService.updateCommercialTenant(bo);
        return R.ok();
    }

    /**
     * 营业执照识别
     */
    @PostMapping("/ocrBizLicense")
    public R<OcrBizLicenseVo> ocrBizLicense(@Validated(AppEditGroup.class) @RequestBody OcrBizLicenseBo bo) {
        Long platformId = ZlyyhUtils.getPlatformId();
        PlatformVo platformVo = platformService.queryById(platformId, PlatformEnumd.MP_WX.getChannel());
        String accessToken = WxUtils.getAccessToken(platformVo.getAppId(), platformVo.getSecret());
        return R.ok(WxUtils.ocrBizLicense(bo.getImgUrl(), accessToken));
    }

    /**
     * 云闪付商户号识别
     */
    @PostMapping("/ocrUp")
    public R<OcrBizLicenseYsfVo> ocrUp(@Validated(AppEditGroup.class) @RequestBody OcrBizLicenseBo bo) {
        Long platformId = ZlyyhUtils.getPlatformId();
        String ocrApiKey = ysfConfigService.queryValueByKey(platformId, "baidu.ocrApiKey");
        String ocrSecretKey = ysfConfigService.queryValueByKey(platformId, "baidu.ocrSecretKey");
        return R.ok(commercialTenantService.ocrUp(bo.getImgUrl(),BaiduUtils.getAccessToken(ocrApiKey,ocrSecretKey)));
    }
}
