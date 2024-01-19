package com.ruoyi.system.dubbo;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.RemoteSysConfigService;
import com.ruoyi.system.api.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 参数配置
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteSysConfigServiceImpl implements RemoteSysConfigService {

    //private final SysConfigMapper baseMapper;
    private final ISysConfigService sysConfigService;

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    //@Cacheable(cacheNames = CacheNames.SYS_CONFIG, key = "#configKey")
    @Override
    public String selectConfigByKey(String configKey) {
        //SysConfig retConfig = baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
        //    .eq(SysConfig::getConfigKey, configKey));
        //if (ObjectUtil.isNotNull(retConfig)) {
        //    return retConfig.getConfigValue();
        //}
        //return StringUtils.EMPTY;
        return sysConfigService.selectConfigByKey(configKey);
    }

}
