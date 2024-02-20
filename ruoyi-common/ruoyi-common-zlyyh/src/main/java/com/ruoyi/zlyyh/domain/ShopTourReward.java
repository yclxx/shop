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
 * 巡检奖励对象 t_shop_tour_reward
 *
 * @author yzg
 * @date 2024-01-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop_tour_reward")
public class ShopTourReward extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 巡检奖励id
     */
    @TableId(value = "tour_reward_id")
    private Long tourRewardId;
    /**
     * 巡检人员id
     */
    private Long verifierId;
    /**
     * 巡检次数
     */
    private Long count;
    /**
     * 巡检奖励  单位：分
     */
    private Long amount;
    /**
     * 发放状态  0-未发放  1-发放中  2-已发放
     */
    private String status;
    /**
     * 删除标志  0-存在  2-删除
     */
    @TableLogic
    private String delFlag;

}
