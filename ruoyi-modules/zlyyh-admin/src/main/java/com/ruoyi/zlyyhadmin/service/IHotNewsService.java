package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.HotNewsBo;
import com.ruoyi.zlyyh.domain.vo.HotNewsVo;

import java.util.Collection;
import java.util.List;

/**
 * 热门搜索配置Service接口
 *
 * @author yzg
 * @date 2023-07-21
 */
public interface IHotNewsService {

    /**
     * 查询热门搜索配置
     */
    HotNewsVo queryById(Long newsId);

    /**
     * 查询热门搜索配置列表
     */
    TableDataInfo<HotNewsVo> queryPageList(HotNewsBo bo, PageQuery pageQuery);

    /**
     * 查询热门搜索配置列表
     */
    List<HotNewsVo> queryList(HotNewsBo bo);

    /**
     * 修改热门搜索配置
     */
    Boolean insertByBo(HotNewsBo bo);

    /**
     * 修改热门搜索配置
     */
    Boolean updateByBo(HotNewsBo bo);

    /**
     * 校验并批量删除热门搜索配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
