package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 商品Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IProductService {

    /**
     * 查询商品
     */
    ProductVo queryById(Long productId);

    ProductVo queryByExternalProductId(String externalProductId);

    Product queryByExternalProductId(String externalProductId, String productType, Long platformKey);

    /**
     * 查询商品列表
     */
    TableDataInfo<ProductVo> queryPageList(ProductBo bo, PageQuery pageQuery);

    /**
     * 查询商品列表
     */
    List<ProductVo> queryList(ProductBo bo);

    /**
     * 查询商品列表
     */
    List<ProductVo> queryProductList(ProductBo bo);

    /**
     * 修改商品
     */
    Boolean insertByBo(ProductBo bo);

    Boolean insert(ProductBo bo);

    /**
     * 修改商品
     */
    Boolean updateByBo(ProductBo bo);

    /**
     * 校验并批量删除商品信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 批量下架商品
     * @param ids
     */
    void updateProducts(Collection<Long> ids);
}
