package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.ProductPackageVo;

import java.util.List;

/**
 * 商品券包Service接口
 *
 * @author yzg
 * @date 2023-06-30
 */
public interface IProductPackageService {

    /**
     * 查询商品券包列表
     */
    List<ProductPackageVo> queryListByProductId(Long productId);
}
