package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.bo.CategoryBo;
import com.ruoyi.zlyyh.domain.vo.CategoryVo;

import java.util.Collection;
import java.util.List;

/**
 * 栏目Service接口
 *
 * @author yzgnet
 * @date 2023-03-31
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

    /**
     * 修改栏目
     */
    Boolean insertByBo(CategoryBo bo);

    /**
     * 修改栏目
     */
    Boolean updateByBo(CategoryBo bo);

    /**
     * 校验并批量删除栏目信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
