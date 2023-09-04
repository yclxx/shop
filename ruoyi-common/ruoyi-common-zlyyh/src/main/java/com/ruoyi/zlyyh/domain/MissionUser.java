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
 * 任务用户对象 t_mission_user
 *
 * @author yzg
 * @date 2023-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_mission_user")
public class MissionUser extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 任务用户ID
     */
    @TableId(value = "mission_user_id")
    private Long missionUserId;
    /**
     * 任务组ID
     */
    private Long missionGroupId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
