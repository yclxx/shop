package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Coupon;
import com.ruoyi.zlyyh.domain.bo.ActionBo;
import com.ruoyi.zlyyh.domain.bo.CouponBo;
import com.ruoyi.zlyyh.domain.vo.ActionVo;
import com.ruoyi.zlyyh.domain.vo.CouponVo;

import java.util.Collection;
import java.util.List;

/**
 * 优惠券批次Service接口
 *
 * @author yzg
 * @date 2023-10-12
 */
public interface IActionService {

    /**
     * 查询优惠券批次
     */
    ActionVo queryById(Long actionId);
    /**
     * 查询优惠券批次列表
     */
    TableDataInfo<ActionVo> queryPageList(ActionBo bo, PageQuery pageQuery);



    /**
     * 根据批次领取对应的优惠券
     */
    Coupon insertUserCoupon(ActionBo bo, Long userId);




}
