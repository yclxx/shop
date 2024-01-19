package com.ruoyi.zlyyhadmin.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyhadmin.domain.bo.ProductJoinParam;

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

    Product queryByExternalProductId(String externalProductId, String productType);

    /**
     * 查询商品列表
     */
    TableDataInfo<ProductVo> queryPageList(ProductBo bo, PageQuery pageQuery);

    TableDataInfo<ProductVo> queryPageList(ProductJoinParam bo, PageQuery pageQuery);

    TableDataInfo<ProductVo> queryPagecategoryProductList(ProductBo bo, PageQuery pageQuery);

    TableDataInfo<ProductVo> queryPageGroupProductList(ProductBo bo, PageQuery pageQuery);

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
     * 设置商品城市
     *
     * @param productId 商品编号
     */
    void setProductCity(Long productId);

    /**
     * 校验并批量删除商品信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 批量下架商品
     *
     * @param ids
     */
    void updateProducts(Collection<Long> ids, String productType);

    void lianProductCall(JSONObject param);

    void lianProductStatusCall(JSONObject param);

    /**
     * 查询银联开放平台票券剩余数量
     */
    TableDataInfo<ProductVo> queryPageListByProductType(PageQuery pageQuery);

    /**
     * 查询产品剩余数量
     *
     * @param productVo 产品
     */
    void queryProductCount(ProductVo productVo);


}
