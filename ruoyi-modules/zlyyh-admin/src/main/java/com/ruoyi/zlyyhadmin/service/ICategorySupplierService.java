package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.CategorySupplier;
import com.ruoyi.zlyyh.domain.bo.CategorySupplierBo;
import com.ruoyi.zlyyh.domain.vo.CategorySupplierVo;

import java.util.Collection;
import java.util.List;

/**
 * 供应商产品分类Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface ICategorySupplierService {

    /**
     * 查询供应商产品分类
     */
    CategorySupplierVo queryById(Long id);

    CategorySupplier queryBySupplierId(String supplierId,String fullName);

    /**
     * 查询供应商产品分类列表
     */
    TableDataInfo<CategorySupplierVo> queryPageList(CategorySupplierBo bo, PageQuery pageQuery);

    /**
     * 查询供应商产品分类列表
     */
    List<CategorySupplierVo> queryList(CategorySupplierBo bo);

    /**
     * 修改供应商产品分类
     */
    Boolean insertByBo(CategorySupplierBo bo);
    Boolean insert(CategorySupplier bo);

    /**
     * 修改供应商产品分类
     */
    Boolean updateByBo(CategorySupplierBo bo);

    /**
     * 校验并批量删除供应商产品分类信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
