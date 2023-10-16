package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.TagsShop;
import com.ruoyi.zlyyh.domain.vo.TagsShopVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口
 *
 * @author yzg
 * @date 2023-10-09
 */
public interface TagsShopMapper extends BaseMapperPlus<TagsShopMapper, TagsShop, TagsShopVo> {
    int deleteByShopId(@Param("shopId") Long shopId);

    List<Long> selectByShopId(@Param("shopId") Long shopId);
}
