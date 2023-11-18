package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * 查询美食商品
     */
    ProductVo queryFoodById(Long productId);

    /**
     * 查询演出票
     *
     * @param productId
     * @return
     */
    ProductVo queryTicketById(Long productId);

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
    List<ProductVo> queryListByCommercialId(Long platformKey, Long commercialId, String weekDate, String showCity);

    /**
     * 查询商品列表（根据门店id）
     */
    List<ProductVo> queryListByShopId(Long platformKey, Long shopId, String weekDate, String showCity);

    /**
     * 查询商品列表
     */
    List<Long> queryCommercialTenantIdList(String weekDate, String showCity, Long platformId);

    /**
     * 查询商品信息
     *
     * @param productIds  产品ID 集合
     * @param cityCode    城市编码
     * @param weekDate    周几
     * @param platformKey 平台标识
     * @return 商品信息
     */
    List<ProductVo> queryGrabPeriodProduct(Set<Object> productIds, String cityCode, String weekDate, Long platformKey);

    /**
     * 查询商品信息
     *
     * @param productIds  产品ID 集合
     * @param cityCode    城市编码
     * @param weekDate    周几
     * @param platformKey 平台标识
     * @return 商品信息
     */
    Map<Long, ProductVo> queryGrabPeriodProductMap(Set<Object> productIds, String cityCode, String weekDate, Long platformKey);

    /**
     * 编辑商品
     */
    Boolean updateProductById(ProductBo bo);
}
