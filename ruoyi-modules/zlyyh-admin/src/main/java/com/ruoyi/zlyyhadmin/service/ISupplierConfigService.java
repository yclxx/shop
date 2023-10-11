package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.SupplierConfigBo;
import com.ruoyi.zlyyh.domain.vo.SupplierConfigVo;

import java.util.Collection;
import java.util.List;

/**
 * 供应商参数配置Service接口
 *
 * @author yzg
 * @date 2023-10-11
 */
public interface ISupplierConfigService {

    /**
     * 查询供应商参数配置
     */
    SupplierConfigVo queryById(Long configId);

    /**
     * 查询供应商参数配置列表
     */
    TableDataInfo<SupplierConfigVo> queryPageList(SupplierConfigBo bo, PageQuery pageQuery);

    /**
     * 查询供应商参数配置列表
     */
    List<SupplierConfigVo> queryList(SupplierConfigBo bo);

    /**
     * 修改供应商参数配置
     */
    Boolean insertByBo(SupplierConfigBo bo);

    /**
     * 修改供应商参数配置
     */
    Boolean updateByBo(SupplierConfigBo bo);

    /**
     * 校验并批量删除供应商参数配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
