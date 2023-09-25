package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.OrderTicketBo;
import com.ruoyi.zlyyh.domain.vo.OrderIdcardVo;
import com.ruoyi.zlyyh.domain.vo.OrderTicketVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 演出票订单Service接口
 *
 * @author yzg
 * @date 2023-09-23
 */
public interface IOrderTicketService {

    /**
     * 查询演出票订单
     */
    OrderTicketVo queryById(Long number);

    /**
     * 查询演出票订单列表
     */
    TableDataInfo<OrderTicketVo> queryPageList(OrderTicketBo bo, PageQuery pageQuery);

    /**
     * 查询演出票订单列表
     */
    List<OrderTicketVo> queryList(OrderTicketBo bo);

    /**
     * 查询观影人信息
     */
    List<OrderIdcardVo> getOrderIdCardList(Long number);

    /**
     * 物流信息导入
     */
    void importData(MultipartFile file) throws IOException;

    /**
     * 核销
     */
    Boolean writeOffCode(String codeNo);

    /**
     * 票券返还
     */
    Boolean voidCode(String codeNo);

    /**
     * 作废
     */
    Boolean returnCode(String codeNo);

}
