package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 演出票订单对象 t_order_ticket
 *
 * @author yzg
 * @date 2023-09-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_ticket")
public class OrderTicket extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @TableId(value = "number")
    private Long number;
    /**
     * 产品id
     */
    private Long productId;
    /**
     * 场次id
     */
    private Long sessionId;
    /**
     * 票种id
     */
    private Long lineId;
    /**
     * 订单状态 0 正常 1停用
     */
    private String status;
    /**
     * 观影时间
     */
    private Date ticketTime;
    /**
     * 购买金额
     */
    private BigDecimal price;
    /**
     * 结算金额
     */
    private BigDecimal sellPrice;
    /**
     * 购买数量
     */
    private Long count;
    /**
     * 用户地址id
     */
    private Long userAddressId;
    /**
     * 联系人
     */
    private String name;
    /**
     * 联系电话
     */
    private String tel;
    /**
     * 地址中文，省市县等，用空格隔开
     */
    private String address;
    /**
     * 详细地址（街道门牌号啥的，全地址需要address+address_info）
     */
    private String addressInfo;
    /**
     * 门店id
     */
    private Long shopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 门店地址
     */
    private String shopAddress;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;
    /**
     * 票形式
     */
    private String ticketForm;
    /**
     * 是否不支持退款
     */
    private String ticketNonsupport;
    /**
     * 是否电子发票
     */
    private String ticketInvoice;
    /**
     * 是否过期退
     */
    private String ticketExpired;
    /**
     * 随时退
     */
    private String ticketAnyTime;
    /**
     * 选座方式
     */
    private String ticketChooseSeat;
    /**
     * 快递方式
     */
    private String ticketPostWay;
    /**
     * 邮费
     */
    private BigDecimal ticketPostage;
    /**
     * 物流单号
     */
    private String logistics;
    /**
     * 状态
     */
    private String logisticsStatus;
    /**
     * 物流公司
     */
    private String logisticsCom;

}
