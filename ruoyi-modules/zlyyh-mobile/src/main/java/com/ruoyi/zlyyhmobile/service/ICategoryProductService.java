package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.CategoryProductVo;

import java.util.List;

/**
 * 栏目商品关联Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface ICategoryProductService {

    /**
     * 查询栏目商品关联列表
     */
    List<CategoryProductVo> queryList(Long categoryId);
}
