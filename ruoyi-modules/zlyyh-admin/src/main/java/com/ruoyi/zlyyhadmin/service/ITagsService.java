package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.Tags;
import com.ruoyi.zlyyh.domain.vo.TagsVo;
import com.ruoyi.zlyyh.domain.bo.TagsBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 标签Service接口
 *
 * @author yzg
 * @date 2023-10-09
 */
public interface ITagsService {

    /**
     * 查询标签
     */
    TagsVo queryById(Long tagsId);

    /**
     * 查询标签列表
     */
    TableDataInfo<TagsVo> queryPageList(TagsBo bo, PageQuery pageQuery);

    /**
     * 查询标签列表
     */
    List<TagsVo> queryList(TagsBo bo);

    /**
     * 修改标签
     */
    Boolean insertByBo(TagsBo bo);

    /**
     * 修改标签
     */
    Boolean updateByBo(TagsBo bo);

    /**
     * 校验并批量删除标签信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
