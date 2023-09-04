package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.PlatformCityIndex;
import com.ruoyi.zlyyh.domain.vo.PlatformCityIndexVo;
import com.ruoyi.zlyyh.mapper.PlatformCityIndexMapper;
import com.ruoyi.zlyyhmobile.service.IPlatformCityIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义首页Service业务层处理
 *
 * @author yzg
 * @date 2023-08-07
 */
@RequiredArgsConstructor
@Service
public class PlatformCityIndexServiceImpl implements IPlatformCityIndexService {

    private final PlatformCityIndexMapper baseMapper;

    /**
     * 查询自定义首页
     */
    @Cacheable(cacheNames = CacheNames.cityIndexList, key = "#platformKey+'-'+#cityCode")
    @Override
    public List<PlatformCityIndexVo> queryByCityCode(String cityCode, Long platformKey) {
        LambdaQueryWrapper<PlatformCityIndex> lqw = Wrappers.lambdaQuery();
        lqw.eq(PlatformCityIndex::getAdcode, cityCode);
        lqw.eq(PlatformCityIndex::getStatus, "0");
        lqw.eq(PlatformCityIndex::getPlatformKey, platformKey);
        lqw.last("order by id desc");
        return baseMapper.selectVoList(lqw);
    }
}
