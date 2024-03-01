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
 * 巡检活动对象 t_shop_tour_activity
 *
 * @author yzg
 * @date 2024-03-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop_tour_activity")
public class ShopTourActivity extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 巡检活动id
     */
    @TableId(value = "tour_activity_id")
    private Long tourActivityId;
    /**
     * 巡检活动名称
     */
    private String tourActivityName;
    /**
     * 开始时间
     */
    private Date startDate;
    /**
     * 结束时间
     */
    private Date endDate;
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
