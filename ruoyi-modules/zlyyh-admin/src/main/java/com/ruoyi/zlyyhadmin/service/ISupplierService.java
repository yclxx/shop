package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.SupplierBo;
import com.ruoyi.zlyyh.domain.vo.SupplierVo;

import java.util.Collection;
import java.util.List;

/**
 * 供应商Service接口
 *
 * @author yzg
 * @date 2023-10-11
 */
public interface ISupplierService {

    /**
     * 查询供应商
     */
    SupplierVo queryById(Long supplierId);

    /**
     * 查询供应商列表
     */
    TableDataInfo<SupplierVo> queryPageList(SupplierBo bo, PageQuery pageQuery);

    /**
     * 查询供应商列表
     */
    List<SupplierVo> queryList(SupplierBo bo);

    /**
     * 修改供应商
     */
    Boolean insertByBo(SupplierBo bo);

    /**
     * 修改供应商
     */
    Boolean updateByBo(SupplierBo bo);

    /**
     * 校验并批量删除供应商信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
