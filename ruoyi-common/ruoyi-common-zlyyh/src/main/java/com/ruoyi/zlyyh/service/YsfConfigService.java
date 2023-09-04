package com.ruoyi.zlyyh.service;

/**
 * @author yzg
 */
public interface YsfConfigService {
    /**
     * 获取平台参数配置
     *
     * @param platformId 平台标识
     * @param key        参数key
     * @return 参数值
     */
    String queryValueByKey(Long platformId, String key);
}
