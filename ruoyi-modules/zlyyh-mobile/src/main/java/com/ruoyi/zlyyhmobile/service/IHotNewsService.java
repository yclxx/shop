package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.HotNewsBo;
import com.ruoyi.zlyyh.domain.vo.HotNewsVo;

import java.util.List;

/**
 * 热门搜索Service接口
 */
public interface IHotNewsService {

    /**
     * 查询热门搜索列表
     */
    List<HotNewsVo> queryList(HotNewsBo bo);
}
