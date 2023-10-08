package com.ruoyi.zlyyhadmin.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LianLianProductBo implements Serializable {
    private Integer productId;
    private String onlyName;
    private String productSimpleDesc;
    private String shareText;
    private String title;
    private String channelMallImg;
    private Integer saleAmount;
    private Integer stockAmount;
    private Integer itemStock;
    private Long salePrice;
    private Long originPrice;
    private Integer isSoldOut;
    private String faceImg;
    private String shareImg;
    private String attention;
    private Integer bookingType;
    private String bookingShowAddress;
    private String orderShowIdCard;
    private String orderShowDate;
    private String beginTime;
    private String endTime;
    private String validBeginDate;
    private String validEndDate;
    private String releaseTime;
    private Integer singleMin;
    private Integer singleMax;
    private String ecommerce;
    private String categoryPath;
    private String productCategoryId;
    private String categoryName;
    private String qualificationsList;
    private String itemList;
    private String shopList;
    private String noticeVOList;
    private String productItemsContentMap;
    private String imgList;
}
