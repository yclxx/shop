package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.YsfConfig;
import com.ruoyi.zlyyh.domain.bo.YsfConfigBo;
import com.ruoyi.zlyyh.domain.vo.YsfConfigVo;
import com.ruoyi.zlyyh.mapper.YsfConfigMapper;
import com.ruoyi.zlyyhadmin.service.IYsfConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 云闪付参数配置Service业务层处理
 *
 * @author yzg
 * @date 2023-07-31
 */
@RequiredArgsConstructor
@Service
public class YsfConfigServiceImpl implements IYsfConfigService {

    private final YsfConfigMapper baseMapper;

    @Cacheable(cacheNames = CacheNames.ysfConfig, key = "#platformId+'-'+#key")
    @Override
    public String queryValueByKey(Long platformId, String key) {
        try {
            if (null != platformId) {
                String result = baseMapper.queryValueByKey(platformId, key);
                if (StringUtils.isNotBlank(result)) {
                    return result;
                }
            } else {
                return queryValueByKey(key);
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    @Cacheable(cacheNames = CacheNames.ysfConfig, key = "#key")
    @Override
    public String queryValueByKey(String key) {
        try {
            if (null != key) {
                String result = baseMapper.queryValueByKeys(key);
                if (StringUtils.isNotBlank(result)) {
                    return result;
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * 查询云闪付参数配置
     */
    @Override
    public YsfConfigVo queryById(Long configId) {
        return baseMapper.selectVoById(configId);
    }

    /**
     * 查询云闪付参数配置列表
     */
    @Override
    public TableDataInfo<YsfConfigVo> queryPageList(YsfConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<YsfConfig> lqw = buildQueryWrapper(bo);
        Page<YsfConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询云闪付参数配置列表
     */
    @Override
    public List<YsfConfigVo> queryList(YsfConfigBo bo) {
        LambdaQueryWrapper<YsfConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<YsfConfig> buildQueryWrapper(YsfConfigBo bo) {
        //Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<YsfConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformId() != null, YsfConfig::getPlatformId, bo.getPlatformId());
        lqw.like(StringUtils.isNotBlank(bo.getConfigName()), YsfConfig::getConfigName, bo.getConfigName());
        lqw.eq(StringUtils.isNotBlank(bo.getConfigKey()), YsfConfig::getConfigKey, bo.getConfigKey());
        lqw.eq(StringUtils.isNotBlank(bo.getConfigValue()), YsfConfig::getConfigValue, bo.getConfigValue());
        lqw.orderByDesc(YsfConfig::getCreateTime);
        return lqw;
    }

    /**
     * 新增云闪付参数配置
     */
    @CacheEvict(cacheNames = CacheNames.ysfConfig, key = "#bo.getPlatformId()+'-'+#bo.configKey")
    @Override
    public Boolean insertByBo(YsfConfigBo bo) {
        YsfConfig add = BeanUtil.toBean(bo, YsfConfig.class);
        //validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setConfigId(add.getConfigId());
        }
        return flag;
    }

    /**
     * 修改云闪付参数配置
     */
    @CacheEvict(cacheNames = CacheNames.ysfConfig, key = "#bo.getPlatformId()+'-'+#bo.configKey")
    @Override
    public Boolean updateByBo(YsfConfigBo bo) {
        YsfConfig update = BeanUtil.toBean(bo, YsfConfig.class);
        //validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    //private void validEntityBeforeSave(YsfConfig entity) {
    //TODO 做一些数据校验,如唯一约束
    //}

    /**
     * 批量删除云闪付参数配置
     */
    @CacheEvict(cacheNames = CacheNames.ysfConfig, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
