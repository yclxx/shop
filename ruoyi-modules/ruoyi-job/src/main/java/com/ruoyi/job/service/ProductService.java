package com.ruoyi.job.service;

import com.ruoyi.system.api.RemoteProductService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductService {
    @DubboReference(retries = 0)
    private RemoteProductService remoteProductService;

    /**
     * 查询票券剩余数量
     */
    @XxlJob("queryProductCount")
    public void queryProductCount() {
        remoteProductService.queryProductCount();
    }
}
