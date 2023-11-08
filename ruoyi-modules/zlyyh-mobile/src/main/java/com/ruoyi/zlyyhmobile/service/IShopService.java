package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.bo.ShopMerchantBo;
import com.ruoyi.zlyyh.domain.vo.ShopMerchantVo;
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

    Shop getShopBytId(Long shopId);

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

    List<ShopMerchantVo> getShopMerchantVo(ShopMerchantBo bo);

    Boolean updateShopMerchantById(List<ShopMerchantBo> bos);

    boolean addApproval(MerchantApprovalBo bo);
}
