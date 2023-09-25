package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.ProductTicketLineVo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketSessionVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;

public interface TicketService {
    ProductVo queryTicketById(Long productId);

    ProductTicketSessionVo getProductTicketLine(Long lineId);
}
