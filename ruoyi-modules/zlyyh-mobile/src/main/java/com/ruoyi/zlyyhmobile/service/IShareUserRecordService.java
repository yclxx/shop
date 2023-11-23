package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;
import com.ruoyi.zlyyh.utils.CloudRechargeEntity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 分销记录Service接口
 *
 * @author yzg
 * @date 2023-11-09
 */
public interface IShareUserRecordService {

    /**
     * 查询分销总次数
     *
     * @param userId 用户ID
     * @return 分销次数
     */
    Long shareCount(Long userId);

    /**
     * 查询分销记录
     */
    ShareUserRecordVo queryById(Long recordId);

    /**
     * 查询分销记录列表
     */
    TableDataInfo<ShareUserRecordVo> queryPageList(ShareUserRecordBo bo, PageQuery pageQuery);

    /**
     * 查询分销记录列表
     */
    List<ShareUserRecordVo> queryList(ShareUserRecordBo bo);

    /**
     * 修改分销记录
     */
    Boolean insertByBo(ShareUserRecordBo bo);

    /**
     * 修改分销记录
     */
    Boolean updateByBo(ShareUserRecordBo bo);

    /**
     * 校验并批量删除分销记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据分享用户ID和订单编号查询分享记录
     *
     * @param number 订单编号
     * @return 分享记录
     */
    List<ShareUserRecordVo> queryByNumber(Long number);

    /**
     * 发送奖励
     *
     * @param recordId 分享记录ID
     */
    void sendAward(Long recordId);

    /**
     * 查询状态
     *
     * @param recordId 分享记录ID
     */
    void querySendAwardStatus(Long recordId);

    BigDecimal sumAwardAmount(Long userId);

    /**
     * 充值中心订单回调
     *
     * @param cloudRechargeEntity 通知参数
     */
    void cloudRechargeCallback(CloudRechargeEntity cloudRechargeEntity);
}
