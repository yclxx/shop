package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.PageVo;

/**
 * 页面Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IPageService {

    /**
     * 查询页面
     *
     * @param pagePath 页面标识
     * @return 页面标题配置
     */
    PageVo queryByPagePath(String pagePath,Long platformKey);

}
