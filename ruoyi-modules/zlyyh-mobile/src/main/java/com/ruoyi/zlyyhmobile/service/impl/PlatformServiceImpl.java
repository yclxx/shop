package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.Area;
import com.ruoyi.zlyyh.domain.vo.AreaVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.mapper.AreaMapper;
import com.ruoyi.zlyyh.mapper.PlatformMapper;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 平台信息Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class PlatformServiceImpl implements IPlatformService {

    private final PlatformMapper baseMapper;
    private final AreaMapper areaMapper;

    /**
     * 查询平台信息
     */
    @Cacheable(cacheNames = CacheNames.PLATFORM, key = "#platformKey")
    @Override
    public PlatformVo queryById(Long platformKey) {
        PlatformVo platformVo = baseMapper.selectVoById(platformKey);
        if(null != platformVo && StringUtils.isNotBlank(platformVo.getPlatformCity()) && !"ALL".equalsIgnoreCase(platformVo.getPlatformCity())){
            List<AreaVo> areaVos = areaMapper.selectVoList(new LambdaQueryWrapper<Area>().eq(Area::getLevel,"city").in(Area::getAdcode, platformVo.getPlatformCity().split(",")));
            platformVo.setPlatformCityList(areaVos);
        }
        return platformVo;
    }
}
