package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.UnionpayMissionUser;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionUserBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 银联任务用户Service接口
 *
 * @author yzg
 * @date 2024-02-21
 */
public interface IUnionpayMissionUserService {

    /**
     * 查询银联任务用户
     */
    UnionpayMissionUserVo queryById(Long upMissionUserId);

    /**
     * 查询银联任务用户列表
     */
    TableDataInfo<UnionpayMissionUserVo> queryPageList(UnionpayMissionUserBo bo, PageQuery pageQuery);

    /**
     * 查询银联任务用户列表
     */
    List<UnionpayMissionUserVo> queryList(UnionpayMissionUserBo bo);

    /**
     * 修改银联任务用户
     */
    Boolean insertByBo(UnionpayMissionUserBo bo);

    /**
     * 修改银联任务用户
     */
    Boolean updateByBo(UnionpayMissionUserBo bo);

    /**
     * 校验并批量删除银联任务用户信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
