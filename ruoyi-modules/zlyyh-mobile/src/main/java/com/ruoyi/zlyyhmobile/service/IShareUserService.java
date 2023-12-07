package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.ShareUserBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;

import java.util.List;

/**
 * 分销员Service接口
 *
 * @author yzg
 * @date 2023-11-09
 */
public interface IShareUserService {

    /**
     * 查询分销员
     */
    ShareUserVo queryById(Long userId);


    /**
     * 查询分销员列表
     */
    List<ShareUserVo> queryList(ShareUserBo bo);

    /**
     * 修改分销员
     */
    Boolean insertByBo(ShareUserBo bo);

}
