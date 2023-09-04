package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.InviteUserLogBo;
import com.ruoyi.zlyyh.domain.vo.InviteUserLogVo;

import java.util.Collection;
import java.util.List;

/**
 * 邀请记录Service接口
 *
 * @author yzg
 * @date 2023-08-08
 */
public interface IInviteUserLogService {

    /**
     * 查询邀请记录
     */
    InviteUserLogVo queryById(Long id);

    /**
     * 查询邀请记录列表
     */
    TableDataInfo<InviteUserLogVo> queryPageList(InviteUserLogBo bo, PageQuery pageQuery);

    /**
     * 查询邀请记录列表
     */
    List<InviteUserLogVo> queryList(InviteUserLogBo bo);

    /**
     * 修改邀请记录
     */
    Boolean insertByBo(InviteUserLogBo bo);

    /**
     * 修改邀请记录
     */
    Boolean updateByBo(InviteUserLogBo bo);

    /**
     * 校验并批量删除邀请记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
