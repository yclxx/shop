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
 * 任务组背景对象 t_unionpay_mission_group_bg
 *
 * @author yzg
 * @date 2024-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_unionpay_mission_group_bg")
public class UnionpayMissionGroupBg extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 任务组背景id
     */
    @TableId(value = "mission_bg_id")
    private Long missionBgId;
    /**
     * 任务组ID
     */
    private Long missionGroupId;
    /**
     * 背景图片
     */
    private String bgImg;
    /**
     * 背景类型  1-首页(报名前)  2-首页(报名后)  3-记录页
     */
    private String imgType;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 状态  0-正常  1-停用
     */
    private String status;
    /**
     * 删除标志  0-存在 2-删除
     */
    @TableLogic
    private String delFlag;
    /**
     * 排序 从小到大 默认99
     */
    private Long sort;

    private String bgName;

    private String isToLink;

    private String toUrl;

}
