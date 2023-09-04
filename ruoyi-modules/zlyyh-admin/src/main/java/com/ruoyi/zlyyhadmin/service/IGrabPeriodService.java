package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.GrabPeriod;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodVo;
import com.ruoyi.zlyyh.domain.bo.GrabPeriodBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 秒杀配置Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IGrabPeriodService {

    /**
     * 查询秒杀配置
     */
    GrabPeriodVo queryById(Long id);

    /**
     * 查询秒杀配置列表
     */
    TableDataInfo<GrabPeriodVo> queryPageList(GrabPeriodBo bo, PageQuery pageQuery);

    /**
     * 查询秒杀配置列表
     */
    List<GrabPeriodVo> queryList(GrabPeriodBo bo);

    /**
     * 修改秒杀配置
     */
    Boolean insertByBo(GrabPeriodBo bo);

    /**
     * 修改秒杀配置
     */
    Boolean updateByBo(GrabPeriodBo bo);

    /**
     * 校验并批量删除秒杀配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
