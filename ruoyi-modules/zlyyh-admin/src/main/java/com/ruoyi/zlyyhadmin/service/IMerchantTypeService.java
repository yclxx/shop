package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.MerchantType;
import com.ruoyi.zlyyh.domain.vo.MerchantTypeVo;
import com.ruoyi.zlyyh.domain.bo.MerchantTypeBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 商户门店类别Service接口
 *
 * @author yzg
 * @date 2024-01-04
 */
public interface IMerchantTypeService {

    /**
     * 查询商户门店类别
     */
    MerchantTypeVo queryById(Long merchantTypeId);

    /**
     * 查询商户门店类别列表
     */
    TableDataInfo<MerchantTypeVo> queryPageList(MerchantTypeBo bo, PageQuery pageQuery);

    /**
     * 查询商户门店类别列表
     */
    List<MerchantTypeVo> queryList(MerchantTypeBo bo);

    /**
     * 修改商户门店类别
     */
    Boolean insertByBo(MerchantTypeBo bo);

    /**
     * 修改商户门店类别
     */
    Boolean updateByBo(MerchantTypeBo bo);

    /**
     * 校验并批量删除商户门店类别信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
