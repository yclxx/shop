package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.EquityProductBo;
import com.ruoyi.zlyyh.domain.vo.EquityProductVo;

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
    List<EquityProductVo> queryList(EquityProductBo bo);
}
