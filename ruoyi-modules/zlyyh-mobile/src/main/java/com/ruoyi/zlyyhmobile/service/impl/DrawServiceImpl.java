package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.Draw;
import com.ruoyi.zlyyh.domain.bo.DrawBo;
import com.ruoyi.zlyyh.domain.vo.DrawVo;
import com.ruoyi.zlyyh.mapper.DrawMapper;
import com.ruoyi.zlyyhmobile.service.IDrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 奖品管理Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@RequiredArgsConstructor
@Service
public class DrawServiceImpl implements IDrawService {

    private final DrawMapper baseMapper;

    /**
     * 查询奖品管理
     */
    @Override
    public DrawVo queryById(Long drawId) {
        return baseMapper.selectVoById(drawId);
    }

    /**
     * 查询奖品管理列表
     */
    @Cacheable(cacheNames = CacheNames.MISSION_GROUP_DRAW, key = "#bo.getPlatformKey() + '-' + #bo.getMissionGroupId()")
    @Override
    public List<DrawVo> queryList(DrawBo bo) {
        LambdaQueryWrapper<Draw> lqw = Wrappers.lambdaQuery();
        lqw.eq(null != bo.getMissionGroupId(), Draw::getMissionGroupId, bo.getMissionGroupId());
        lqw.eq(null != bo.getPlatformKey(), Draw::getPlatformKey, bo.getPlatformKey());
        lqw.eq(null != bo.getDrawWinning(), Draw::getDrawWinning, bo.getDrawWinning());
        lqw.eq(Draw::getStatus, "0");
        lqw.last("order by sort asc");
        return baseMapper.selectVoList(lqw);
    }
}
