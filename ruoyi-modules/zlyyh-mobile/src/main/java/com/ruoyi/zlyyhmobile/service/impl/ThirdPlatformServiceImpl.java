package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zlyyh.domain.ThirdPlatform;
import com.ruoyi.zlyyh.domain.vo.ThirdPlatformVo;
import com.ruoyi.zlyyh.mapper.ThirdPlatformMapper;
import com.ruoyi.zlyyhmobile.service.IThirdPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ThirdPlatformServiceImpl implements IThirdPlatformService {

    private final ThirdPlatformMapper baseMapper;

    /**
     * 查询
     */
    @Override
    public ThirdPlatformVo selectByAppId(String appId, String type) {
        LambdaQueryWrapper<ThirdPlatform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThirdPlatform::getAppId,appId);
        wrapper.eq(ThirdPlatform::getType,type);
        wrapper.last("limit 1");
        return baseMapper.selectVoOne(wrapper);
    }
}
