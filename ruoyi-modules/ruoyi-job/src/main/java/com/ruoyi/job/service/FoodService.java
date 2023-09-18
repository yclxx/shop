package com.ruoyi.job.service;

import com.ruoyi.system.api.RemoteCtripFoodService;
import com.ruoyi.system.api.RemoteFoodService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 美食套餐定时任务
 */
@Slf4j
@Service
public class FoodService {

    @DubboReference(retries = 0)
    private RemoteFoodService remoteFoodService;
    @DubboReference(retries = 0)
    private RemoteCtripFoodService remoteCtripFoodService;


    /**
     * 定时获取美食套餐
     */
    @XxlJob("getFoodList")
    public void getFoodList(){
        String jobParam = XxlJobHelper.getJobParam();
        remoteFoodService.getFoodList(Long.valueOf(jobParam));
    }

    /**
     * 定时获取美食套餐
     */
    @XxlJob("getCtripFoodList")
    public void getCtripFoodList(){
        String jobParam = XxlJobHelper.getJobParam();
        remoteCtripFoodService.getCtripFoodList(Long.valueOf(jobParam));
    }

}
