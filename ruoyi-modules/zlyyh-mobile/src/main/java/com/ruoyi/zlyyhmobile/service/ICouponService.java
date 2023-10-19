package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Action;
import com.ruoyi.zlyyh.domain.Coupon;
import com.ruoyi.zlyyh.domain.ProductAction;
import com.ruoyi.zlyyh.domain.bo.CouponBo;
import com.ruoyi.zlyyh.domain.vo.CouponVo;

import java.util.Collection;
import java.util.List;

/**
 * 优惠券Service接口
 *
 * @author yzg
 * @date 2023-10-16
 */
public interface ICouponService {

    /**
     * 查询优惠券
     */
    CouponVo queryById(Long couponId);



    /**
     * 查询优惠券列表
     */
    TableDataInfo<CouponVo> queryPageList(CouponBo bo, PageQuery pageQuery);

    /**
     * 查询优惠券列表
     */
    List<CouponVo> queryList(CouponBo bo);

    /**
     * 兑换优惠券
     *
     * @param coupon 优惠券
     * @return 结果
     */
    boolean conversion(Coupon coupon);



}
