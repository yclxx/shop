package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.PlatformCityGroupBo;
import com.ruoyi.zlyyh.domain.vo.PlatformCityGroupEntity;

import java.util.List;

/**
 * 企业微信二维码Service 接口
 */
public interface IPlatformCityGroupService {

    /**
     * 查询列表
     */
    List<PlatformCityGroupEntity> queryList(PlatformCityGroupBo bo);
}
