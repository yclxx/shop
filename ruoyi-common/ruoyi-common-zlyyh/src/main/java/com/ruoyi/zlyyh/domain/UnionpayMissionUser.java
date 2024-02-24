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
 * 银联任务用户对象 t_unionpay_mission_user
 *
 * @author yzg
 * @date 2024-02-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_unionpay_mission_user")
public class UnionpayMissionUser extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 银联任务用户ID
     */
    @TableId(value = "up_mission_user_id")
    private Long upMissionUserId;
    /**
     * 银联任务组ID
     */
    private Long upMissionGroupId;
    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 状态  0-正常  1-停用
     */
    private String status;
    /**
     * 删除标志  0-存在  2-删除
     */
    @TableLogic
    private String delFlag;

}
