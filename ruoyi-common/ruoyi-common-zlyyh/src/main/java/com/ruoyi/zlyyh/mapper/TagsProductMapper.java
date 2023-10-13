package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.TagsProduct;
import com.ruoyi.zlyyh.domain.vo.TagsProductVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口
 *
 * @author yzg
 * @date 2023-10-09
 */
public interface TagsProductMapper extends BaseMapperPlus<TagsProductMapper, TagsProduct, TagsProductVo> {
    int deleteByProductId(@Param("productId") Long productId);

    List<Long> selectByProductId(@Param("productId") Long productId);
}
