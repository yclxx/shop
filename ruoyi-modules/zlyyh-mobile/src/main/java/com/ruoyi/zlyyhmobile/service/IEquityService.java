package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.EquityVo;

/**
 * 权益包Service接口
 *
 * @author yzg
 * @date 2023-06-06
 */
public interface IEquityService {

    /**
     * 查询权益包
     */
    EquityVo queryById(Long equityId);

    /**
     * 查询权益包列表
     */
    EquityVo queryByPlatformId(Long platformId);
}
