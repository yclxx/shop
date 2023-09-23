package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketSessionVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyhmobile.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * 演出票相关信息查询
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    /**
     * 获取商品详情
     *
     * @return 商品列表
     */
    @GetMapping("/getProductTicketInfo")
    public R<ProductVo> getProductTicketInfo(ProductBo productBo) {
        ProductVo productVo = ticketService.queryTicketById(productBo.getProductId());
        return R.ok(productVo);
    }

    @GetMapping("/line/{lineId}")
    public R<ProductTicketSessionVo> getProductTicketLine(@NotNull(message = "主键不能为空") @PathVariable Long lineId) {
        return R.ok(ticketService.getProductTicketLine(lineId));
    }

}
