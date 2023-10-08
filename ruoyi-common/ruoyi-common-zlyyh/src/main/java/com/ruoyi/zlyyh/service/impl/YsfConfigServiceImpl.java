package com.ruoyi.zlyyh.service.impl;

import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.mapper.YsfConfigMapper;
import com.ruoyi.zlyyh.service.YsfConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author yzg
 */
@RequiredArgsConstructor
@Service
public class YsfConfigServiceImpl implements YsfConfigService {
    private final YsfConfigMapper ysfConfigMapper;

    @Cacheable(cacheNames = CacheNames.ysfConfig, key = "#platformId+'-'+#key")
    @Override
    public String queryValueByKey(Long platformId, String key) {
        try {
            if (null != platformId) {
                String result = ysfConfigMapper.queryValueByKey(platformId, key);
                if (StringUtils.isNotBlank(result)) {
                    return result;
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    @Cacheable(cacheNames = CacheNames.ysfConfig, key = "'-1-'+#key")
    @Override
    public String queryValueByKeys(String key) {
        try {
            if (null != key) {
                String result = ysfConfigMapper.queryValueByKeys(key);
                if (StringUtils.isNotBlank(result)) {
                    return result;
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }
}
