package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 邀请记录业务对象
 *
 * @author yzg
 * @date 2023-08-08
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class InviteUserLogBo extends BaseEntity {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 被邀请用户ID
     */
    private Long inviteUserId;

    /**
     * 被邀请用户所在城市
     */
    private String inviteCityName;

    /**
     * 被邀请用户城市行政区号
     */
    private String inviteCityCode;

    /**
     * 订单号
     */
    private Long number;

    /**
     * 任务ID
     */
    private Long missionId;

    /**
     * 平台标识
     */
    private Long platformKey;


}
