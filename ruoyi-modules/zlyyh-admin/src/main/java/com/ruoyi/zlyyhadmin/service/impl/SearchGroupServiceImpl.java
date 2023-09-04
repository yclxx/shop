package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.ISearchGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.SearchGroupBo;
import com.ruoyi.zlyyh.domain.vo.SearchGroupVo;
import com.ruoyi.zlyyh.domain.SearchGroup;
import com.ruoyi.zlyyh.mapper.SearchGroupMapper;


import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 搜索彩蛋配置Service业务层处理
 *
 * @author yzg
 * @date 2023-07-24
 */
@RequiredArgsConstructor
@Service
public class SearchGroupServiceImpl implements ISearchGroupService {

    private final SearchGroupMapper baseMapper;

    /**
     * 查询搜索彩蛋配置
     */
    @Override
    public SearchGroupVo queryById(Long searchId){
        return baseMapper.selectVoById(searchId);
    }

    /**
     * 查询搜索彩蛋配置列表
     */
    @Override
    public TableDataInfo<SearchGroupVo> queryPageList(SearchGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SearchGroup> lqw = buildQueryWrapper(bo);
        Page<SearchGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询搜索彩蛋配置列表
     */
    @Override
    public List<SearchGroupVo> queryList(SearchGroupBo bo) {
        LambdaQueryWrapper<SearchGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SearchGroup> buildQueryWrapper(SearchGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SearchGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getSearchContent()), SearchGroup::getSearchContent, bo.getSearchContent());
        lqw.eq(bo.getProductId() != null, SearchGroup::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getToType()), SearchGroup::getToType, bo.getToType());
        lqw.eq(StringUtils.isNotBlank(bo.getAppId()), SearchGroup::getAppId, bo.getAppId());
        lqw.eq(StringUtils.isNotBlank(bo.getUrl()), SearchGroup::getUrl, bo.getUrl());
        lqw.eq(bo.getStartTime() != null, SearchGroup::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, SearchGroup::getEndTime, bo.getEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getShowCity()), SearchGroup::getShowCity, bo.getShowCity());
        lqw.eq(StringUtils.isNotBlank(bo.getAssignDate()), SearchGroup::getAssignDate, bo.getAssignDate());
        lqw.eq(StringUtils.isNotBlank(bo.getWeekDate()), SearchGroup::getWeekDate, bo.getWeekDate());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SearchGroup::getStatus, bo.getStatus());
        lqw.eq(bo.getPlatformKey() != null, SearchGroup::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }

    /**
     * 新增搜索彩蛋配置
     */
    @Override
    public Boolean insertByBo(SearchGroupBo bo) {
        SearchGroup add = BeanUtil.toBean(bo, SearchGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setSearchId(add.getSearchId());
        }
        return flag;
    }

    /**
     * 修改搜索彩蛋配置
     */
    @Override
    public Boolean updateByBo(SearchGroupBo bo) {
        SearchGroup update = BeanUtil.toBean(bo, SearchGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SearchGroup entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除搜索彩蛋配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
