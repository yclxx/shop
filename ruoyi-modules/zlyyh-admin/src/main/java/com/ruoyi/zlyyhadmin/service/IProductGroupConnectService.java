package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.ProductGroupConnect;
import com.ruoyi.zlyyh.domain.vo.ProductGroupConnectVo;
import com.ruoyi.zlyyh.domain.bo.ProductGroupConnectBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 商品商品组关联Service接口
 *
 * @author yzg
 * @date 2024-01-16
 */
public interface IProductGroupConnectService {

    /**
     * 查询商品商品组关联
     */
    ProductGroupConnectVo queryById(Long id);

    /**
     * 查询商品商品组关联列表
     */
    TableDataInfo<ProductGroupConnectVo> queryPageList(ProductGroupConnectBo bo, PageQuery pageQuery);

    /**
     * 查询商品商品组关联列表
     */
    List<ProductGroupConnectVo> queryList(ProductGroupConnectBo bo);

    /**
     * 修改商品商品组关联
     */
    Boolean insertByBo(ProductGroupConnectBo bo);

    /**
     * 修改商品商品组关联
     */
    Boolean updateByBo(ProductGroupConnectBo bo);

    /**
     * 校验并批量删除商品商品组关联信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
