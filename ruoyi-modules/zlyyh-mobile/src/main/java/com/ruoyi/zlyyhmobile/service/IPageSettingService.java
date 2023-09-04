package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.PageSettingBo;
import com.ruoyi.zlyyh.domain.vo.PageSettingVo;

import java.util.List;

/**
 * 页面配置Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IPageSettingService {

    /**
     * 查询页面配置列表
     */
    List<PageSettingVo> queryList(PageSettingBo bo);

}
