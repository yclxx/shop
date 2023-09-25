package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zlyyh.domain.ProductTicket;
import com.ruoyi.zlyyh.domain.ProductTicketLine;
import com.ruoyi.zlyyh.domain.vo.ProductTicketLineVo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketSessionVo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyh.mapper.ProductTicketLineMapper;
import com.ruoyi.zlyyh.mapper.ProductTicketMapper;
import com.ruoyi.zlyyh.mapper.ProductTicketSessionMapper;
import com.ruoyi.zlyyhmobile.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {
    private final ProductMapper productMapper;
    private final ProductTicketMapper ticketMapper;
    private final ProductTicketSessionMapper ticketSessionMapper;
    private final ProductTicketLineMapper ticketLineMapper;

    @Override
    public ProductVo queryTicketById(Long productId) {
        ProductVo productVo = productMapper.selectVoById(productId);
        if (null == productVo) {
            return null;
        }
        LambdaQueryWrapper<ProductTicket> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductTicket::getProductId, productId);
        ProductTicketVo productTicketVo = ticketMapper.selectVoOne(queryWrapper);
        productVo.setTicket(productTicketVo);
        return productVo;
    }

    /**
     * 查询场次与票种信息
     */
    public ProductTicketSessionVo getProductTicketLine(Long lineId) {
        LambdaQueryWrapper<ProductTicketLine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductTicketLine::getLineId, lineId);
        queryWrapper.eq(ProductTicketLine::getLineStatus, "0");
        ProductTicketLineVo lineVo = ticketLineMapper.selectVoById(lineId);
        ProductTicketSessionVo sessionVo = ticketSessionMapper.selectVoById(lineVo.getSessionId());
        sessionVo.setTicketLine(ListUtils.newArrayList(lineVo));
        return sessionVo;
    }
}
