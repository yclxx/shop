package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustOrderBo extends BaseEntity {

    private Long orderId;

    private Long goodsId;

    private Long custId;

    private String goodsName;

    private String mainImage;

    private String type;

    private BigDecimal totalPrice;

    private Integer totalPoint;

    private BigDecimal reducedPrice;

    private BigDecimal discountPrice;

    private Integer realPricePoint;

    private Date payTime;

    private Date expireDate;

    private Integer totalNum;

    private Integer status;

    private String mobile;

    private Integer exgStatus;

    private Date createTime;

    private Date updateTime;
}
