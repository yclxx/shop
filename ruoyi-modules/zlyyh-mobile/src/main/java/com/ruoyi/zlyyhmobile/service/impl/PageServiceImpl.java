package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.Page;
import com.ruoyi.zlyyh.domain.vo.PageVo;
import com.ruoyi.zlyyh.mapper.PageMapper;
import com.ruoyi.zlyyhmobile.service.IPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 页面Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class PageServiceImpl implements IPageService {

    private final PageMapper baseMapper;

    /**
     * 查询页面
     *
     * @param pagePath 页面标识
     */
    @Cacheable(cacheNames = CacheNames.PAGE, key = "#platformKey+'-'+#pagePath")
    @Override
    public PageVo queryByPagePath(String pagePath,Long platformKey) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<Page>().eq(Page::getPagePath, pagePath).eq(Page::getPlatformKey, platformKey));
    }
}
