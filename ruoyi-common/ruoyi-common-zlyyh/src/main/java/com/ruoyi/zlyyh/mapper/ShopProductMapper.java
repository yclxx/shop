package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.ShopProduct;
import com.ruoyi.zlyyh.domain.vo.ShopProductVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品门店关联Mapper接口
 *
 * @author yzg
 * @date 2023-05-16
 */
public interface ShopProductMapper extends BaseMapperPlus<ShopProductMapper, ShopProduct, ShopProductVo> {

    /**
     * 查询商品门店城市
     * @param productId 商品ID
     * @return 返回城市列表
     */
    List<String> queryCityCode(@Param("productId") Long productId);
}
