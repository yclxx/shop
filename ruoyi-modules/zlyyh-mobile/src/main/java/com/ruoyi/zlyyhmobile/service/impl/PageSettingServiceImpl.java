package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.PageSetting;
import com.ruoyi.zlyyh.domain.bo.PageSettingBo;
import com.ruoyi.zlyyh.domain.vo.PageSettingVo;
import com.ruoyi.zlyyh.mapper.PageSettingMapper;
import com.ruoyi.zlyyhmobile.service.IPageSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 页面配置Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class PageSettingServiceImpl implements IPageSettingService {

    private final PageSettingMapper baseMapper;

    /**
     * 查询页面配置列表
     */
    @Cacheable(cacheNames = CacheNames.PAGE_SETTING, key = "#bo.getPlatformKey()+'-'+#bo.pagePath")
    @Override
    public List<PageSettingVo> queryList(PageSettingBo bo) {
        LambdaQueryWrapper<PageSetting> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PageSetting> buildQueryWrapper(PageSettingBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PageSetting> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getPagePath()), PageSetting::getPagePath, bo.getPagePath());
        lqw.eq(StringUtils.isNotBlank(bo.getBannerType()), PageSetting::getBannerType, bo.getBannerType());
        lqw.eq(bo.getBlockId() != null, PageSetting::getBlockId, bo.getBlockId());
        lqw.eq(StringUtils.isNotBlank(bo.getBlockColumnValue()), PageSetting::getBlockColumnValue, bo.getBlockColumnValue());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), PageSetting::getStatus, bo.getStatus());
        lqw.eq(bo.getPlatformKey() != null, PageSetting::getPlatformKey, bo.getPlatformKey());
        lqw.last("order by sort asc");
        return lqw;
    }
}
