package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.TemplateSetting;
import com.ruoyi.zlyyh.domain.bo.TemplateSettingBo;
import com.ruoyi.zlyyh.domain.vo.TemplateSettingVo;
import com.ruoyi.zlyyh.mapper.TemplateSettingMapper;
import com.ruoyi.zlyyhadmin.service.ITemplateSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 落地页配置Service业务层处理
 *
 * @author yzg
 * @date 2023-06-09
 */
@RequiredArgsConstructor
@Service
public class TemplateSettingServiceImpl implements ITemplateSettingService {

    private final TemplateSettingMapper baseMapper;

    /**
     * 查询落地页配置
     */
    @Override
    public TemplateSettingVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询落地页配置列表
     */
    @Override
    public TableDataInfo<TemplateSettingVo> queryPageList(TemplateSettingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TemplateSetting> lqw = buildQueryWrapper(bo);
        Page<TemplateSettingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询落地页配置列表
     */
    @Override
    public List<TemplateSettingVo> queryList(TemplateSettingBo bo) {
        LambdaQueryWrapper<TemplateSetting> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<TemplateSetting> buildQueryWrapper(TemplateSettingBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<TemplateSetting> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getTemplateId() != null, TemplateSetting::getTemplateId, bo.getTemplateId());
        return lqw;
    }

    /**
     * 新增落地页配置
     */
    @CacheEvict(cacheNames = CacheNames.TEMPLATE_SETTING_LIST, key = "#bo.getTemplateId()")
    @Override
    public Boolean insertByBo(TemplateSettingBo bo) {
        TemplateSetting add = BeanUtil.toBean(bo, TemplateSetting.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改落地页配置
     */
    @CacheEvict(cacheNames = CacheNames.TEMPLATE_SETTING_LIST, key = "#bo.getTemplateId()")
    @Override
    public Boolean updateByBo(TemplateSettingBo bo) {
        TemplateSetting update = BeanUtil.toBean(bo, TemplateSetting.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(TemplateSetting entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除落地页配置
     */
    @CacheEvict(cacheNames = CacheNames.TEMPLATE_SETTING_LIST, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
