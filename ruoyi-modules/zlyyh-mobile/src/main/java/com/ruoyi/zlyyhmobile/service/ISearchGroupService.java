package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.SearchGroup;
import com.ruoyi.zlyyh.domain.vo.SearchGroupVo;
import com.ruoyi.zlyyh.domain.bo.SearchGroupBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 搜索彩蛋配置Service接口
 *
 * @author yzg
 * @date 2023-07-24
 */
public interface ISearchGroupService {

    /**
     * 查询搜索彩蛋配置
     */
    SearchGroupVo queryById(Long searchId);

    /**
     * 查询搜索彩蛋配置列表
     */
    TableDataInfo<SearchGroupVo> queryPageList(SearchGroupBo bo, PageQuery pageQuery);


    SearchGroupVo queryByContent(SearchGroupBo searchGroupBo);
}
