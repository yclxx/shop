package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Action;
import com.ruoyi.zlyyh.domain.Coupon;
import com.ruoyi.zlyyh.domain.ProductAction;
import com.ruoyi.zlyyh.domain.ProductCoupon;
import com.ruoyi.zlyyh.domain.bo.CouponBo;
import com.ruoyi.zlyyh.domain.vo.CouponVo;
import com.ruoyi.zlyyh.mapper.CouponMapper;
import com.ruoyi.zlyyh.mapper.ProductCouponMapper;
import com.ruoyi.zlyyhadmin.service.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    /**
     * 查询优惠券
     */
    @Override
    public CouponVo queryById(Long couponId) {
        return baseMapper.selectVoById(couponId);
    }

    @Override
    public Long queryNumberByActionNo(String actionNo) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.eq(Coupon::getActionNo, actionNo);
        return baseMapper.selectCount(lqw);
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
    public Boolean addCoupon(Action action, Long number, List<ProductAction> productActions) {
        List<Coupon> addList = new ArrayList<>();
        List<ProductCoupon> productCoupons = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Coupon coupon = new Coupon();
            coupon.setCouponId(IdUtil.getSnowflakeNextId());
            coupon.setRedeemCode(RandomUtil.randomNumbers(10));
            coupon.setCouponName(action.getCouponName());
            coupon.setCouponAmount(action.getCouponAmount());
            coupon.setMinAmount(action.getMinAmount());
            coupon.setCouponType(action.getCouponType());
            coupon.setPeriodOfStart(action.getPeriodOfStart());
            coupon.setPeriodOfValidity(action.getPeriodOfValidity());
            coupon.setCouponDescription(action.getCouponDescription());
            coupon.setUseStatus("0");
            coupon.setGenTime(DateUtils.getNowDate());
            coupon.setActionNo(action.getActionNo());
            coupon.setCouponImage(action.getCouponImage());
            coupon.setConversionStartDate(action.getConversionStartDate());
            coupon.setConversionEndDate(action.getConversionEndDate());
            coupon.setPlatformKey(action.getPlatformKey());
            addList.add(coupon);
            if (ObjectUtil.isNotEmpty(productActions)) {
                for (ProductAction productAction : productActions) {
                    ProductCoupon productCoupon = new ProductCoupon();
                    productCoupon.setCouponId(coupon.getCouponId());
                    productCoupon.setProductId(productAction.getProductId());
                    productCoupons.add(productCoupon);
                }
            }
        }
        if (ObjectUtil.isNotEmpty(addList)) {
            baseMapper.insertBatch(addList);
            if (ObjectUtil.isNotEmpty(productCoupons)) {
                productCouponMapper.insertAll(productCoupons);
            }
            return true;
        }
        return false;
    }

    private LambdaQueryWrapper<Coupon> buildQueryWrapper(CouponBo bo) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCouponName()), Coupon::getCouponName, bo.getCouponName());
        lqw.eq(StringUtils.isNotBlank(bo.getRedeemCode()), Coupon::getRedeemCode, bo.getRedeemCode());
        lqw.eq(bo.getCouponAmount() != null, Coupon::getCouponAmount, bo.getCouponAmount());
        lqw.eq(bo.getMinAmount() != null, Coupon::getMinAmount, bo.getMinAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getCouponType()), Coupon::getCouponType, bo.getCouponType());
        lqw.eq(StringUtils.isNotBlank(bo.getUseStatus()), Coupon::getUseStatus, bo.getUseStatus());
        lqw.eq(bo.getUseTime() != null, Coupon::getUseTime, bo.getUseTime());
        lqw.eq(StringUtils.isNotBlank(bo.getNumber()), Coupon::getNumber, bo.getNumber());
        lqw.eq(bo.getUserAddTime() != null, Coupon::getUserAddTime, bo.getUserAddTime());
        lqw.eq(StringUtils.isNotBlank(bo.getActionNo()), Coupon::getActionNo, bo.getActionNo());
        lqw.eq(StringUtils.isNotBlank(bo.getCouponImage()), Coupon::getCouponImage, bo.getCouponImage());
        lqw.eq(bo.getUserId() != null, Coupon::getUserId, bo.getUserId());
        lqw.eq(bo.getPlatformKey() != null, Coupon::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }

    /**
     * 新增优惠券
     */
    @Override
    public Boolean insertByBo(CouponBo bo) {
        Coupon add = BeanUtil.toBean(bo, Coupon.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCouponId(add.getCouponId());
        }
        return flag;
    }

    /**
     * 修改优惠券
     */
    @Override
    public Boolean updateByBo(CouponBo bo) {
        Coupon coupon = new Coupon();
        coupon.setUseStatus("5");
        LambdaQueryWrapper<Coupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Coupon::getActionNo, bo.getActionNo());
        lqw.in(Coupon::getUseStatus, "0", "1");
        return baseMapper.update(coupon, lqw) > 0;
    }

    /**
     * 批量作废优惠券
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        Coupon coupon = new Coupon();
        coupon.setUseStatus("5");
        LambdaQueryWrapper<Coupon> lqw = new LambdaQueryWrapper<>();
        lqw.in(Coupon::getCouponId,ids);
        lqw.in(Coupon::getUseStatus, "0", "1");
        return baseMapper.update(coupon, lqw) > 0;
    }
}
