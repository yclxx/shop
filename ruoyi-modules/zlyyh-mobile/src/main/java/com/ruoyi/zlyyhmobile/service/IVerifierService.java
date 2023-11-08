package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;

import java.util.List;

/**
 * 商户端接口
 */
public interface IVerifierService {
    VerifierVo info(VerifierBo bo);

    List<ShopVo> queryShopList(VerifierBo bo);

    List<VerifierVo> verifierList(Long shopId);

    TableDataInfo<ShopVo> queryShopPageList(VerifierBo bo, PageQuery pageQuery);

    Boolean updateShopById(ShopBo bo);

    Boolean updateShopMerchantById(List<ShopMerchantBo> bos);

    TableDataInfo<Product> queryProductPageList(VerifierBo bo, PageQuery pageQuery);

    void productStatus(ProductBo bo);

    TableDataInfo<VerifierVo> getVerifierListByShop(CodeBo bo, PageQuery pageQuery);

    Boolean cancelVerifier(CodeBo bo);

    Boolean addVerifier(CodeBo bo);

    Boolean updateProductById(ProductBo bo);
}
