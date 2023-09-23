package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.annotation.Sensitive;
import com.ruoyi.common.core.enums.SensitiveStrategy;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import com.ruoyi.zlyyh.domain.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 演出票订单视图对象
 *
 * @author yzg
 * @date 2023-09-22
 */
@Data
@ExcelIgnoreUnannotated
public class OrderTicketVo {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private Long number;

    /**
     * 产品id
     */
    @ExcelProperty(value = "产品id")
    private Long productId;

    /**
     * 场次id
     */
    @ExcelProperty(value = "场次id")
    private Long sessionId;

    /**
     * 票种id
     */
    @ExcelProperty(value = "票种id")
    private Long lineId;

    /**
     * 观影时间
     */
    @ExcelProperty(value = "观影时间")
    private Date ticketTime;

    /**
     * 购买金额
     */
    @ExcelProperty(value = "购买金额")
    private BigDecimal price;

    /**
     * 结算金额
     */
    @ExcelProperty(value = "结算金额")
    private BigDecimal sellPrice;

    /**
     * 购买数量
     */
    @ExcelProperty(value = "购买数量")
    private Long count;

    /**
     * 用户地址id
     */
    @ExcelIgnore
    private Long userAddressId;

    /**
     * 联系人
     */
    @ExcelProperty(value = "联系人")
    private String name;

    /**
     * 联系电话
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    @ExcelProperty(value = "联系电话")
    private String tel;

    /**
     * 地址中文，省市县等，用空格隔开
     */
    @ExcelProperty(value = "地址中文，省市县等，用空格隔开")
    private String address;

    /**
     * 详细地址（街道门牌号啥的，全地址需要address+address_info）
     */
    @ExcelProperty(value = "详细地址", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "街=道门牌号啥的，全地址需要address+address_info")
    private String addressInfo;

    /**
     * 门店id
     */
    @ExcelIgnore
    private Long shopId;

    /**
     * 门店名称
     */
    @ExcelProperty(value = "门店名称")
    private String shopName;

    /**
     * 门店地址
     */
    @ExcelProperty(value = "门店地址")
    private String shopAddress;
    /**
     * 是否不支持退款
     */
    @ExcelIgnore
    private String ticketNonsupport;
    /**
     * 是否电子发票
     */
    @ExcelIgnore
    private String ticketInvoice;
    /**
     * 是否过期退
     */
    @ExcelIgnore
    private String ticketExpired;
    /**
     * 随时退
     */
    @ExcelIgnore
    private String ticketAnyTime;
    /**
     * 选座方式
     */
    @ExcelIgnore
    private String ticketChooseSeat;
    /**
     * 票形式
     */
    @ExcelProperty(value = "票形式")
    private String ticketForm;
    /**
     * 快递方式
     */
    @ExcelProperty(value = "快递方式")
    private String ticketPostWay;
    /**
     * 邮费
     */
    @ExcelProperty(value = "邮费")
    private BigDecimal ticketPostage;
    /**
     * 物流单号
     */
    @ExcelIgnore
    private String logistics;
    /**
     * 状态
     */
    @ExcelIgnore
    private String logisticsStatus;
    /**
     * 物流公司
     */
    @ExcelIgnore
    private String logisticsCom;

    @ExcelIgnore
    private Order order;

    /**
     * 观影人信息
     */
    @ExcelIgnore
    private List<OrderIdcardVo> orderIdCardVos;
    /**
     * 券码
     */
    @ExcelIgnore
    private List<CodeVo> codeVos;
}
