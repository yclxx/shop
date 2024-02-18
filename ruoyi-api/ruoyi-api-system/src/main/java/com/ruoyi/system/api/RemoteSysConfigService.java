package com.ruoyi.system.api;

import com.ruoyi.system.api.domain.SysConfig;

import java.util.List;

/**
 * 参数配置 服务层
 *
 * @author ruoyi
 */
public interface RemoteSysConfigService {

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);

}
