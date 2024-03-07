package com.ruoyi.system.api.model;

public interface RemoteShopTourService {

    /**
     * 定时查巡检预约有效期
     */
    void queryShopTourValidity(String jobParam);
}
