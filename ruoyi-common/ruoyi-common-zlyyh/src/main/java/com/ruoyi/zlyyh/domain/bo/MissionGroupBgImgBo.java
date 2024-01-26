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
 * 任务组背景图片配置业务对象
 *
 * @author yzg
 * @date 2024-01-03
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class MissionGroupBgImgBo extends BaseEntity {

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空", groups = { EditGroup.class })
    private Long missionBgImgId;

    /**
     * 任务组ID
     */
    @NotNull(message = "任务组ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long missionGroupId;

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态（0正常 1停用）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

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
     * 任务图片
     */
    @NotBlank(message = "任务图片不能为空", groups = { AddGroup.class, EditGroup.class })
    private String missionBgImg;

    /**
     * 平台标识
     */
    @NotNull(message = "平台标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long platformKey;


}
