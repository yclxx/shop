package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.HotNews;
import com.ruoyi.zlyyh.domain.bo.HotNewsBo;
import com.ruoyi.zlyyh.domain.vo.HotNewsVo;
import com.ruoyi.zlyyh.mapper.HotNewsMapper;
import com.ruoyi.zlyyhmobile.service.IHotNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 热门搜索Service业务层处理
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class HotNewsServiceImpl implements IHotNewsService {

    private final HotNewsMapper baseMapper;

    /**
     * 查询热门搜索
     */
    @Cacheable(cacheNames = CacheNames.hotNews, key = "#bo.getPlatformKey()")
    @Override
    public List<HotNewsVo> queryList(HotNewsBo bo) {
        LambdaQueryWrapper<HotNews> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, HotNews::getPlatformKey, bo.getPlatformKey());
        lqw.eq(HotNews::getStatus, "0");
        lqw.and(lm -> {
            lm.isNull(HotNews::getStartTime).or().lt(HotNews::getStartTime, new Date());
        });
        lqw.and(lm -> {
            lm.isNull(HotNews::getEndTime).or().gt(HotNews::getEndTime, new Date());
        });
        lqw.last("order by news_rank asc");
        return baseMapper.selectVoList(lqw);
    }
}
