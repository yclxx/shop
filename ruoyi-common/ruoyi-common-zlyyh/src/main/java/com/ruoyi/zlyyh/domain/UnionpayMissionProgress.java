package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 银联任务进度对象 t_unionpay_mission_progress
 *
 * @author yzg
 * @date 2024-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_unionpay_mission_progress")
public class UnionpayMissionProgress extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 任务进度ID
     */
    @TableId(value = "progress_id")
    private Long progressId;
    /**
     * 银联任务ID
     */
    private Long upMissionId;
    /**
     * 银联任务组ID
     */
    private Long upMissionGroupId;
    /**
     * 银联任务用户id
     */
    private Long upMissionUserId;
    /**
     * 当日进度
     */
    private Long dayProgress;
    /**
     * 本周进度
     */
    private Long weekProgress;
    /**
     * 本月进度
     */
    private Long monthProgress;
    /**
     * 活动总进度
     */
    private Long activityProgress;
    /**
     * 删除标志  0-存在  2-删除
     */
    @TableLogic
    private String delFlag;

}
