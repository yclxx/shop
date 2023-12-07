package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;

/**
 * 商户Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface ICommercialTenantService {

    /**
     * 查询商户
     */
    CommercialTenantVo queryById(CommercialTenantBo bo);

    /**
     * 查询商户列表
     */
    TableDataInfo<CommercialTenantVo> queryPageListByDayProduct(CommercialTenantBo bo, PageQuery pageQuery);

    /**
     * 查询门店商户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页条件
     * @return 结果
     */
    TableDataInfo<CommercialTenantVo> getShopCommercialTenantList(CommercialTenantBo bo, PageQuery pageQuery);

    /**
     * 查询门店商户分页（商户端）
     */
    TableDataInfo<CommercialTenantVo> getPage(CommercialTenantBo bo, PageQuery pageQuery);

    CommercialTenantVo getDetails(Long commercialTenantId);

    CommercialTenantVo getCommercialTenant();

    Boolean updateCommercialTenant(CommercialTenantBo bo);
}
