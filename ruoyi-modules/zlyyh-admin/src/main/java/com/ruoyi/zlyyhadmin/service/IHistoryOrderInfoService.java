package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.HistoryOrderInfo;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderInfoVo;
import com.ruoyi.zlyyh.domain.bo.HistoryOrderInfoBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 历史订单扩展信息Service接口
 *
 * @author yzg
 * @date 2023-08-01
 */
public interface IHistoryOrderInfoService {

    /**
     * 查询历史订单扩展信息
     */
    HistoryOrderInfoVo queryById(Long number);

    /**
     * 查询历史订单扩展信息列表
     */
    TableDataInfo<HistoryOrderInfoVo> queryPageList(HistoryOrderInfoBo bo, PageQuery pageQuery);

    /**
     * 查询历史订单扩展信息列表
     */
    List<HistoryOrderInfoVo> queryList(HistoryOrderInfoBo bo);

    /**
     * 修改历史订单扩展信息
     */
    Boolean insertByBo(HistoryOrderInfoBo bo);

    /**
     * 修改历史订单扩展信息
     */
    Boolean updateByBo(HistoryOrderInfoBo bo);

    /**
     * 校验并批量删除历史订单扩展信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
