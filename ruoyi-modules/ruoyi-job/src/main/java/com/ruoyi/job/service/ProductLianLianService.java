package com.ruoyi.job.service;

import com.ruoyi.system.api.RemoteLianLianProductService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class ProductLianLianService {
    @DubboReference(retries = 0)
    private RemoteLianLianProductService lianLianProductService;

    /**
     * 定时获取联联商品列表
     */
    @XxlJob("selectLianLianProductList")
    public void selectLianLianProductList() {
        String jobParam = XxlJobHelper.getJobParam();
        lianLianProductService.selectLianLianProductList(Long.valueOf(jobParam));
    }
}
