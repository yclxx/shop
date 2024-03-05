package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.UnionpayMissionUser;
import com.ruoyi.zlyyh.domain.bo.UserBo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.UnionpayMissionUserMapper;
import com.ruoyi.zlyyh.mapper.UserMapper;
import com.ruoyi.zlyyhadmin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionUserLogBo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserLogVo;
import com.ruoyi.zlyyh.domain.UnionpayMissionUserLog;
import com.ruoyi.zlyyh.mapper.UnionpayMissionUserLogMapper;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionUserLogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 银联任务奖励发放记录Service业务层处理
 *
 * @author yzg
 * @date 2024-02-21
 */
@RequiredArgsConstructor
@Service
public class UnionpayMissionUserLogServiceImpl implements IUnionpayMissionUserLogService {

    private final UnionpayMissionUserLogMapper baseMapper;
    private final UnionpayMissionUserMapper unionpayMissionUserMapper;
    private final IUserService userService;

    /**
     * 查询银联任务奖励发放记录
     */
    @Override
    public UnionpayMissionUserLogVo queryById(Long upMissionUserLog){
        return baseMapper.selectVoById(upMissionUserLog);
    }

    /**
     * 查询银联任务奖励发放记录列表
     */
    @Override
    public TableDataInfo<UnionpayMissionUserLogVo> queryPageList(UnionpayMissionUserLogBo bo, PageQuery pageQuery) {
        if (ObjectUtil.isNotEmpty(bo.getUpMissionUserId())) {
            UserBo userBo = new UserBo();
            userBo.setMobile(bo.getUpMissionUserId().toString());
            List<UserVo> userVos = userService.queryList(userBo);
            if (ObjectUtil.isNotEmpty(userVos)) {
                UnionpayMissionUserVo missionUserVo = unionpayMissionUserMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionUser>().eq(UnionpayMissionUser::getUserId, userVos.get(0).getUserId()).last("limit 1"));
                if (ObjectUtil.isNotEmpty(missionUserVo)) {
                    bo.setUpMissionUserId(missionUserVo.getUpMissionUserId());
                } else {
                    return TableDataInfo.build(new ArrayList<>());
                }
            } else {
                return TableDataInfo.build(new ArrayList<>());
            }
        }
        LambdaQueryWrapper<UnionpayMissionUserLog> lqw = buildQueryWrapper(bo);
        Page<UnionpayMissionUserLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<UnionpayMissionUserLogVo> dataInfo = TableDataInfo.build(result);
        for (UnionpayMissionUserLogVo row : dataInfo.getRows()) {
            UnionpayMissionUserVo missionUserVo = unionpayMissionUserMapper.selectVoById(row.getUpMissionUserId());
            if (ObjectUtil.isNotEmpty(missionUserVo)) {
                //查询用户
                row.setUserVo(userService.queryById(missionUserVo.getUserId()));
            }
        }
        return dataInfo;
    }

    /**
     * 查询银联任务奖励发放记录列表
     */
    @Override
    public List<UnionpayMissionUserLogVo> queryList(UnionpayMissionUserLogBo bo) {
        LambdaQueryWrapper<UnionpayMissionUserLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<UnionpayMissionUserLog> buildQueryWrapper(UnionpayMissionUserLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<UnionpayMissionUserLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUpMissionUserId() != null, UnionpayMissionUserLog::getUpMissionUserId, bo.getUpMissionUserId());
        lqw.eq(bo.getUpMissionGroupId() != null, UnionpayMissionUserLog::getUpMissionGroupId, bo.getUpMissionGroupId());
        lqw.eq(bo.getUpMissionId() != null, UnionpayMissionUserLog::getUpMissionId, bo.getUpMissionId());
        lqw.eq(bo.getProductId() != null, UnionpayMissionUserLog::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), UnionpayMissionUserLog::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getAccount()), UnionpayMissionUserLog::getAccount, bo.getAccount());
        lqw.eq(bo.getSendTime() != null, UnionpayMissionUserLog::getSendTime, bo.getSendTime());
        lqw.eq(bo.getAmount() != null, UnionpayMissionUserLog::getAmount, bo.getAmount());
        lqw.eq(bo.getSendCount() != null, UnionpayMissionUserLog::getSendCount, bo.getSendCount());
        lqw.eq(StringUtils.isNotBlank(bo.getFailReason()), UnionpayMissionUserLog::getFailReason, bo.getFailReason());
        return lqw;
    }

    /**
     * 新增银联任务奖励发放记录
     */
    @Override
    public Boolean insertByBo(UnionpayMissionUserLogBo bo) {
        UnionpayMissionUserLog add = BeanUtil.toBean(bo, UnionpayMissionUserLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setUpMissionUserLog(add.getUpMissionUserLog());
        }
        return flag;
    }

    /**
     * 修改银联任务奖励发放记录
     */
    @Override
    public Boolean updateByBo(UnionpayMissionUserLogBo bo) {
        UnionpayMissionUserLog update = BeanUtil.toBean(bo, UnionpayMissionUserLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(UnionpayMissionUserLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除银联任务奖励发放记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
