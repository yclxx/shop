package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.DrawBo;
import com.ruoyi.zlyyh.domain.vo.DrawVo;

import java.util.List;

/**
 * 奖品管理Service接口
 *
 * @author yzg
 * @date 2023-05-10
 */
public interface IDrawService {

    /**
     * 查询奖品管理
     */
    DrawVo queryById(Long drawId);

    /**
     * 查询奖品管理列表
     */
    List<DrawVo> queryList(DrawBo bo);

    /**
     * 查询奖品管理列表
     */
    List<DrawVo> queryListNoCache(DrawBo bo);

}
