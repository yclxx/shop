package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.PlatformVo;

/**
 * 平台信息Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IPlatformService {

    /**
     * 查询平台信息
     * @param platformKey 平台key
     * @return 平台信息
     */
    PlatformVo queryById(Long platformKey);
}
