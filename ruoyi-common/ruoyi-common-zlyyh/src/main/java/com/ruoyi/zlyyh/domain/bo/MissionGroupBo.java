package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 任务组业务对象
 *
 * @author yzg
 * @date 2023-05-10
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class MissionGroupBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long missionGroupId;

    /**
     * 任务组名称
     */
    @NotBlank(message = "任务组名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String missionGroupName;

    /**
     * 状态
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
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
     * 规则图片
     */
    private String missionImg;
    /**
     * 默认背景图片
     */
    private String missionBgImg;

    /**
     * 任务组编号
     */
    private String missionGroupUpid;

    /**
     * 积点兑红包：0-不可兑换，1-可兑换
     */
    private String jdConvertRedPacket;

    /**
     * 平台标识
     */
    @NotNull(message = "平台标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long platformKey;

    private String showCity;

    private BigDecimal convertValue;
}
