package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.ProductCoupon;
import com.ruoyi.zlyyh.domain.vo.ProductCouponVo;

import java.util.List;

/**
 * 优惠券Mapper接口
 *
 * @author yzg
 * @date 2023-10-16
 */
public interface ProductCouponMapper extends BaseMapperPlus<ProductCouponMapper,ProductCoupon, ProductCouponVo> {
    int insertAll(List<ProductCoupon> productActionsList);
}
