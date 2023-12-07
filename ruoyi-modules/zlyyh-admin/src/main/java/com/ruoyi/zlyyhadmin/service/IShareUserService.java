package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShareUserBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;

import java.util.Collection;
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
    TableDataInfo<ShareUserVo> queryPageList(ShareUserBo bo, PageQuery pageQuery);


    /**
     * 查询分销员列表
     */
    List<ShareUserVo> queryList(ShareUserBo bo);

    /**
     * 修改分销员
     */
    Boolean insertByBo(ShareUserBo bo);

    /**
     * 修改分销员
     */
    Boolean updateByBo(ShareUserBo bo);

    /**
     * 校验并批量删除分销员信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
