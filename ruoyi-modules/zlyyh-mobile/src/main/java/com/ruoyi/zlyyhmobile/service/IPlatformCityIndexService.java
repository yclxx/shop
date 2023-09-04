package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.PlatformCityIndexVo;

import java.util.List;

/**
 * 自定义首页Service接口
 *
 * @author yzg
 * @date 2023-08-07
 */
public interface IPlatformCityIndexService {

    /**
     * 查询自定义首页
     */
    List<PlatformCityIndexVo> queryByCityCode(String cityCode, Long platformKey);
}
