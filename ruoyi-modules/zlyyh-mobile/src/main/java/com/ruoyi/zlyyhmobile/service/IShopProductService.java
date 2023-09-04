package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShopProductBo;
import com.ruoyi.zlyyh.domain.vo.ShopProductVo;

import java.util.Collection;
import java.util.List;

/**
 * 商品门店关联Service接口
 *
 * @author yzg
 * @date 2023-05-16
 */
public interface IShopProductService {

    /**
     * 查询商品门店关联
     */
    ShopProductVo queryById(Long id);

    /**
     * 查询商品门店关联列表
     */
    TableDataInfo<ShopProductVo> queryPageList(ShopProductBo bo, PageQuery pageQuery);

    /**
     * 查询商品门店关联列表
     */
    List<ShopProductVo> queryList(ShopProductBo bo);

    /**
     * 修改商品门店关联
     */
    Boolean insertByBo(ShopProductBo bo);

    /**
     * 修改商品门店关联
     */
    Boolean updateByBo(ShopProductBo bo);

    /**
     * 校验并批量删除商品门店关联信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据门店id删除门店商品关联信息
     * @param shopId
     * @return
     */
    Integer deleteWithValidByShopId(Long shopId);

    List<ShopProductVo> queryByShopId(Long shopId);
}
