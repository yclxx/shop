package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.MarketBo;
import com.ruoyi.zlyyh.domain.vo.MarketVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 新用户营销Service接口
 *
 * @author yzg
 * @date 2023-10-18
 */
public interface IMarketService {

    /**
     * 查询新用户营销
     */
    MarketVo queryById(Long marketId);

    /**
     * 查询奖励信息
     */
    Map<String, Object> queryByIds(Long marketId);
    /**
     * 查询新用户营销列表
     */
    TableDataInfo<MarketVo> queryPageList(MarketBo bo, PageQuery pageQuery);

    /**
     * 查询新用户营销列表
     */
    List<MarketVo> queryList(MarketBo bo);

    /**
     * 修改新用户营销
     */
    Boolean insertByBo(MarketBo bo);

    /**
     * 修改新用户营销
     */
    Boolean updateByBo(MarketBo bo);

    /**
     * 校验并批量删除新用户营销信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
