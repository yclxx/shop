package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.ExtensionServiceProvider;
import com.ruoyi.zlyyh.domain.vo.ExtensionServiceProviderVo;
import com.ruoyi.zlyyh.domain.bo.ExtensionServiceProviderBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 商户拓展服务商Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface IExtensionServiceProviderService {

    /**
     * 查询商户拓展服务商
     */
    ExtensionServiceProviderVo queryById(Long id);

    /**
     * 查询商户拓展服务商列表
     */
    TableDataInfo<ExtensionServiceProviderVo> queryPageList(ExtensionServiceProviderBo bo, PageQuery pageQuery);

    /**
     * 查询商户拓展服务商列表
     */
    List<ExtensionServiceProviderVo> queryList(ExtensionServiceProviderBo bo);

    /**
     * 修改商户拓展服务商
     */
    Boolean insertByBo(ExtensionServiceProviderBo bo);

    /**
     * 修改商户拓展服务商
     */
    Boolean updateByBo(ExtensionServiceProviderBo bo);

    /**
     * 校验并批量删除商户拓展服务商信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
