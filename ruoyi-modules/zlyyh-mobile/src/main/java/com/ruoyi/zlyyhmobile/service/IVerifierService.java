package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.domain.vo.ShopMerchantVo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;
import com.ruoyi.zlyyhmobile.domain.bo.ShopAndMerchantBo;

import java.util.List;
import java.util.Map;

/**
 * 商户端接口
 */
public interface IVerifierService {
    VerifierVo info(VerifierBo bo);

    VerifierVo queryById(Long id);

    Boolean updateVerifier(VerifierBo bo);

    List<ShopVo> queryShopList(VerifierBo bo);

    Map<String, Object> getShopId(Long shopId);

    Boolean updateShopId(ShopAndMerchantBo bo);


    Boolean insertShop(ShopAndMerchantBo bo);

    List<VerifierVo> verifierList(Long shopId);

    TableDataInfo<ShopVo> queryShopPageList(VerifierBo bo, PageQuery pageQuery);

    Boolean updateShopById(ShopBo bo);

    TableDataInfo<ProductVo> queryProductPageList(VerifierBo bo, PageQuery pageQuery);

    void productStatus(ProductBo bo);

    TableDataInfo<VerifierVo> getVerifierListByShop(CodeBo bo, PageQuery pageQuery);

    Boolean cancelVerifier(VerifierBo bo);

    Boolean addVerifier(VerifierBo bo);

    Boolean updateProductById(ProductBo bo);

    Boolean insertProduct(ProductBo bo);

    List<ShopMerchantVo> getShopMerchant(ShopMerchantBo bo);

    Boolean insertShopMerchant(ShopAndMerchantBo bo);

    /**
     * 新增商户门店门店编号(商户端)
     */
    Boolean insertTenantShopMerchant(ShopAndMerchantBo bo);
}
