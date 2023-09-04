package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 城市商户对象 t_city_merchant
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@TableName("t_city_merchant")
public class CityMerchant implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 行政编码
     */
    private String adcode;
    /**
     * 行政区名称
     */
    private String areaName;
    /**
     * 商户ID
     */
    private Long merchantId;

}
