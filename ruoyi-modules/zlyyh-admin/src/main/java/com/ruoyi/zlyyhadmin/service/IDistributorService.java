package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.DistributorBo;
import com.ruoyi.zlyyh.domain.vo.DistributorVo;

import java.util.Collection;
import java.util.List;

/**
 * 分销商信息Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface IDistributorService {

    /**
     * 查询分销商信息
     */
    DistributorVo queryById(String distributorId);

    /**
     * 查询分销商信息列表
     */
    TableDataInfo<DistributorVo> queryPageList(DistributorBo bo, PageQuery pageQuery);

    /**
     * 查询分销商信息列表
     */
    List<DistributorVo> queryList(DistributorBo bo);

    /**
     * 修改分销商信息
     */
    Boolean insertByBo(DistributorBo bo);

    /**
     * 修改分销商信息
     */
    Boolean updateByBo(DistributorBo bo);

    /**
     * 校验并批量删除分销商信息信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
