package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.CategoryBo;
import com.ruoyi.zlyyh.domain.vo.CategoryVo;

import java.util.List;

/**
 * 栏目Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface ICategoryService {

    /**
     * 查询栏目
     */
    CategoryVo queryById(Long categoryId);

    /**
     * 查询栏目列表
     */
    List<CategoryVo> queryList(CategoryBo bo);
}
