package com.ruoyi.zlyyh.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Damon
 * @Description: 联联接口请求参数
 * @date 2023/07/03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LianLianParam implements Serializable {

    private String channelId;
    private String encryptedData;
    private String sign;
    private Long timestamp;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductListParam implements Serializable {
        // 产品列表参数
        private String channelId;
        private String cityCode;
        private Integer pageNum;
        private Integer pageSize;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDetailParam implements Serializable {
        // 产品详情（图文）参数
        private String productId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateOrderParam implements Serializable {
        // 创建渠道订单-并自动发码参数
        private String validToken;
        private String thirdPartyOrderNo;
        private Integer productId;
        private String itemId;
        private String settlePrice;
        private String travelDate;
        private String idCard;
        private String customerName;
        private String customerPhoneNumber;
        private String address;
        private Integer payType;
        private Integer quantity;
        private String purchaseTime;
        private Integer thirdSalePrice;
        private String locationId;
        private String memo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderQueryParam implements Serializable {
        // 查询订单详情参数
        private String channelId;
        private String channelOrderId;
        private String thirdOrderId;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderReturnParam implements Serializable {
        private String thirdOrderId;//第三方订单号
        private String channelOrderId;// 联联渠道订单号
        private String orderId;// 小订单号
        private Integer channelId;// 渠道编号
        private BigDecimal chargeAmount;// 手续费
        private BigDecimal applyAmount;// 申请退款金额
        private BigDecimal refundAmount;// 申请退款金额
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShopList implements Serializable {
        // 查询产品列表参数
        private Long id;
        private String name;
        private String latitude; //纬度
        private String longitude; //经度
        private String cityCode; //城市编码
        private String address;
        private String phoneNumber;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductInfoParam implements Serializable {
        // 查询产品详情参数
        private String channelId;//产品编号
        private String productId;//产品编号
        //private List<Long> itemIdList;//套餐编号列表
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckOrderParam implements Serializable {
        // 验证-渠道订单创建条件参数
        private String locationId;
        private String thirdPartyOrderNo;
        private Integer productId;
        private String itemId;
        private String settlePrice;
        private String travelDate;
        private String idCard;
        private String customerName;
        private String customerPhoneNumber;
        private String address;
        private Integer payType;
        private Integer quantity;
        private String purchaseTime;
        private String thirdSalePrice;
        private String memo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReSendSmsParam implements Serializable {
        // 重发短信参数
        private String channelOrderId;//渠道订单编号
        private String cOrderId;//渠道订单编号
        private List<String> orderIdList;//小订单列表
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductStateParam implements Serializable {//联联产品上下架售罄通知
        private Integer productId;
        private Integer type;//类型 0:下架 1:上架 2:售罄
        private String items;//:[1000324,1000325] //套餐 id 列表
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApplyRefundParam implements Serializable {
        // 联联订单退款
        private String channelOrderNo;//渠道订单号
        private List<String> orderNoList;//退款小订单号列表
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderCheckParam implements Serializable {//订单核销参数
        private String channelId;//第三方订单号
        private String channelOrderId;//渠道订单号
        private String thirdOrderId;//渠道订单号
        private String orderId;// 小订单号
        private String corderId;// 中订单号
        private String completeDate;//核销时间
    }
}
