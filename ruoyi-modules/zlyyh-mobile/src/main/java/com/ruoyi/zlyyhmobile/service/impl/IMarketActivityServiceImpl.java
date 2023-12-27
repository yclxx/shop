package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.zlyyh.domain.MarketActivity;
import com.ruoyi.zlyyh.domain.VerifierActivity;
import com.ruoyi.zlyyh.domain.bo.MarketActivityBo;
import com.ruoyi.zlyyh.domain.bo.VerifierActivityBo;
import com.ruoyi.zlyyh.domain.vo.MarketActivityVo;
import com.ruoyi.zlyyh.domain.vo.VerifierActivityVo;
import com.ruoyi.zlyyh.mapper.ActivityMerchantMapper;
import com.ruoyi.zlyyh.mapper.MarketActivityMapper;
import com.ruoyi.zlyyh.mapper.VerifierActivityMapper;
import com.ruoyi.zlyyhmobile.service.IMarketActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class IMarketActivityServiceImpl implements IMarketActivityService {
    private final MarketActivityMapper baseMapper;
    private final VerifierActivityMapper verifierActivityMapper;
    private final ActivityMerchantMapper activityMerchantMapper;

    public List<MarketActivityVo> getMarketActivity(MarketActivityBo bo) {
        LambdaQueryWrapper<MarketActivity> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MarketActivity::getSort);
        lqw.eq(MarketActivity::getPlatformKey, bo.getPlatformKey());
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public Boolean addVerifierActivity(VerifierActivityBo bo) {
        LambdaQueryWrapper<VerifierActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(VerifierActivity::getVerifierId, bo.getVerifierId());
        if (ObjectUtil.isNotEmpty(bo.getActivityId())) {
            lqw.eq(VerifierActivity::getActivityId, bo.getActivityId());
        } else {
            lqw.eq(VerifierActivity::getActivityName, bo.getActivityName());
        }
        VerifierActivityVo verifierActivityVo = verifierActivityMapper.selectVoOne(lqw);
        if (ObjectUtil.isNotEmpty(verifierActivityVo)) throw new ServiceException("恭喜你，你已成功参加该活动");

        if (ObjectUtil.isNotEmpty(bo.getActivityId())) {
            MarketActivityVo marketActivityVo = baseMapper.selectVoById(bo.getActivityId());
            VerifierActivity verifierActivity = new VerifierActivity();
            verifierActivity.setId(IdUtil.getSnowflakeNextId());
            verifierActivity.setPlatformKey(marketActivityVo.getPlatformKey());
            verifierActivity.setActivityId(marketActivityVo.getActivityId());
            verifierActivity.setActivityName(marketActivityVo.getName());
            verifierActivity.setVerifierId(bo.getVerifierId());
            verifierActivityMapper.insert(verifierActivity);

            if (ObjectUtil.isNotEmpty(bo.getActivityMerchant())) {
                bo.getActivityMerchant().forEach(o -> {
                    o.setActivityId(marketActivityVo.getActivityId());
                    o.setVerifierId(bo.getVerifierId());
                });
                activityMerchantMapper.insertOrUpdateBatch(bo.getActivityMerchant());
            }
        } else {
            VerifierActivity verifierActivity = new VerifierActivity();
            verifierActivity.setId(IdUtil.getSnowflakeNextId());
            verifierActivity.setPlatformKey(bo.getPlatformKey());
            verifierActivity.setActivityName(bo.getActivityName());
            verifierActivity.setVerifierId(bo.getVerifierId());
            verifierActivityMapper.insert(verifierActivity);
            if (ObjectUtil.isNotEmpty(bo.getActivityMerchant())) {
                bo.getActivityMerchant().forEach(o -> o.setVerifierId(bo.getVerifierId()));
                activityMerchantMapper.insertOrUpdateBatch(bo.getActivityMerchant());
            }
        }
        return true;
    }
}
