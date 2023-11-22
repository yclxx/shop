package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.zlyyh.domain.MerchantApproval;
import com.ruoyi.zlyyh.domain.PromotionTask;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.PromotionTaskBo;
import com.ruoyi.zlyyh.domain.vo.PromotionTaskVo;
import com.ruoyi.zlyyh.mapper.MerchantApprovalMapper;
import com.ruoyi.zlyyh.mapper.PromotionTaskMapper;
import com.ruoyi.zlyyhmobile.service.IPromotionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PromotionTaskServiceImpl implements IPromotionTaskService {
    private final PromotionTaskMapper baseMapper;
    private final MerchantApprovalMapper merchantApprovalMapper;

    @Cacheable(cacheNames = CacheNames.M_PROMOTIONTASK, key = "#bo.getPlatformKey()+'-'+#bo.getShowCity()")
    @Override
    public List<PromotionTaskVo> list(PromotionTaskBo bo) {
        LambdaQueryWrapper<PromotionTask> lqw = Wrappers.lambdaQuery();
        lqw.eq(PromotionTask::getPlatformKey, bo.getPlatformKey());
        lqw.eq(PromotionTask::getStatus, "0");
        if (StringUtils.isNotEmpty(bo.getShowCity())) {
            lqw.and(lq -> lq.like(PromotionTask::getShowCity, bo.getShowCity()).or(e -> e.eq(PromotionTask::getShowCity, "ALL")));
        }
        Date nowDate = DateUtils.getNowDate();
        lqw.and(lq -> lq.le(PromotionTask::getStartDate, nowDate).or(e -> e.isNull(PromotionTask::getStartDate)));
        lqw.and(lq -> lq.ge(PromotionTask::getEndDate, nowDate).or(e -> e.isNull(PromotionTask::getEndDate)));
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public PromotionTaskVo getPromotionTask(Long taskId) {
        return baseMapper.selectVoById(taskId);
    }

    public Boolean merchantApprovalInsert(MerchantApprovalBo bo) {
        bo.setType("1");
        MerchantApproval merchantApproval = BeanCopyUtils.copy(bo, MerchantApproval.class);
        return merchantApprovalMapper.insert(merchantApproval) > 0;
    }
}
