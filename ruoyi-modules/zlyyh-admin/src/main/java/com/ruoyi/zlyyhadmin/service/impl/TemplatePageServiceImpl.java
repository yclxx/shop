package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.zlyyh.domain.TemplatePage;
import com.ruoyi.zlyyh.domain.bo.TemplatePageBo;
import com.ruoyi.zlyyh.domain.vo.TemplatePageVo;
import com.ruoyi.zlyyh.mapper.TemplatePageMapper;
import com.ruoyi.zlyyhadmin.service.ITemplatePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 落地页Service业务层处理
 *
 * @author yzg
 * @date 2023-06-09
 */
@RequiredArgsConstructor
@Service
public class TemplatePageServiceImpl implements ITemplatePageService {

    private final TemplatePageMapper baseMapper;

    /**
     * 查询落地页
     */
    @Override
    public TemplatePageVo queryById(Long templateId) {
        return baseMapper.selectVoById(templateId);
    }

    /**
     * 查询落地页列表
     */
    @Override
    public TableDataInfo<TemplatePageVo> queryPageList(TemplatePageBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TemplatePage> lqw = buildQueryWrapper(bo);
        Page<TemplatePageVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询落地页列表
     */
    @Override
    public List<TemplatePageVo> queryList(TemplatePageBo bo) {
        LambdaQueryWrapper<TemplatePage> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<TemplatePage> buildQueryWrapper(TemplatePageBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<TemplatePage> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTemplateName()), TemplatePage::getTemplateName, bo.getTemplateName());
        lqw.eq(StringUtils.isNotBlank(bo.getShowTitle()), TemplatePage::getShowTitle, bo.getShowTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getPageTitle()), TemplatePage::getPageTitle, bo.getPageTitle());
        return lqw;
    }

    /**
     * 新增落地页
     */
    @Override
    public Boolean insertByBo(TemplatePageBo bo) {
        TemplatePage add = BeanUtil.toBean(bo, TemplatePage.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTemplateId(add.getTemplateId());
        }
        return flag;
    }

    /**
     * 修改落地页
     */
    @CacheEvict(cacheNames = CacheNames.TEMPLATE_PAGE, key = "#bo.getTemplateId()")
    @Override
    public Boolean updateByBo(TemplatePageBo bo) {
        TemplatePage update = BeanUtil.toBean(bo, TemplatePage.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(TemplatePage entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除落地页
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        for (Long id : ids) {
            CacheUtils.evict(CacheNames.TEMPLATE_PAGE, id);
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
