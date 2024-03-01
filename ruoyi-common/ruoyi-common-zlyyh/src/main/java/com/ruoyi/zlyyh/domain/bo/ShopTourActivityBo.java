package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.util.Date;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 巡检活动业务对象
 *
 * @author yzg
 * @date 2024-03-01
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopTourActivityBo extends BaseEntity {

    /**
     * 巡检活动id
     */
    @NotNull(message = "巡检活动id不能为空", groups = { EditGroup.class })
    private Long tourActivityId;

    /**
     * 巡检活动名称
     */
    @NotBlank(message = "巡检活动名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tourActivityName;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date startDate;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date endDate;

    /**
     * 状态  0-正常  1-停用
     */
    @NotBlank(message = "状态  0-正常  1-停用不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;


}
