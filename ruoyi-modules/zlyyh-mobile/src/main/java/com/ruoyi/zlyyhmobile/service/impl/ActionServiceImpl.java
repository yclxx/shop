package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Action;
import com.ruoyi.zlyyh.domain.Coupon;
import com.ruoyi.zlyyh.domain.bo.ActionBo;
import com.ruoyi.zlyyh.domain.bo.CouponBo;
import com.ruoyi.zlyyh.domain.vo.ActionVo;
import com.ruoyi.zlyyh.domain.vo.CouponVo;
import com.ruoyi.zlyyh.mapper.ActionMapper;
import com.ruoyi.zlyyh.mapper.CouponMapper;
import com.ruoyi.zlyyhmobile.service.IActionService;
import com.ruoyi.zlyyhmobile.service.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 优惠券批次Service业务层处理
 *
 * @author yzg
 * @date 2023-10-12
 */
@RequiredArgsConstructor
@Service
public class ActionServiceImpl implements IActionService {

    private final ActionMapper baseMapper;
    private final ICouponService couponService;
    private final CouponMapper couponMapper;

    /**
     * 查询优惠券批次
     */
    @Override
    public ActionVo queryById(Long actionId) {
        return baseMapper.selectVoById(actionId);
    }

    /**
     * 查询优惠券批次列表
     */
    @Override
    public TableDataInfo<ActionVo> queryPageList(ActionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Action> lqw = buildQueryWrapper(bo);
        Page<ActionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 根据批次领取对应的优惠券
     */
    @Override
    public Coupon insertUserCoupon(ActionBo bo, Long userId) {
        if (ObjectUtil.isEmpty(bo)  || ObjectUtil.isEmpty(userId)) {
            throw new ServiceException("优惠券领取失败");
        }
        //判断用户该批次号的优惠券是否已经使用，未使用不能重复领取
        CouponBo couponBo = new CouponBo();
        couponBo.setActionNo(bo.getActionNo());
        couponBo.setUserId(userId);
        couponBo.setUseStatus("1");
        List<CouponVo> couponVos = couponService.queryList(couponBo);
        if (couponVos.size()>0){
            throw new ServiceException("您已领取该优惠券,请您使用后再来领取哦");
        }

        // 生成数量
        Integer count = 1;
        // 批次查询
        ActionBo action = new ActionBo();
        action.setActionNo(bo.getActionNo());
        List<ActionVo> actions = baseMapper.selectVoList(buildQueryWrapper(bo));
        if (ObjectUtil.isEmpty(actions)) {
            throw new ServiceException("优惠券领取失败");
        }
        if (actions.get(0).getStatus().equals("1")) {
            throw new ServiceException("优惠券领取失败");
        }
        // 判断生成数量是否允许
        if (actions.get(0).getCouponCount() != -1) {
            // 查询已生成的优惠券数量
            CouponBo coupon = new CouponBo();
            coupon.setActionNo(action.getActionNo());
            List<CouponVo> coupons = couponService.queryList(coupon);
            count = count + (ObjectUtil.isNotEmpty(coupons) ? coupons.size() : 0);
            if (count > actions.get(0).getCouponCount()) {
                throw new ServiceException("优惠券已领完,请下次再来");
            }
        }

        // 生成优惠券信息
        Coupon coupon = new Coupon();
        coupon.setCouponName(actions.get(0).getCouponName());
        coupon.setCouponAmount(actions.get(0).getCouponAmount());
        coupon.setMinAmount(actions.get(0).getMinAmount());
        coupon.setCouponType(actions.get(0).getCouponType());
        coupon.setPeriodOfStart(actions.get(0).getPeriodOfStart());
        coupon.setPeriodOfValidity(actions.get(0).getPeriodOfValidity());
        coupon.setCouponDescription(actions.get(0).getCouponDescription());
        coupon.setCouponImage(actions.get(0).getCouponImage());
        coupon.setActionNo(actions.get(0).getActionNo());
        coupon.setConversionStartDate(actions.get(0).getConversionStartDate());
        coupon.setConversionEndDate(actions.get(0).getConversionEndDate());
        coupon.setUserId(userId);
        coupon.setUserAddTime(new Date());
        coupon.setUseStatus("1");
        // 优惠券兑换码
        coupon.setRedeemCode(RandomUtil.randomNumbers(10));
        // 新增优惠券
        couponMapper.insert(coupon);
        return coupon;
    }

    private LambdaQueryWrapper<Action> buildQueryWrapper(ActionBo bo) {
        LambdaQueryWrapper<Action> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getActionNo()), Action::getActionNo, bo.getActionNo());
        lqw.eq(StringUtils.isNotBlank(bo.getActionNote()), Action::getActionNote, bo.getActionNote());
        lqw.like(StringUtils.isNotBlank(bo.getCouponName()), Action::getCouponName, bo.getCouponName());
        lqw.eq(StringUtils.isNotBlank(bo.getCouponType()), Action::getCouponType, bo.getCouponType());
        lqw.eq(bo.getPeriodOfStart() != null, Action::getPeriodOfStart, bo.getPeriodOfStart());
        lqw.eq(bo.getPeriodOfValidity() != null, Action::getPeriodOfValidity, bo.getPeriodOfValidity());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Action::getStatus, bo.getStatus());
        lqw.eq(bo.getConversionStartDate() != null, Action::getConversionStartDate, bo.getConversionStartDate());
        lqw.eq(bo.getConversionEndDate() != null, Action::getConversionEndDate, bo.getConversionEndDate());
        lqw.eq(bo.getPlatformKey() != null, Action::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }
}
