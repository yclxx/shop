package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Page;
import com.ruoyi.zlyyh.domain.bo.PageBo;
import com.ruoyi.zlyyh.domain.vo.PageVo;
import com.ruoyi.zlyyh.mapper.PageMapper;
import com.ruoyi.zlyyhadmin.service.IPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     */
    @Override
    public PageVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询页面列表
     */
    @Override
    public TableDataInfo<PageVo> queryPageList(PageBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Page> lqw = buildQueryWrapper(bo);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PageVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询页面列表
     */
    @Override
    public List<PageVo> queryList(PageBo bo) {
        LambdaQueryWrapper<Page> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Page> buildQueryWrapper(PageBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Page> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getPagePath()), Page::getPagePath, bo.getPagePath());
        lqw.like(StringUtils.isNotBlank(bo.getPageName()), Page::getPageName, bo.getPageName());
        lqw.like(StringUtils.isNotBlank(bo.getPageRemake()), Page::getPageRemake, bo.getPageRemake());
        lqw.eq(StringUtils.isNotBlank(bo.getNavBackgroundColor()), Page::getNavBackgroundColor, bo.getNavBackgroundColor());
        lqw.eq(StringUtils.isNotBlank(bo.getAppletStyle()), Page::getAppletStyle, bo.getAppletStyle());
        lqw.eq(StringUtils.isNotBlank(bo.getAppletTitleBarVisible()), Page::getAppletTitleBarVisible, bo.getAppletTitleBarVisible());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Page::getStatus, bo.getStatus());
        lqw.eq(null != bo.getPlatformKey(), Page::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }

    /**
     * 新增页面
     */
    @CacheEvict(cacheNames = CacheNames.PAGE, key = "#bo.getPlatformKey()+'-'+#bo.pagePath")
    @Override
    public Boolean insertByBo(PageBo bo) {
        Page add = BeanUtil.toBean(bo, Page.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改页面
     */
    @CacheEvict(cacheNames = CacheNames.PAGE, key = "#bo.getPlatformKey()+'-'+#bo.pagePath")
    @Override
    public Boolean updateByBo(PageBo bo) {
        Page update = BeanUtil.toBean(bo, Page.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Page entity) {
        if (StringUtils.isNotBlank(entity.getNavBackgroundColor())) {
            entity.setNavBackgroundColor(entity.getNavBackgroundColor().replace("#", "0xff"));
        }
    }

    /**
     * 批量删除页面
     */
    @CacheEvict(cacheNames = CacheNames.PAGE, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
