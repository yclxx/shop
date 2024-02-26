package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionProgressBo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionProgressVo;
import com.ruoyi.zlyyh.domain.UnionpayMissionProgress;
import com.ruoyi.zlyyh.mapper.UnionpayMissionProgressMapper;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionProgressService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 银联任务进度Service业务层处理
 *
 * @author yzg
 * @date 2024-02-22
 */
@RequiredArgsConstructor
@Service
public class UnionpayMissionProgressServiceImpl implements IUnionpayMissionProgressService {

    private final UnionpayMissionProgressMapper baseMapper;

    /**
     * 查询银联任务进度
     */
    @Override
    public UnionpayMissionProgressVo queryById(Long progressId){
        return baseMapper.selectVoById(progressId);
    }

    /**
     * 查询银联任务进度列表
     */
    @Override
    public TableDataInfo<UnionpayMissionProgressVo> queryPageList(UnionpayMissionProgressBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<UnionpayMissionProgress> lqw = buildQueryWrapper(bo);
        Page<UnionpayMissionProgressVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询银联任务进度列表
     */
    @Override
    public List<UnionpayMissionProgressVo> queryList(UnionpayMissionProgressBo bo) {
        LambdaQueryWrapper<UnionpayMissionProgress> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<UnionpayMissionProgress> buildQueryWrapper(UnionpayMissionProgressBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<UnionpayMissionProgress> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUpMissionId() != null, UnionpayMissionProgress::getUpMissionId, bo.getUpMissionId());
        lqw.eq(bo.getUpMissionGroupId() != null, UnionpayMissionProgress::getUpMissionGroupId, bo.getUpMissionGroupId());
        lqw.eq(bo.getUpMissionUserId() != null, UnionpayMissionProgress::getUpMissionUserId, bo.getUpMissionUserId());
        lqw.eq(bo.getDayProgress() != null, UnionpayMissionProgress::getDayProgress, bo.getDayProgress());
        lqw.eq(bo.getWeekProgress() != null, UnionpayMissionProgress::getWeekProgress, bo.getWeekProgress());
        lqw.eq(bo.getMonthProgress() != null, UnionpayMissionProgress::getMonthProgress, bo.getMonthProgress());
        lqw.eq(bo.getActivityProgress() != null, UnionpayMissionProgress::getActivityProgress, bo.getActivityProgress());
        return lqw;
    }

    /**
     * 新增银联任务进度
     */
    @Override
    public Boolean insertByBo(UnionpayMissionProgressBo bo) {
        UnionpayMissionProgress add = BeanUtil.toBean(bo, UnionpayMissionProgress.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProgressId(add.getProgressId());
        }
        return flag;
    }

    /**
     * 修改银联任务进度
     */
    @Override
    public Boolean updateByBo(UnionpayMissionProgressBo bo) {
        UnionpayMissionProgress update = BeanUtil.toBean(bo, UnionpayMissionProgress.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(UnionpayMissionProgress entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除银联任务进度
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
