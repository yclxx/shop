package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.UnionPayContentRefundOrder;
import com.ruoyi.zlyyh.domain.vo.UnionPayContentRefundOrderVo;
import com.ruoyi.zlyyh.domain.bo.UnionPayContentRefundOrderBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 内容分销内容方退券订单Service接口
 *
 * @author yzg
 * @date 2023-09-23
 */
public interface IUnionPayContentRefundOrderService {

    /**
     * 查询内容分销内容方退券订单
     */
    UnionPayContentRefundOrderVo queryById(Long id);

    /**
     * 查询内容分销内容方退券订单列表
     */
    TableDataInfo<UnionPayContentRefundOrderVo> queryPageList(UnionPayContentRefundOrderBo bo, PageQuery pageQuery);

    /**
     * 查询内容分销内容方退券订单列表
     */
    List<UnionPayContentRefundOrderVo> queryList(UnionPayContentRefundOrderBo bo);

    /**
     * 修改内容分销内容方退券订单
     */
    Boolean insertByBo(UnionPayContentRefundOrderBo bo);

    /**
     * 修改内容分销内容方退券订单
     */
    Boolean updateByBo(UnionPayContentRefundOrderBo bo);

    /**
     * 校验并批量删除内容分销内容方退券订单信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
