package com.ruoyi.zlyyhadmin.service;

import cn.hutool.core.lang.tree.Tree;
import com.ruoyi.zlyyh.domain.Area;
import com.ruoyi.zlyyh.domain.vo.AreaVo;
import com.ruoyi.zlyyh.domain.bo.AreaBo;

import java.util.Collection;
import java.util.List;

/**
 * 行政区Service接口
 *
 * @author ruoyi
 * @date 2023-03-27
 */
public interface IAreaService {

    /**
     * 查询行政区
     */
    AreaVo queryById(Long id);

    /**
     * 查询行政区列表
     */
    List<AreaVo> queryList(AreaBo bo);

    /**
     * 修改行政区
     */
    Boolean insertByBo(AreaBo bo);

    /**
     * 修改行政区
     */
    Boolean updateByBo(AreaBo bo);

    /**
     * 校验并批量删除行政区信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 查询列表
     */
    List<Area> selectCityList(Area area);

    /**
     * 查询行政下拉树
     *
     * @return
     */
    List<Tree<Long>> buildCityTreeSelect(List<Area> areaList);

    /**
     * 查询
     */
    List<Long> selectPlatformCityByPlatform(Long platformId);

    List<Long> selectPlatformCityByBannerId(Long bannerId);

    List<Long> selectPlatformCityByNewsId(Long bannerId);

    List<Long> selectPlatformCityByProductId(Long productId);

    List<Long> selectPlatformCityBySearchId(Long searchId);

    List<Long> getCityIds(String showCity);
}
