package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.Refund;
import com.ruoyi.zlyyh.domain.vo.RefundVo;
import com.ruoyi.zlyyh.domain.bo.RefundBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 退款订单登记Service接口
 *
 * @author yzg
 * @date 2023-08-07
 */
public interface IRefundService {

    /**
     * 查询退款订单登记
     */
    RefundVo queryById(Long refundId);

    /**
     * 查询退款订单登记列表
     */
    TableDataInfo<RefundVo> queryPageList(RefundBo bo, PageQuery pageQuery);

    /**
     * 查询退款订单登记列表
     */
    List<RefundVo> queryList(RefundBo bo);

    /**
     * 修改退款订单登记
     */
    Boolean insertByBo(RefundBo bo);

    /**
     * 修改退款订单登记
     */
    Boolean updateByBo(RefundBo bo);

    /**
     * 审核通过
     */
    void agreeSubmit(Long refundId);

    /**
     * 审核拒绝
     */
    void refuseSubmit(Long refundId);

    /**
     * 校验并批量删除退款订单登记信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
