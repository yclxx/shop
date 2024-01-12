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
 * 任务组背景图片配置对象 t_mission_group_bg_img
 *
 * @author yzg
 * @date 2024-01-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_mission_group_bg_img")
public class MissionGroupBgImg extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 任务ID
     */
    @TableId(value = "mission_bg_img_id")
    private Long missionBgImgId;
    /**
     * ID
     */
    private Long missionGroupId;
    /**
     * 状态（0正常 1停用）
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
     * 任务图片
     */
    private String missionBgImg;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
