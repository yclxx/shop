package com.ruoyi.zlyyhmobile.event;

import com.ruoyi.zlyyh.domain.MissionUserRecord;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 25487
 */
@Data
public class MissionUserRecordEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private MissionUserRecord missionUserRecord;
    private Date cacheTime;
    private boolean toEvent = true;
}
