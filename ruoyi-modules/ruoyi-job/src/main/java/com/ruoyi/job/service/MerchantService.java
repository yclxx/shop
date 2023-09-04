package com.ruoyi.job.service;



import com.ruoyi.system.api.RemoteMerchantService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 银联商户定时任务
 */
@Slf4j
@Service
public class MerchantService {
    @DubboReference(retries = 0)
    private RemoteMerchantService remoteMerchantService;



    /**
     * 定时获取银联商户
     */
    @XxlJob("getMerchantList")
    public void getBrandMerchantList() {
        String jobParam = XxlJobHelper.getJobParam();
        remoteMerchantService.getBrandMerchantList(Long.valueOf(jobParam));
    }

}
