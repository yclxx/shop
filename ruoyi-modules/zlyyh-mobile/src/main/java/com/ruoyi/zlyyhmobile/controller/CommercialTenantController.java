package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.ICommercialTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/commercialTenant")
public class CommercialTenantController {
    private final ICommercialTenantService commercialTenantService;

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
     * 民生银行获取商户详情（产品列表拆分处理）
     *
     * @return 商户信息
     */
    @GetMapping("/ignore/getMsCommercialTenantInfo")
    public R<CommercialTenantVo> getMsCommercialTenantInfo(CommercialTenantBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return R.ok(commercialTenantService.queryMsById(bo));
    }

    /**
     * 查询商户分页表(商户端)
     */
    @GetMapping("/getPage")
    public TableDataInfo<CommercialTenantVo> getPage(CommercialTenantBo bo, PageQuery pageQuery) {
        bo.setVerifierId(LoginHelper.getUserId());
        return commercialTenantService.getPage(bo, pageQuery);
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
    public R updateCommercialTenant(@RequestBody CommercialTenantBo bo) {
        bo.setVerifierId(LoginHelper.getUserId());
        return R.ok(commercialTenantService.updateCommercialTenant(bo));
    }
}
