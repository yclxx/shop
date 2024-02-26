package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.util.Date;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 银联任务组对象 t_unionpay_mission_group
 *
 * @author yzg
 * @date 2024-02-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_unionpay_mission_group")
public class UnionpayMissionGroup extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 任务组ID
     */
    @TableId(value = "up_mission_group_id")
    private Long upMissionGroupId;
    /**
     * 任务组名称
     */
    private String upMissionGroupName;
    /**
     * 状态  0-正常  1-停用
     */
    private String status;
    /**
     * 开始时间
     */
    private Date startDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 银联任务组编号
     */
    private String upMissionGroupUpid;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 删除标志  0-存在  2-删除
     */
    @TableLogic
    private String delFlag;

}
