package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.PlatformBo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;

import java.util.Collection;
import java.util.List;

/**
 * 平台信息Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IPlatformService {

    /**
     * 查询平台信息
     */
    PlatformVo queryById(Long platformKey);

    /**
     * 查询平台信息列表
     */
    TableDataInfo<PlatformVo> queryPageList(PlatformBo bo, PageQuery pageQuery);

    /**
     * 查询平台信息列表
     */
    List<PlatformVo> queryList(PlatformBo bo);

    /**
     * 修改平台信息
     */
    Boolean insertByBo(PlatformBo bo);

    /**
     * 修改平台信息
     */
    Boolean updateByBo(PlatformBo bo);

    /**
     * 校验并批量删除平台信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
