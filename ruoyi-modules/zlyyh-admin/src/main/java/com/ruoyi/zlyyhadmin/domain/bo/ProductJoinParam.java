package com.ruoyi.zlyyhadmin.domain.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProductJoinParam implements Serializable {
    private Long platformKey;
    private Long productId;
    private String productName;
    private String productType;
    private String productAffiliation;
    private String pickupMethod;
    private String status;
    private String externalProductId;
    private Date showStartDate;
    private Date showEndDate;
    private String showCity;
    private String showIndex;
    private Long actionId;
    private Long couponId;
    private Long shopId;
    private Long sort;
    private Map<String, Object> params = new HashMap<>();
}
