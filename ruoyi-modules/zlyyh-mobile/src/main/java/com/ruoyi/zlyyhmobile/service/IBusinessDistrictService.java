package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.BusinessDistrictBo;
import com.ruoyi.zlyyh.domain.vo.BusinessDistrictVo;

import java.util.List;

/**
 * 商圈Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface IBusinessDistrictService {

    /**
     * 查询商圈列表
     */
    List<BusinessDistrictVo> queryList(BusinessDistrictBo bo);

    void insertShopBusinessDistrict(List<Long> businessDistrictIds, Long shopId);
}
