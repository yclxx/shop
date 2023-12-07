package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 推广任务业务对象
 *
 * @author yzg
 * @date 2023-11-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PromotionTaskBo extends BaseEntity {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Long taskId;

    /**
     * 平台标识
     */
    @NotNull(message = "平台标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long platformKey;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String taskName;

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
     * 活动性质
     */
    @NotBlank(message = "活动性质不能为空", groups = { AddGroup.class, EditGroup.class })
    private String activityNature;

    /**
     * 状态
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 规则图片
     */
    @NotBlank(message = "规则图片不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ruleImage;

    /**
     * 推广图片
     */
    @NotBlank(message = "推广图片不能为空", groups = { AddGroup.class, EditGroup.class })
    private String image;

    /**
     * 背景图片
     */
    @NotBlank(message = "背景图片不能为空", groups = { AddGroup.class, EditGroup.class })
    private String backgroundImage;

    /**
     * 展示城市
     */
    //@NotBlank(message = "展示城市不能为空", groups = { AddGroup.class, EditGroup.class })
    private String showCity;

    /**
     * 排序
     */
    //@NotBlank(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sort;


}
