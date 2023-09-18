package com.ruoyi.zlyyhmobile.service.impl;

import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.zlyyh.domain.vo.DistributorVo;
import com.ruoyi.zlyyh.mapper.DistributorMapper;
import com.ruoyi.zlyyhmobile.service.IDistributorService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 分销商信息Service业务层处理
 *
 * @author yzg
 * @date 2023-09-15
 */
@RequiredArgsConstructor
@Service
public class DistributorServiceImpl implements IDistributorService {

    private final DistributorMapper baseMapper;

    /**
     * 查询分销商信息
     */
    @Cacheable(cacheNames = CacheNames.DISTRIBUTOR, key = "#distributorId")
    @Override
    public DistributorVo queryById(String distributorId) {
        return baseMapper.selectVoById(distributorId);
    }
}
