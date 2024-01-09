package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.ShopMerchant;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;

import java.util.List;

/**
 * 门店Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IShopService {

    /**
     * 查询门店
     */
    ShopVo queryById(Long shopId);

    Boolean updateShopById(ShopBo bo);

    /**
     * 查询门店列表
     */
    TableDataInfo<ShopVo> queryPageList(ShopBo bo, PageQuery pageQuery);

    /**
     * 查询门店列表
     */
    TableDataInfo<ShopVo> getShopListByProductId(ShopBo bo, PageQuery pageQuery);

    /**
     * 查询门店列表
     */
    TableDataInfo<ShopVo> selectShopListByCommercialTenantId(ShopBo bo, PageQuery pageQuery);

    /**
     * 查询商户门店
     *
     * @param commercialIds 商户ID
     * @return 门店集合
     */
    List<ShopVo> queryListByCommercialIds(List<Long> commercialIds, String cityCode);

    /**
     * 查询商户门店
     */
    List<Shop> queryListByCommercialId(Long commercialId);

    Boolean updateShopMerchantById(Long shopId, List<ShopMerchant> bos);

    boolean addApproval(MerchantApprovalBo bo);

    /**
     * 查询商户门店数量
     *
     * @param commercialTenantId 商户Id
     * @return 门店数量
     */
    Long selectCountByCommercialTenantId(Long commercialTenantId);
}
