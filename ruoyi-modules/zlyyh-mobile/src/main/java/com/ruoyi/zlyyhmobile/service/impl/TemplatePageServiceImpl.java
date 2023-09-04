package com.ruoyi.zlyyhmobile.service.impl;

import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.vo.TemplatePageVo;
import com.ruoyi.zlyyh.mapper.TemplatePageMapper;
import com.ruoyi.zlyyhmobile.service.ITemplatePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
    @Cacheable(cacheNames = CacheNames.TEMPLATE_PAGE, key = "#templateId")
    @Override
    public TemplatePageVo queryById(Long templateId){
        return baseMapper.selectVoById(templateId);
    }
}
