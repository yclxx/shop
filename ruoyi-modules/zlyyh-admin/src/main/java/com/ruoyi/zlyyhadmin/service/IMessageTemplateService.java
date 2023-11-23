package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.MessageTemplateBo;
import com.ruoyi.zlyyh.domain.vo.MessageTemplateVo;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 消息模板Service接口
 *
 * @author yzg
 * @date 2023-11-23
 */
public interface IMessageTemplateService {

    /**
     * 查询消息模板
     */
    MessageTemplateVo queryById(Long templateId);

    /**
     * 查询消息模板列表
     */
    TableDataInfo<MessageTemplateVo> queryPageList(MessageTemplateBo bo, PageQuery pageQuery);

    /**
     * 查询消息模板列表
     */
    List<MessageTemplateVo> queryList(MessageTemplateBo bo);

    /**
     * 修改消息模板
     */
    Boolean insertByBo(MessageTemplateBo bo);

    /**
     * 修改消息模板
     */
    Boolean updateByBo(MessageTemplateBo bo);

    /**
     * 校验并批量删除消息模板信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    void importData(MessageTemplateBo bo) throws IOException;
}
