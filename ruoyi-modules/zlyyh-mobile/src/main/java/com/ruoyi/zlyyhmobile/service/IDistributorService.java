package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.DistributorVo;

/**
 * 分销商信息Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface IDistributorService {

    /**
     * 查询分销商信息
     */
    DistributorVo queryById(String distributorId);
}
