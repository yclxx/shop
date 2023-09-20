package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zlyyh.domain.ProductTicket;
import com.ruoyi.zlyyh.domain.ProductTicketLine;
import com.ruoyi.zlyyh.domain.ProductTicketSession;
import com.ruoyi.zlyyh.domain.vo.ProductTicketLineVo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketSessionVo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketVo;
import com.ruoyi.zlyyh.mapper.ProductTicketLineMapper;
import com.ruoyi.zlyyh.mapper.ProductTicketMapper;
import com.ruoyi.zlyyh.mapper.ProductTicketSessionMapper;
import com.ruoyi.zlyyhmobile.service.IProductTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductTicketServiceImpl implements IProductTicketService {
    private final ProductTicketMapper baseMapper;
    private final ProductTicketSessionMapper productTicketSessionMapper;
    private final ProductTicketLineMapper productTicketLineMapper;

    public ProductTicketVo queryProductTicket(Long productId) {
        LambdaQueryWrapper<ProductTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductTicket::getProductId, productId);
        return baseMapper.selectVoOne(wrapper);
    }

    public List<ProductTicketSessionVo> querySessionAndLineByProductId(Long productId) {
        LambdaQueryWrapper<ProductTicketSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductTicketSession::getProductId, productId);
        wrapper.eq(ProductTicketSession::getStatus, "0");
        List<ProductTicketSessionVo> ticketSessionVos = productTicketSessionMapper.selectVoList(wrapper);
        ticketSessionVos.forEach(o -> {
            LambdaQueryWrapper<ProductTicketLine> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(ProductTicketLine::getProductId, o.getProductId());
            wrapper2.eq(ProductTicketLine::getSessionId, o.getSessionId());
            wrapper2.eq(ProductTicketLine::getLineStatus, "0");
            List<ProductTicketLineVo> productTicketLineVos = productTicketLineMapper.selectVoList(wrapper2);
            o.setTicketLine(productTicketLineVos);
        });
        return ticketSessionVos;
    }
}
