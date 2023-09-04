package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.PlatformCityIndex;
import com.ruoyi.zlyyh.domain.vo.PlatformCityIndexVo;
import com.ruoyi.zlyyh.domain.bo.PlatformCityIndexBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
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
    PlatformCityIndexVo queryById(Long id);

    /**
     * 查询自定义首页列表
     */
    TableDataInfo<PlatformCityIndexVo> queryPageList(PlatformCityIndexBo bo, PageQuery pageQuery);

    /**
     * 查询自定义首页列表
     */
    List<PlatformCityIndexVo> queryList(PlatformCityIndexBo bo);

    /**
     * 修改自定义首页
     */
    Boolean insertByBo(PlatformCityIndexBo bo);

    /**
     * 修改自定义首页
     */
    Boolean updateByBo(PlatformCityIndexBo bo);

    /**
     * 校验并批量删除自定义首页信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
