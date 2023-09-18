package com.ruoyi.zlyyhadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zlyyh.domain.CategoryProduct;
import com.ruoyi.zlyyh.domain.vo.CategoryProductVo;
import com.ruoyi.zlyyh.domain.bo.CategoryProductBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 栏目商品关联Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface ICategoryProductService {

    /**
     * 查询栏目商品关联
     */
    CategoryProductVo queryById(Long id);

    Long queryByCategoryAndProduct(Long categoryId,Long productId);

    /**
     * 查询栏目商品关联列表
     */
    TableDataInfo<CategoryProductVo> queryPageList(CategoryProductBo bo, PageQuery pageQuery);

    /**
     * 查询栏目商品关联列表
     */
    List<CategoryProductVo> queryList(CategoryProductBo bo);

    /**
     * 修改栏目商品关联
     */
    Boolean insertByBo(CategoryProductBo bo);

    /**
     * 修改栏目商品关联
     */
    Boolean updateByBo(CategoryProductBo bo);

    /**
     * 校验并批量删除栏目商品关联信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean remove(LambdaQueryWrapper<CategoryProduct> queryWrapper);
}
