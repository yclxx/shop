package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.MissionUserRecord;
import com.ruoyi.zlyyh.domain.bo.MissionGroupProductBo;
import com.ruoyi.zlyyh.domain.vo.MissionUserRecordVo;
import com.ruoyi.zlyyh.utils.CloudRechargeEntity;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.domain.vo.UserProductCount;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 活动记录Service接口
 *
 * @author yzg
 * @date 2023-05-10
 */
public interface IMissionUserRecordService {
    List<MissionUserRecordVo> getRecordList(Long missionGroupId);

    TableDataInfo<MissionUserRecordVo> getUserRecordPageList(Long missionGroupId, PageQuery pageQuery);

    MissionUserRecord getDraw(Long missionGroupId, Long userId, Long platformKey, String channel);

    Long getUserDrawCount(Long missionGroupId, Long userId);

    /**
     * 充值中心订单回调
     *
     * @param cloudRechargeEntity 通知参数
     */
    void cloudRechargeCallback(CloudRechargeEntity cloudRechargeEntity);

    /**
     * 发奖
     *
     * @param missionUserRecordId 需要发放的抽奖记录
     */
    void sendDraw(Long missionUserRecordId);

    /**
     * 失败自动补发
     */
    void sendStatusOrder();

    /**
     * 缓存已发次数
     *
     * @param missionUserRecord 抽奖记录
     */
    void saveDrawCount(MissionUserRecord missionUserRecord, Date cacheTime);

    Map<String, Double> getUserQuota(Long missionId);

    /**
     * 兑换商品
     */
    CreateOrderResult convertProduct(MissionGroupProductBo missionGroupProductBo);

    /**
     * 查询任务完成进度
     */
    void queryMission();

    /**
     * 查询用户购买次数
     *
     * @param missionGroupId 任务组ID
     * @param userId         用户ID
     * @return 结果
     */
    UserProductCount getUserProductPayCount(Long missionGroupId, Long missionId, Long userId);

    /**
     * 购买商品
     */
    CreateOrderResult payMissionGroupProduct(Long missionId, Long userId);
}
