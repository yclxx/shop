package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 奖励发放记录业务对象
 *
 * @author yzg
 * @date 2023-10-18
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketLogBo extends BaseEntity {
    private Long logId;
    /**
     * 平台标识
     */
    private Long platformKey;

    /**
     *
     */
    private Long marketId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 领取时间
     */
    private Date receiveDate;

    /**
     * 奖励类型
     */
    private String rewardType;


}
