package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.TemplatePageVo;

/**
 * 落地页Service接口
 *
 * @author yzg
 * @date 2023-06-09
 */
public interface ITemplatePageService {

    /**
     * 查询落地页
     */
    TemplatePageVo queryById(Long templateId);
}
