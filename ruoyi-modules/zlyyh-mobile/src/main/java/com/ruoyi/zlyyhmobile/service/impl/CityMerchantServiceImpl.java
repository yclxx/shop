package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.CityMerchant;
import com.ruoyi.zlyyh.domain.vo.CityMerchantVo;
import com.ruoyi.zlyyh.mapper.CityMerchantMapper;
import com.ruoyi.zlyyhmobile.service.ICityMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 城市商户Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class CityMerchantServiceImpl implements ICityMerchantService {

    private final CityMerchantMapper baseMapper;

    /**
     * 查询城市商户列表
     */
    @Override
    public CityMerchantVo queryOneByCityCode(String cityCode,Long platformKey) {
        LambdaQueryWrapper<CityMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(CityMerchant::getAdcode,cityCode);
        lqw.eq(CityMerchant::getPlatformKey,platformKey);
        lqw.last("limit 1");
        return baseMapper.selectVoOne(lqw);
    }

}
