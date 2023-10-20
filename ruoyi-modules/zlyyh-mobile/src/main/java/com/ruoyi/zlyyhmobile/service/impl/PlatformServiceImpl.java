package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.Area;
import com.ruoyi.zlyyh.domain.PlatformChannel;
import com.ruoyi.zlyyh.domain.vo.AreaVo;
import com.ruoyi.zlyyh.domain.vo.PlatformChannelVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.mapper.AreaMapper;
import com.ruoyi.zlyyh.mapper.PlatformChannelMapper;
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
    private final PlatformChannelMapper platformChannelMapper;
    private final AreaMapper areaMapper;

    /**
     * 查询平台信息
     */
    @Cacheable(cacheNames = CacheNames.PLATFORM, key = "#platformKey + '-' + #platformEnumd.getPlatformType()")
    @Override
    public PlatformVo queryById(Long platformKey, PlatformEnumd platformEnumd) {
        PlatformVo platformVo = baseMapper.selectVoById(platformKey);
        if (null != platformVo) {
            try {
                this.setPlatformChannelInfo(platformVo, platformEnumd);
            } catch (Exception e) {
                return null;
            }
            if (StringUtils.isNotBlank(platformVo.getPlatformCity()) && !"ALL".equalsIgnoreCase(platformVo.getPlatformCity())) {
                List<AreaVo> areaVos = areaMapper.selectVoList(new LambdaQueryWrapper<Area>().eq(Area::getLevel, "city").in(Area::getAdcode, platformVo.getPlatformCity().split(",")));
                platformVo.setPlatformCityList(areaVos);
            }
        }
        return platformVo;
    }

    private void setPlatformChannelInfo(PlatformVo platformVo, PlatformEnumd platformEnumd) {
        LambdaQueryWrapper<PlatformChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(PlatformChannel::getPlatformKey, platformVo.getPlatformKey());
        lqw.eq(PlatformChannel::getChannel, platformEnumd.getChannel());
        PlatformChannelVo platformChannelVo = platformChannelMapper.selectVoOne(lqw);
        if (null == platformChannelVo) {
            throw new ServiceException("渠道配置错误,请联系客服处理。");
        }
        platformVo.setPlatformTitle(platformChannelVo.getPlatformTitle());
        platformVo.setAppId(platformChannelVo.getAppId());
        platformVo.setEncryptAppId(platformChannelVo.getEncryptAppId());
        platformVo.setSecret(platformChannelVo.getSecret());
        platformVo.setSymmetricKey(platformChannelVo.getSymmetricKey());
        platformVo.setRsaPrivateKey(platformChannelVo.getRsaPrivateKey());
        platformVo.setRsaPublicKey(platformChannelVo.getRsaPublicKey());
        platformVo.setMerchantId(platformChannelVo.getMerchantId());
    }
}
