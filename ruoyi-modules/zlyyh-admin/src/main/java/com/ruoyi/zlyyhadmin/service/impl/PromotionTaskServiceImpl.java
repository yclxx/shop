package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.PromotionTask;
import com.ruoyi.zlyyh.domain.bo.PromotionTaskBo;
import com.ruoyi.zlyyh.domain.vo.PromotionTaskVo;
import com.ruoyi.zlyyh.mapper.PromotionTaskMapper;
import com.ruoyi.zlyyhadmin.service.IPromotionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 推广任务Service业务层处理
 *
 * @author yzg
 * @date 2023-11-16
 */
@RequiredArgsConstructor
@Service
public class PromotionTaskServiceImpl implements IPromotionTaskService {

    private final PromotionTaskMapper baseMapper;

    /**
     * 查询推广任务
     */
    @Override
    public PromotionTaskVo queryById(Long taskId){
        return baseMapper.selectVoById(taskId);
    }

    /**
     * 查询推广任务列表
     */
    @Override
    public TableDataInfo<PromotionTaskVo> queryPageList(PromotionTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PromotionTask> lqw = buildQueryWrapper(bo);
        Page<PromotionTaskVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询推广任务列表
     */
    @Override
    public List<PromotionTaskVo> queryList(PromotionTaskBo bo) {
        LambdaQueryWrapper<PromotionTask> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PromotionTask> buildQueryWrapper(PromotionTaskBo bo) {
        //Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PromotionTask> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, PromotionTask::getPlatformKey, bo.getPlatformKey());
        lqw.like(StringUtils.isNotBlank(bo.getTaskName()), PromotionTask::getTaskName, bo.getTaskName());
        lqw.eq(bo.getStartDate() != null, PromotionTask::getStartDate, bo.getStartDate());
        lqw.eq(bo.getEndDate() != null, PromotionTask::getEndDate, bo.getEndDate());
        lqw.eq(StringUtils.isNotBlank(bo.getActivityNature()), PromotionTask::getActivityNature, bo.getActivityNature());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), PromotionTask::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增推广任务
     */
    @CacheEvict(cacheNames = CacheNames.M_PROMOTIONTASK, allEntries = true)
    @Override
    public Boolean insertByBo(PromotionTaskBo bo) {
        PromotionTask add = BeanUtil.toBean(bo, PromotionTask.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTaskId(add.getTaskId());
        }
        return flag;
    }

    /**
     * 修改推广任务
     */
    @CacheEvict(cacheNames = CacheNames.M_PROMOTIONTASK, allEntries = true)
    @Override
    public Boolean updateByBo(PromotionTaskBo bo) {
        PromotionTask update = BeanUtil.toBean(bo, PromotionTask.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除推广任务
     */
    @CacheEvict(cacheNames = CacheNames.M_PROMOTIONTASK, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
