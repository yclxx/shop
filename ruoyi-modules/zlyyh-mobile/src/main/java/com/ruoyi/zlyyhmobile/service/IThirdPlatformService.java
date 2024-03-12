package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.ThirdPlatformVo;

/**
 * 第三方平台信息配置Service接口
 *
 * @author yzg
 * @date 2024-03-08
 */
public interface IThirdPlatformService {

    /**
     * 查询
     */
    ThirdPlatformVo selectByAppId(String appId,String type);
}
