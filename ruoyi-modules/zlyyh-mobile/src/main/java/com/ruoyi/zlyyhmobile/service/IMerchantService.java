package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.MerchantVo;

/**
 * 商户号Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IMerchantService {

    /**
     * 查询商户号
     */
    MerchantVo queryById(Long id);

}
