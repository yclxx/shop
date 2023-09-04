package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.Banner;
import com.ruoyi.zlyyh.domain.bo.BannerBo;
import com.ruoyi.zlyyh.domain.vo.BannerVo;
import com.ruoyi.zlyyh.mapper.BannerMapper;
import com.ruoyi.zlyyhmobile.service.IBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 广告管理Service业务层处理
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class BannerServiceImpl implements IBannerService {

    private final BannerMapper baseMapper;

    /**
     * 查询广告管理列表
     */
    @Cacheable(cacheNames = CacheNames.BANNER, key = "#bo.getPlatformKey()+'-'+#bo.bannerType+'-'+#bo.pagePath")
    @Override
    public List<BannerVo> queryList(BannerBo bo) {
        LambdaQueryWrapper<Banner> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getBannerType()), Banner::getBannerType, bo.getBannerType());
        lqw.eq(StringUtils.isNotBlank(bo.getPagePath()), Banner::getPagePath, bo.getPagePath());
        lqw.eq(bo.getPlatformKey() != null, Banner::getPlatformKey, bo.getPlatformKey());
        lqw.eq(Banner::getStatus, "0");
        lqw.and(lm -> {
            lm.isNull(Banner::getStartTime).or().lt(Banner::getStartTime, new Date());
        });
        lqw.and(lm -> {
            lm.isNull(Banner::getEndTime).or().gt(Banner::getEndTime, new Date());
        });
        lqw.last("order by banner_rank asc");
        return baseMapper.selectVoList(lqw);
    }
}
