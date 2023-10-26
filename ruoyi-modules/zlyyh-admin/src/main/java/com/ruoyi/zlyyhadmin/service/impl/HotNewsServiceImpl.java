package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.HotNews;
import com.ruoyi.zlyyh.domain.bo.HotNewsBo;
import com.ruoyi.zlyyh.domain.vo.HotNewsVo;
import com.ruoyi.zlyyh.mapper.HotNewsMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IHotNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 热门搜索配置Service业务层处理
 *
 * @author yzg
 * @date 2023-07-21
 */
@RequiredArgsConstructor
@Service
public class HotNewsServiceImpl implements IHotNewsService {

    private final HotNewsMapper baseMapper;

    /**
     * 查询热门搜索配置
     */
    @Override
    public HotNewsVo queryById(Long newsId) {
        return baseMapper.selectVoById(newsId);
    }

    /**
     * 查询热门搜索配置列表
     */
    @Override
    public TableDataInfo<HotNewsVo> queryPageList(HotNewsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HotNews> lqw = buildQueryWrapper(bo);
        Page<HotNewsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询热门搜索配置列表
     */
    @Override
    public List<HotNewsVo> queryList(HotNewsBo bo) {
        LambdaQueryWrapper<HotNews> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<HotNews> buildQueryWrapper(HotNewsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<HotNews> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getNewsName()), HotNews::getNewsName, bo.getNewsName());
        lqw.eq(bo.getPlatformKey() != null, HotNews::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), HotNews::getStatus, bo.getStatus());
        lqw.eq(bo.getStartTime() != null, HotNews::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, HotNews::getEndTime, bo.getEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getAssignDate()), HotNews::getAssignDate, bo.getAssignDate());
        lqw.eq(StringUtils.isNotBlank(bo.getShowCity()), HotNews::getShowCity, bo.getShowCity());
        return lqw;
    }

    /**
     * 新增热门搜索配置
     */
    @CacheEvict(cacheNames = CacheNames.hotNews, key = "#bo.getPlatformKey()+'-'+#bo.getSupportChannel()")
    @Override
    public Boolean insertByBo(HotNewsBo bo) {
        HotNews add = BeanUtil.toBean(bo, HotNews.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setNewsId(add.getNewsId());
        }
        return flag;
    }

    /**
     * 修改热门搜索配置
     */
    @CacheEvict(cacheNames = CacheNames.hotNews, key = "#bo.getPlatformKey()+'-'+#bo.getSupportChannel()")
    @Override
    public Boolean updateByBo(HotNewsBo bo) {
        HotNews update = BeanUtil.toBean(bo, HotNews.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(HotNews entity) {
        PermissionUtils.setPlatformDeptIdAndUserId(entity, entity.getPlatformKey(), null == entity.getNewsId(), false);
    }

    /**
     * 批量删除热门搜索配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
