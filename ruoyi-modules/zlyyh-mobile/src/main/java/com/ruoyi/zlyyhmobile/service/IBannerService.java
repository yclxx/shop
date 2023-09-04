package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.BannerBo;
import com.ruoyi.zlyyh.domain.vo.BannerVo;

import java.util.List;

/**
 * 广告管理Service接口
 *
 * @author ruoyi
 * @date 2023-03-21
 */
public interface IBannerService {

    /**
     * 查询广告管理列表
     */
    List<BannerVo> queryList(BannerBo bo);
}
