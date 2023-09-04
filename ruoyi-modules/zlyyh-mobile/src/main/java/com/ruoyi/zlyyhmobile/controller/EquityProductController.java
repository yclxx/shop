package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.EquityProductBo;
import com.ruoyi.zlyyh.domain.vo.EquityProductVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IEquityProductService;
import com.ruoyi.zlyyhmobile.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * 权益包商品控制器
 * 前端访问路由地址为:/zlyyh/equityProduct
 *
 * @author yzg
 * @date 2023-06-06
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/equityProduct")
public class EquityProductController extends BaseController {

    private final IEquityProductService iEquityProductService;
    private final IProductService productService;

    /**
     * 查询权益包商品列表
     */
    @GetMapping("/ignore/list")
    public R<Map<String, Object>> list(EquityProductBo bo) {
        List<EquityProductVo> equityProductVos = iEquityProductService.queryList(bo);
        if (ObjectUtil.isEmpty(equityProductVos)) {
            return R.ok(null);
        }
        Map<Long, Long> collect = equityProductVos.stream().collect(HashMap::new, (m, v) -> m.put(v.getProductId(), Optional.ofNullable(v.getSort()).orElse(99L)), HashMap::putAll);
        Map<Long, ProductVo> productVoMap = productService.queryGrabPeriodProductMap((Set) collect.keySet(), ServletUtils.getHeader(ZlyyhConstants.CITY_CODE), null, ZlyyhUtils.getPlatformId());
        Map<String, Object> result = new HashMap<>(3);
        List<ProductVo> oneList = new ArrayList<>();
        List<ProductVo> twoList = new ArrayList<>();
        BigDecimal totalAmount = new BigDecimal("0");
        for (EquityProductVo equityProductVo : equityProductVos) {
            ProductVo productVo = productVoMap.get(equityProductVo.getProductId());
            if (null != productVo) {
                for (int i = 0; i < equityProductVo.getSendCount(); i++) {
                    if ("0".equals(equityProductVo.getEquityType())) {
                        oneList.add(productVo);
                        totalAmount = totalAmount.add(equityProductVo.getProductAmount());
                    } else if ("1".equals(equityProductVo.getEquityType())) {
                        twoList.add(productVo);
                        totalAmount = totalAmount.add(equityProductVo.getProductAmount());
                    }
                }
            }
        }
        result.put("oneList", oneList);
        result.put("twoList", twoList);
        result.put("totalAmount", totalAmount.intValue());
        return R.ok(result);
    }
}
