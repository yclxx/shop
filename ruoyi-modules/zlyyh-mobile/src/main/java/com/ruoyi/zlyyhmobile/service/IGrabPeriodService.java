package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.GrabPeriodVo;
import com.ruoyi.zlyyhmobile.domain.bo.GrabPeriodProductQueryBo;
import com.ruoyi.zlyyh.domain.vo.AppProductVo;

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
     * 查询秒杀商品
     *
     * @param bo 秒杀配置
     * @return 产品接口
     */
    List<AppProductVo> getProductList(GrabPeriodProductQueryBo bo);
}
