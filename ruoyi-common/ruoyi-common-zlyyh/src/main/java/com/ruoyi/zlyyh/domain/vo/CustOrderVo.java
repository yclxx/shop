package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
public class CustOrderVo {

    private static final long serialVersionUID = 1L;

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
