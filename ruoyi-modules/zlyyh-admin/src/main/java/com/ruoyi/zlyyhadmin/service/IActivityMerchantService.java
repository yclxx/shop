package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ActivityMerchantBo;
import com.ruoyi.zlyyh.domain.vo.ActivityMerchantVo;

import java.util.Collection;
import java.util.List;

/**
 * 活动商户号Service接口
 *
 * @author yzg
 * @date 2023-12-14
 */
public interface IActivityMerchantService {

    /**
     * 查询活动商户号
     */
    ActivityMerchantVo queryById(Long id);

    /**
     * 查询活动商户号列表
     */
    TableDataInfo<ActivityMerchantVo> queryPageList(ActivityMerchantBo bo, PageQuery pageQuery);

    /**
     * 查询活动商户号列表
     */
    List<ActivityMerchantVo> queryList(ActivityMerchantBo bo);

    /**
     * 修改活动商户号
     */
    Boolean insertByBo(ActivityMerchantBo bo);

    /**
     * 修改活动商户号
     */
    Boolean updateByBo(ActivityMerchantBo bo);

    /**
     * 校验并批量删除活动商户号信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
