package com.ruoyi.zlyyhmobile.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionProgressVo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserLogVo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionVo;

import java.util.Collection;
import java.util.List;

/**
 * 银联任务配置Service接口
 *
 * @author yzg
 * @date 2024-02-21
 */
public interface IUnionpayMissionService {

    /**
     * 查询银联任务配置
     */
    UnionpayMissionVo queryById(Long upMissionId);

    /**
     * 查询银联任务配置列表
     */
    TableDataInfo<UnionpayMissionVo> queryPageList(UnionpayMissionBo bo, PageQuery pageQuery);

    /**
     * 查询银联任务配置列表
     */
    List<UnionpayMissionVo> queryList(UnionpayMissionBo bo);

    /**
     * 修改银联任务配置
     */
    Boolean insertByBo(UnionpayMissionBo bo);

    /**
     * 修改银联任务配置
     */
    Boolean updateByBo(UnionpayMissionBo bo);

    /**
     * 校验并批量删除银联任务配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 用户报名
     */
    void userSingUp(UnionpayMissionUserBo bo);

    /**
     * 用户报名校验
     */
    R<Object> signUpVerify(UnionpayMissionUserBo bo);

    /**
     * 查询任务进度
     * @param bo
     * @return
     */
    void getMissionProgress(UnionpayMissionUserBo bo);

    /**
     * 查询任务奖励列表
     * @param bo
     * @return
     */
    List<UnionpayMissionUserLogVo> rewardList(UnionpayMissionUserLogBo bo);

    /**
     * 任务通知处理
     * @param param
     */
    void missionCallback(JSONObject param);

    /**
     * 查询银联任务进度列表
     */
    List<UnionpayMissionProgressVo> getProgressList(UnionpayMissionProgressBo bo);
}
