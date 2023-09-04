package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.TemplateSetting;
import com.ruoyi.zlyyh.domain.vo.TemplateSettingVo;
import com.ruoyi.zlyyh.mapper.TemplateSettingMapper;
import com.ruoyi.zlyyhmobile.service.ITemplateSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * 查询落地页配置列表
     */
    @Cacheable(cacheNames = CacheNames.TEMPLATE_SETTING_LIST, key = "#templateId")
    @Override
    public List<TemplateSettingVo> queryListByTemplateId(Long templateId) {
        LambdaQueryWrapper<TemplateSetting> lqw = Wrappers.lambdaQuery();
        lqw.eq(TemplateSetting::getTemplateId, templateId);
        lqw.last("order by sort asc");
        return baseMapper.selectVoList(lqw);
    }
}
