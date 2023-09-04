package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.Equity;
import com.ruoyi.zlyyh.domain.vo.EquityVo;
import com.ruoyi.zlyyh.mapper.EquityMapper;
import com.ruoyi.zlyyhmobile.service.IEquityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 权益包Service业务层处理
 *
 * @author yzg
 * @date 2023-06-06
 */
@RequiredArgsConstructor
@Service
public class EquityServiceImpl implements IEquityService {

    private final EquityMapper baseMapper;

    /**
     * 查询权益包
     */
    @Override
    public EquityVo queryById(Long equityId) {
        return baseMapper.selectVoById(equityId);
    }

    /**
     * 查询权益包列表
     */
    @Override
    public EquityVo queryByPlatformId(Long platformId) {
        LambdaQueryWrapper<Equity> lqw = Wrappers.lambdaQuery();
        lqw.eq(Equity::getStatus, "0");
        lqw.eq(Equity::getPlatformKey, platformId);
        lqw.last("limit 1");
        return baseMapper.selectVoOne(lqw);
    }
}
