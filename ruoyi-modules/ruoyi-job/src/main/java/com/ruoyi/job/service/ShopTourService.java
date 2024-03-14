package com.ruoyi.job.service;

import com.ruoyi.system.api.model.RemoteShopTourService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 银联任务相关定时任务
 * @author 25487
 */
@Slf4j
@Service
public class ShopTourService {
    @DubboReference(retries = 0)
    private RemoteShopTourService remoteShopTourService;

    /**
     * 定时查巡检预约有效期
     */
    @XxlJob("queryShopTourValidity")
    public void queryShopTourValidity() {
        String jobParam = XxlJobHelper.getJobParam();
        remoteShopTourService.queryShopTourValidity(jobParam);
    }
}
