package com.ruoyi.job.service;


import com.ruoyi.system.api.RemoteProductService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductUnionPayService {

    @DubboReference(retries = 0)
    private RemoteProductService remoteProductService;

    /**
     * 定时获取银联分销商品列表
     */
    @XxlJob("selectUnionPayProductList")
    public void selectUnionPayProductList() {
        String jobParam = XxlJobHelper.getJobParam();
        remoteProductService.selectUnionPayProductList(Long.valueOf(jobParam));
    }
}
