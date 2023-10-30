package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.CouponBo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantProductVo;
import com.ruoyi.zlyyh.domain.vo.CouponVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.mapper.CouponMapper;
import com.ruoyi.zlyyh.mapper.ProductCouponMapper;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyhmobile.service.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券Service业务层处理
 *
 * @author yzg
 * @date 2023-10-16
 */
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements ICouponService {

    private final CouponMapper baseMapper;
    private final ProductCouponMapper productCouponMapper;
    private final ProductMapper productMapper;

    /**
     * 查询优惠券
     */
    @Override
    public CouponVo queryById(Long couponId) {
        CouponVo couponVo = baseMapper.selectVoById(couponId);
        //优惠券新增商品关联表
        List<ProductCoupon> productCoupons = productCouponMapper.selectList(new LambdaQueryWrapper<ProductCoupon>().eq(ProductCoupon::getCouponId, couponVo.getCouponId()));
        if (ObjectUtil.isNotEmpty(productCoupons)){
            List<Long> productIds = productCoupons.stream().map(ProductCoupon::getProductId).collect(Collectors.toList());
            List<ProductVo> productVos = productMapper.selectVoList(new LambdaQueryWrapper<Product>().in(Product::getProductId, productIds));
            couponVo.setProductVoList(productVos);
        }
        return couponVo;
    }

    /**
     * 查询优惠券列表
     */
    @Override
    public TableDataInfo<CouponVo> queryPageList(CouponBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Coupon> lqw = buildQueryWrapper(bo);
        Page<CouponVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询优惠券列表
     */
    @Override
    public List<CouponVo> queryList(CouponBo bo) {
        LambdaQueryWrapper<Coupon> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public boolean conversion(Coupon coupon) {
        // 查询兑换码
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.eq(Coupon::getRedeemCode, coupon.getRedeemCode());
        lqw.eq(Coupon::getPlatformKey, coupon.getPlatformKey());
        Coupon code = baseMapper.selectOne(lqw);
        if (ObjectUtil.isEmpty(code)) {
            throw new ServiceException("兑换码不存在！");
        }
        if (code.getUseStatus().equals("1") || code.getUseStatus().equals("2") || code.getUseStatus().equals("3")) {
            throw new ServiceException("兑换码已兑换，请勿重复兑换！");
        }
        if (code.getUseStatus().equals("4") || code.getUseStatus().equals("5") || DateUtils.validTime(code.getConversionEndDate(), 1)) {
            throw new ServiceException("兑换码已失效！");
        }
        // 校验可兑换时间
        if (ObjectUtil.isNotNull(code.getConversionStartDate()) && !DateUtils.validTime(code.getConversionStartDate(), 0)) {
            throw new ServiceException("兑换码未到可兑换日期！");
        }
        // 兑换优惠券
        code.setUserId(coupon.getUserId());
        code.setUserAddTime(new Date());
        code.setUseStatus("1");
        return baseMapper.updateById(code) > 0;
    }





    private LambdaQueryWrapper<Coupon> buildQueryWrapper(CouponBo bo) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getCouponType()), Coupon::getCouponType, bo.getCouponType());
        lqw.eq(bo.getUseTime() != null, Coupon::getUseTime, bo.getUseTime());
        lqw.eq(StringUtils.isNotBlank(bo.getNumber()), Coupon::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getActionNo()), Coupon::getActionNo, bo.getActionNo());
        lqw.eq(bo.getUserId() != null, Coupon::getUserId, bo.getUserId());
        lqw.eq(bo.getPlatformKey() != null, Coupon::getPlatformKey, bo.getPlatformKey());
        if ("2".equals(bo.getUseStatus())) {
            lqw.in(Coupon::getUseStatus, "2", "3");
        } else if ("3".equals(bo.getUseStatus())) {
            lqw.in(Coupon::getUseStatus, "4", "5");
        } else {
            lqw.eq(Coupon::getUseStatus, bo.getUseStatus());
        }

        return lqw;
    }



    /**
     * 查询优惠券商品配置列表
     */
    public List<Long> queryCouponIdListByProductIds(List<Long> productIds) {
        QueryWrapper<ProductCoupon> lqw = Wrappers.query();
        lqw.select("DISTINCT coupon_id").lambda().in(ProductCoupon::getProductId, productIds);
        List<ProductCoupon> productCoupons = productCouponMapper.selectList(lqw);
        if(ObjectUtil.isEmpty(productCoupons)){
            return new ArrayList<>();
        }
        return productCoupons.stream().map(ProductCoupon::getCouponId).collect(Collectors.toList());
    }


}
