package com.ruoyi.system.api.model;

import lombok.Data;

/**
 * @author 25487
 */
@Data
public class YsfEntity extends LoginEntity {
    /**
     * accessToken
     */
    private String accessToken;
    /**
     * 基础服务令牌 backendToken
     */
    private String backendToken;
}
