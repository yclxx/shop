package com.ruoyi.zlyyhmobile.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 25487
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheMissionRecordEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long missionUserRecordId;
}
