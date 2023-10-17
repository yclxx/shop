package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.zlyyh.domain.ProductCoupon;

import java.util.List;

/**
 * 优惠券Mapper接口
 *
 * @author yzg
 * @date 2023-10-16
 */
public interface ProductCouponMapper extends BaseMapper<ProductCoupon> {
    int insertAll(List<ProductCoupon> productActionsList);
}
