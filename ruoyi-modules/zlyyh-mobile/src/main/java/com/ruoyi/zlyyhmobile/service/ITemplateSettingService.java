package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.TemplateSettingVo;

import java.util.List;

/**
 * 落地页配置Service接口
 *
 * @author yzg
 * @date 2023-06-09
 */
public interface ITemplateSettingService {
    /**
     * 查询落地页配置列表
     */
    List<TemplateSettingVo> queryListByTemplateId(Long templateId);
}
