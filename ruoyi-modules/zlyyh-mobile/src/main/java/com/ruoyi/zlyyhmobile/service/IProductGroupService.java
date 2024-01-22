package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.ProductGroup;
import com.ruoyi.zlyyh.domain.vo.ProductGroupVo;
import com.ruoyi.zlyyh.domain.bo.ProductGroupBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 商品组规则配置Service接口
 *
 * @author yzg
 * @date 2024-01-16
 */
public interface IProductGroupService {

    /**
     * 查询商品组规则配置
     */
    ProductGroupVo queryById(Long productGroupId);

    /**
     * 查询商品组规则配置列表
     */
    TableDataInfo<ProductGroupVo> queryPageList(ProductGroupBo bo, PageQuery pageQuery);

    /**
     * 查询商品组规则配置列表
     */
    List<ProductGroupVo> queryList(ProductGroupBo bo);


}
