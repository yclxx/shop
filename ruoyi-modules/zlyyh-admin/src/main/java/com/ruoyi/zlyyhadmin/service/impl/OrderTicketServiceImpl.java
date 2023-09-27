package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.RemoteCodeService;
import com.ruoyi.zlyyh.domain.Code;
import com.ruoyi.zlyyh.domain.OrderIdcard;
import com.ruoyi.zlyyh.domain.OrderTicket;
import com.ruoyi.zlyyh.domain.bo.LogisticsImportBo;
import com.ruoyi.zlyyh.domain.bo.OrderTicketBo;
import com.ruoyi.zlyyh.domain.vo.CodeVo;
import com.ruoyi.zlyyh.domain.vo.OrderIdcardVo;
import com.ruoyi.zlyyh.domain.vo.OrderTicketVo;
import com.ruoyi.zlyyh.mapper.CodeMapper;
import com.ruoyi.zlyyh.mapper.OrderIdcardMapper;
import com.ruoyi.zlyyh.mapper.OrderTicketMapper;
import com.ruoyi.zlyyhadmin.service.IOrderTicketService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 演出票订单Service业务层处理
 *
 * @author yzg
 * @date 2023-09-23
 */
@RequiredArgsConstructor
@Service
public class OrderTicketServiceImpl implements IOrderTicketService {
    @DubboReference(retries = 0)
    private RemoteCodeService remoteCodeService;
    private final OrderTicketMapper baseMapper;
    private final OrderIdcardMapper orderIdcardMapper;
    private final CodeMapper codeMapper;


    /**
     * 查询演出票订单
     */
    @Override
    public OrderTicketVo queryById(Long number) {
        return baseMapper.selectVoById(number);
    }

    /**
     * 查询演出票订单列表
     */
    public TableDataInfo<OrderTicketVo> selectPageUserList(OrderTicketBo bo, PageQuery pageQuery) {
        Page<OrderTicketVo> result = baseMapper.selectVoPages(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询演出票订单列表
     */
    @Override
    public List<OrderTicketVo> queryList(OrderTicketBo bo) {
        return baseMapper.selectVoLists(bo);
    }

    @Override
    public List<OrderIdcardVo> getOrderIdCardList(Long number) {
        LambdaQueryWrapper<OrderIdcard> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(OrderIdcard::getNumber, number);
        return orderIdcardMapper.selectVoList(queryWrapper);
    }

    private LambdaQueryWrapper<OrderTicket> buildQueryWrapper(OrderTicketBo bo) {
        //Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderTicket> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getNumber() != null, OrderTicket::getNumber, bo.getNumber());
        lqw.eq(bo.getProductId() != null, OrderTicket::getProductId, bo.getProductId());
        lqw.eq(bo.getSessionId() != null, OrderTicket::getSessionId, bo.getSessionId());
        lqw.eq(bo.getLineId() != null, OrderTicket::getLineId, bo.getLineId());
        lqw.eq(bo.getTicketTime() != null, OrderTicket::getTicketTime, bo.getTicketTime());
        lqw.eq(bo.getCount() != null, OrderTicket::getCount, bo.getCount());
        lqw.eq(StringUtils.isNotEmpty(bo.getLogisticsStatus()), OrderTicket::getLogisticsStatus, bo.getLogisticsStatus());
        return lqw;
    }

    @Async
    @Override
    public void importData(MultipartFile file) throws IOException {
        List<LogisticsImportBo> logisticsImportBos = ExcelUtil.importExcel(file.getInputStream(), LogisticsImportBo.class);
        if (ObjectUtil.isNotEmpty(logisticsImportBos)) {
            for (LogisticsImportBo bo : logisticsImportBos) {
                if (StringUtils.isEmpty(bo.getNumber())) continue;
                OrderTicket orderTicket = baseMapper.selectById(bo.getNumber());
                if (ObjectUtil.isEmpty(orderTicket)) continue;
                orderTicket.setLogistics(bo.getLogistics());
                orderTicket.setLogisticsStatus(bo.getLogisticsStatus());
                orderTicket.setLogisticsCom(bo.getLogisticsCom());
                baseMapper.updateById(orderTicket);

                // 物流已发货，开始核销
                if (orderTicket.getLogisticsStatus().equals("1")) {
                    // 查询订单中所有未核销的核销码
                    LambdaQueryWrapper<Code> codeWrapper = Wrappers.lambdaQuery();
                    codeWrapper.eq(Code::getNumber, orderTicket.getNumber());
                    codeWrapper.eq(Code::getUsedStatus, "0");
                    List<CodeVo> codeVos = codeMapper.selectVoList(codeWrapper);
                    if (ObjectUtil.isEmpty(codeVos)) continue;
                    // 发起核销
                    codeVos.forEach(o -> {
                        remoteCodeService.usedCode(o.getCodeNo());
                    });
                }
            }
        }
    }

    /**
     * 核销
     */
    public Boolean writeOffCode(String codeNo) {
        return remoteCodeService.usedCode(codeNo);
    }

    /**
     * 票券返还
     */
    public Boolean voidCode(String codeNo) {
        return remoteCodeService.rollbackCode(codeNo);
    }

    /**
     * 作废
     */
    public Boolean returnCode(String codeNo) {
        return remoteCodeService.cancellationCode(codeNo);
    }
}
