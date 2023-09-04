package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.OrderDownloadLogBo;
import com.ruoyi.zlyyh.domain.vo.OrderDownloadLogVo;

import java.util.Collection;
import java.util.List;

/**
 * 订单下载记录Service接口
 *
 * @author yzg
 * @date 2023-04-01
 */
public interface IOrderDownloadLogService {

    /**
     * 查询订单下载记录
     */
    OrderDownloadLogVo queryById(Long tOrderDownloadId);

    /**
     * 查询订单下载记录列表
     */
    TableDataInfo<OrderDownloadLogVo> queryPageList(OrderDownloadLogBo bo, PageQuery pageQuery);

    /**
     * 查询订单下载记录列表
     */
    List<OrderDownloadLogVo> queryList(OrderDownloadLogBo bo);

    /**
     * 修改订单下载记录
     */
    Boolean insertByBo(OrderDownloadLogBo bo);

    /**
     * 修改订单下载记录
     */
    Boolean updateByBo(OrderDownloadLogBo bo);

    /**
     * 校验并批量删除订单下载记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
