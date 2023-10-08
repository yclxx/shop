package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ProductInfoBo;
import com.ruoyi.zlyyh.domain.vo.ProductInfoVo;

import java.util.Collection;
import java.util.List;

/**
 * 商品拓展Service接口
 *
 * @author yzg
 * @date 2023-05-15
 */
public interface IProductInfoService {

    /**
     * 查询商品拓展
     */
    ProductInfoVo queryById(Long productId);

    /**
     * 查询产品扩展
     */
    ProductInfoVo queryByItemId(String itemId);

    /**
     * 查询商品拓展列表
     */
    TableDataInfo<ProductInfoVo> queryPageList(ProductInfoBo bo, PageQuery pageQuery);

    /**
     * 查询商品拓展列表
     */
    List<ProductInfoVo> queryList(ProductInfoBo bo);

    /**
     * 修改商品拓展
     */
    Boolean insertByBo(ProductInfoBo bo);

    /**
     * 修改商品拓展
     */
    Boolean updateByBo(ProductInfoBo bo);

    /**
     * 校验并批量删除商品拓展信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
