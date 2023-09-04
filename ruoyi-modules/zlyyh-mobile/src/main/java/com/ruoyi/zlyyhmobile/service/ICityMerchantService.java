package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.CityMerchantVo;

/**
 * 城市商户Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface ICityMerchantService {

    /**
     * 查询城市商户列表
     */
    CityMerchantVo queryOneByCityCode(String cityCode,Long platformKey);

}
