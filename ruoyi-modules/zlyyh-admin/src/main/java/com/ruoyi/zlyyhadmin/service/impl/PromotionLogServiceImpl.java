package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.IPromotionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.PromotionLogBo;
import com.ruoyi.zlyyh.domain.vo.PromotionLogVo;
import com.ruoyi.zlyyh.domain.PromotionLog;
import com.ruoyi.zlyyh.mapper.PromotionLogMapper;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 推广任务记录Service业务层处理
 *
 * @author yzg
 * @date 2023-11-22
 */
@RequiredArgsConstructor
@Service
public class PromotionLogServiceImpl implements IPromotionLogService {

    private final PromotionLogMapper baseMapper;

    /**
     * 查询推广任务记录
     */
    @Override
    public PromotionLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询推广任务记录列表
     */
    @Override
    public TableDataInfo<PromotionLogVo> queryPageList(PromotionLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PromotionLog> lqw = buildQueryWrapper(bo);
        Page<PromotionLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询推广任务记录列表
     */
    @Override
    public List<PromotionLogVo> queryList(PromotionLogBo bo) {
        LambdaQueryWrapper<PromotionLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PromotionLog> buildQueryWrapper(PromotionLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PromotionLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, PromotionLog::getPlatformKey, bo.getPlatformKey());
        lqw.like(StringUtils.isNotBlank(bo.getApprovalBrandName()), PromotionLog::getApprovalBrandName, bo.getApprovalBrandName());
        lqw.like(StringUtils.isNotBlank(bo.getTaskName()), PromotionLog::getTaskName, bo.getTaskName());
        lqw.eq(StringUtils.isNotBlank(bo.getVerifierMobile()), PromotionLog::getVerifierMobile, bo.getVerifierMobile());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), PromotionLog::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增推广任务记录
     */
    @Override
    public Boolean insertByBo(PromotionLogBo bo) {
        PromotionLog add = BeanUtil.toBean(bo, PromotionLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        return flag;
    }

    /**
     * 修改推广任务记录
     */
    @Override
    public Boolean updateByBo(PromotionLogBo bo) {
        PromotionLog update = BeanUtil.toBean(bo, PromotionLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PromotionLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除推广任务记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
