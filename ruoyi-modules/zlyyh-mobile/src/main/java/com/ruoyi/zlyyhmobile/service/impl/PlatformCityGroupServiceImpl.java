package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.PlatformCityGroup;
import com.ruoyi.zlyyh.domain.bo.PlatformCityGroupBo;
import com.ruoyi.zlyyh.domain.vo.PlatformCityGroupEntity;
import com.ruoyi.zlyyh.domain.vo.PlatformCityGroupVo;
import com.ruoyi.zlyyh.mapper.PlatformCityGroupMapper;
import com.ruoyi.zlyyhmobile.service.IPlatformCityGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信二维码Service业务层处理
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@Service
public class PlatformCityGroupServiceImpl implements IPlatformCityGroupService {

    private final PlatformCityGroupMapper baseMapper;

    /**
     * 查询列表
     */
    @Override
    public List<PlatformCityGroupEntity> queryList(PlatformCityGroupBo bo) {
        LambdaQueryWrapper<PlatformCityGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()),PlatformCityGroup::getStatus,bo.getStatus());
        lqw.eq(bo.getPlatformKey() != null,PlatformCityGroup::getPlatformKey,bo.getPlatformKey());
        List<PlatformCityGroupVo> platformCityGroupVoList = baseMapper.selectVoList(lqw);
        List<PlatformCityGroupEntity> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(platformCityGroupVoList)){
            PlatformCityGroupEntity platformCityGroupEntity = null;
            for (PlatformCityGroupVo platformCityGroupVo : platformCityGroupVoList) {
                platformCityGroupEntity = new PlatformCityGroupEntity();
                platformCityGroupEntity.setId(platformCityGroupVo.getId());
                platformCityGroupEntity.setName(platformCityGroupVo.getCityName());
                platformCityGroupEntity.setStatus(platformCityGroupVo.getStatus());
                platformCityGroupEntity.setPlatformKey(platformCityGroupVo.getPlatformKey());
                platformCityGroupEntity.setCityCode(platformCityGroupVo.getCityCode());
                platformCityGroupEntity.setGroupImages(platformCityGroupVo.getGroupImages());
                list.add(platformCityGroupEntity);
            }
        }
        return list;
    }
}
