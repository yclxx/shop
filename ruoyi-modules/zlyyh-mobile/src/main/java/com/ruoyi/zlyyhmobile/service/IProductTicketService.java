package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.ProductTicketSessionVo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketVo;

import java.util.List;

public interface IProductTicketService {
    ProductTicketVo queryProductTicket(Long productId);

    List<ProductTicketSessionVo> querySessionAndLineByProductId(Long productId);
}
