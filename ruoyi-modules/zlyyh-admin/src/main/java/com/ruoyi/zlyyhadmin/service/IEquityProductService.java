package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.EquityProductBo;
import com.ruoyi.zlyyh.domain.vo.EquityProductVo;

import java.util.Collection;
import java.util.List;

/**
 * 权益包商品Service接口
 *
 * @author yzg
 * @date 2023-06-06
 */
public interface IEquityProductService {

    /**
     * 查询权益包商品
     */
    EquityProductVo queryById(Long id);

    /**
     * 查询权益包商品列表
     */
    TableDataInfo<EquityProductVo> queryPageList(EquityProductBo bo, PageQuery pageQuery);

    /**
     * 查询权益包商品列表
     */
    List<EquityProductVo> queryList(EquityProductBo bo);

    /**
     * 修改权益包商品
     */
    Boolean insertByBo(EquityProductBo bo);

    /**
     * 修改权益包商品
     */
    Boolean updateByBo(EquityProductBo bo);

    /**
     * 校验并批量删除权益包商品信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
