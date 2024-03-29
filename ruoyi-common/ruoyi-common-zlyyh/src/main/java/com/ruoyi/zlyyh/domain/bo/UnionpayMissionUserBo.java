package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 银联任务用户业务对象
 *
 * @author yzg
 * @date 2024-02-21
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class UnionpayMissionUserBo extends BaseEntity {

    /**
     * 银联任务用户ID
     */
    @NotNull(message = "银联任务用户ID不能为空", groups = { EditGroup.class })
    private Long upMissionUserId;

    /**
     * 银联任务组ID
     */
    //@NotNull(message = "银联任务组ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long upMissionGroupId;

    /**
     * 用户Id
     */
    //@NotNull(message = "用户Id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 平台标识
     */
    //@NotNull(message = "平台标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long platformKey;

    /**
     * 状态  0-正常  1-停用
     */
    //@NotBlank(message = "状态  0-正常  1-停用不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    private List<Long> userIds;

}
