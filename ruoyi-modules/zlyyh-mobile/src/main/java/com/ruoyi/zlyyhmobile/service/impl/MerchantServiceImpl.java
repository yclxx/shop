package com.ruoyi.zlyyhmobile.service.impl;

import com.ruoyi.zlyyh.domain.vo.MerchantVo;
import com.ruoyi.zlyyh.mapper.MerchantMapper;
import com.ruoyi.zlyyhmobile.service.IMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商户号Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class MerchantServiceImpl implements IMerchantService {

    private final MerchantMapper baseMapper;

    /**
     * 查询商户号
     */
    @Override
    public MerchantVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }
}
