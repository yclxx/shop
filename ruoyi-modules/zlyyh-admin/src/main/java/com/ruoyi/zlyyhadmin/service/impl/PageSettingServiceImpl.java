package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.PageBlock;
import com.ruoyi.zlyyh.domain.PageSetting;
import com.ruoyi.zlyyh.domain.bo.PageSettingBo;
import com.ruoyi.zlyyh.domain.vo.PageSettingVo;
import com.ruoyi.zlyyh.mapper.PageBlockMapper;
import com.ruoyi.zlyyh.mapper.PageSettingMapper;
import com.ruoyi.zlyyhadmin.service.IPageSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 页面配置Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class PageSettingServiceImpl implements IPageSettingService {

    private final PageSettingMapper baseMapper;
    private final PageBlockMapper pageBlockMapper;

    /**
     * 查询页面配置
     */
    @Override
    public PageSettingVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询页面配置列表
     */
    @Override
    public TableDataInfo<PageSettingVo> queryPageList(PageSettingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PageSetting> lqw = buildQueryWrapper(bo);
        Page<PageSettingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询页面配置列表
     */
    @Override
    public List<PageSettingVo> queryList(PageSettingBo bo) {
        LambdaQueryWrapper<PageSetting> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PageSetting> buildQueryWrapper(PageSettingBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PageSetting> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getPagePath()), PageSetting::getPagePath, bo.getPagePath());
        lqw.eq(StringUtils.isNotBlank(bo.getBannerType()), PageSetting::getBannerType, bo.getBannerType());
        lqw.eq(bo.getBlockId() != null, PageSetting::getBlockId, bo.getBlockId());
        lqw.eq(StringUtils.isNotBlank(bo.getBlockColumnValue()), PageSetting::getBlockColumnValue, bo.getBlockColumnValue());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), PageSetting::getStatus, bo.getStatus());
        lqw.eq(bo.getSort() != null, PageSetting::getSort, bo.getSort());
        lqw.eq(bo.getPlatformKey() != null, PageSetting::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }

    /**
     * 新增页面配置
     */
    @CacheEvict(cacheNames = CacheNames.PAGE_SETTING, key = "#bo.getPlatformKey()+'-'+#bo.pagePath")
    @Override
    public Boolean insertByBo(PageSettingBo bo) {
        PageSetting add = BeanUtil.toBean(bo, PageSetting.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改页面配置
     */
    @CacheEvict(cacheNames = CacheNames.PAGE_SETTING, key = "#bo.getPlatformKey()+'-'+#bo.pagePath")
    @Override
    public Boolean updateByBo(PageSettingBo bo) {
        PageSetting update = BeanUtil.toBean(bo, PageSetting.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PageSetting entity){
        if(null != entity.getBlockId()){
            PageBlock pageBlock = pageBlockMapper.selectById(entity.getBlockId());
            if(null != pageBlock){
                entity.setMainField(pageBlock.getMainField());
            }
        }
    }

    /**
     * 批量删除页面配置
     */
    @CacheEvict(cacheNames = CacheNames.PAGE_SETTING,allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
