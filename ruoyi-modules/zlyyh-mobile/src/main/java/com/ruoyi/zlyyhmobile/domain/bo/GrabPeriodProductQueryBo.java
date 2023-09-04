package com.ruoyi.zlyyhmobile.domain.bo;

import cn.hutool.core.date.DateUtil;
import lombok.Data;

import java.util.Date;

/**
 * @author 25487
 */
@Data
public class GrabPeriodProductQueryBo {
    /**
     * 活动ID
     */
    private Long grabPeriodId;
    /**
     * 周几 默认今天
     */
    private String weekDate = "" + DateUtil.dayOfWeek(new Date());
}
