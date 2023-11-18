package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.VerifierBo;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;

import java.util.Collection;
import java.util.List;

/**
 * 核销人员管理Service接口
 *
 * @author yzg
 * @date 2023-11-06
 */
public interface IVerifierService {

    /**
     * 查询核销人员管理
     */
    VerifierVo queryById(Long id);

    /**
     * 查询核销人员管理列表
     */
    TableDataInfo<VerifierVo> queryPageList(VerifierBo bo, PageQuery pageQuery);

    /**
     * 查询核销人员管理列表
     */
    List<VerifierVo> queryList(VerifierBo bo);

    /**
     * 修改核销人员管理
     */
    Boolean insertByBo(VerifierBo bo);

    /**
     * 修改核销人员管理
     */
    Boolean updateByBo(VerifierBo bo);

    /**
     * 校验并批量删除核销人员管理信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
