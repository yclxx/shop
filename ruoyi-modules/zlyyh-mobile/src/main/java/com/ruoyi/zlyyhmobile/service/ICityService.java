package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.CityBo;
import com.ruoyi.zlyyh.domain.vo.CityVo;

import java.util.Collection;
import java.util.List;

/**
 * 行政区Service接口
 *
 * @author yzg
 * @date 2024-01-12
 */
public interface ICityService {

    /**
     * 查询行政区
     */
    CityVo queryById(Long adcode);


    /**
     * 查询行政区列表
     */
    List<CityVo> queryList(CityBo bo);

    /**
     * 修改行政区
     */
    Boolean insertByBo(CityBo bo);

    /**
     * 修改行政区
     */
    Boolean updateByBo(CityBo bo);

    /**
     * 校验并批量删除行政区信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    void getDistrict(String adcode);
}
