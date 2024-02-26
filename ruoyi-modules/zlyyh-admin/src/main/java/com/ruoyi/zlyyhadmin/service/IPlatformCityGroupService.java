package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.PlatformCityGroup;
import com.ruoyi.zlyyh.domain.vo.PlatformCityGroupVo;
import com.ruoyi.zlyyh.domain.bo.PlatformCityGroupBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 平台城市企业微信群Service接口
 *
 * @author yzg
 * @date 2024-02-21
 */
public interface IPlatformCityGroupService {

    /**
     * 查询平台城市企业微信群
     */
    PlatformCityGroupVo queryById(Long id);

    /**
     * 查询平台城市企业微信群列表
     */
    TableDataInfo<PlatformCityGroupVo> queryPageList(PlatformCityGroupBo bo, PageQuery pageQuery);

    /**
     * 查询平台城市企业微信群列表
     */
    List<PlatformCityGroupVo> queryList(PlatformCityGroupBo bo);

    /**
     * 修改平台城市企业微信群
     */
    Boolean insertByBo(PlatformCityGroupBo bo);

    /**
     * 修改平台城市企业微信群
     */
    Boolean updateByBo(PlatformCityGroupBo bo);

    /**
     * 校验并批量删除平台城市企业微信群信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
