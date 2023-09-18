package com.ruoyi.zlyyhadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.LianlianCity;
import com.ruoyi.zlyyh.domain.bo.LianlianCityBo;
import com.ruoyi.zlyyh.domain.vo.LianlianCityVo;

import java.util.Collection;
import java.util.List;

/**
 * 联联市级城市Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface ILianlianCityService {

    /**
     * 查询联联市级城市
     */
    LianlianCityVo queryById(Long cityId);

    Page<LianlianCity> selectLlianCityCodeList(String status, Integer pageNum, Integer pageSize);

    /**
     * 查询联联市级城市列表
     */
    TableDataInfo<LianlianCityVo> queryPageList(LianlianCityBo bo, PageQuery pageQuery);

    /**
     * 查询联联市级城市列表
     */
    List<LianlianCityVo> queryList(LianlianCityBo bo);

    /**
     * 修改联联市级城市
     */
    Boolean insertByBo(LianlianCityBo bo);

    /**
     * 修改联联市级城市
     */
    Boolean updateByBo(LianlianCityBo bo);

    /**
     * 校验并批量删除联联市级城市信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
