package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.VerifierShop;
import com.ruoyi.zlyyh.domain.vo.VerifierShopVo;
import org.apache.ibatis.annotations.Param;

public interface VerifierShopMapper extends BaseMapperPlus<VerifierShopMapper, VerifierShop, VerifierShopVo> {
    VerifierShop selectByShopIdAndVerifierId(@Param("shopId") Long shopId, @Param("verifierId") Long verifierId);
}
