package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.TemplateSetting;
import com.ruoyi.zlyyh.domain.vo.TemplateSettingVo;
import com.ruoyi.zlyyh.domain.bo.TemplateSettingBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 落地页配置Service接口
 *
 * @author yzg
 * @date 2023-06-09
 */
public interface ITemplateSettingService {

    /**
     * 查询落地页配置
     */
    TemplateSettingVo queryById(Long id);

    /**
     * 查询落地页配置列表
     */
    TableDataInfo<TemplateSettingVo> queryPageList(TemplateSettingBo bo, PageQuery pageQuery);

    /**
     * 查询落地页配置列表
     */
    List<TemplateSettingVo> queryList(TemplateSettingBo bo);

    /**
     * 修改落地页配置
     */
    Boolean insertByBo(TemplateSettingBo bo);

    /**
     * 修改落地页配置
     */
    Boolean updateByBo(TemplateSettingBo bo);

    /**
     * 校验并批量删除落地页配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
