package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单数据统计（月份）对象 t_product_compute_month
 *
 * @author yzg
 * @date 2023-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product_compute_month")
public class ProductComputeMonth extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 统计月份
     */
    private String month;
    /**
     * 商品编号
     */
    private Long productId;
    /**
     * 行政区号
     */
    private String cityCode;
    /**
     * 城市
     */
    private String cityName;
    /**
     * 每个城市用户人数
     */
    private Long cityUserNumber;
    /**
     * 每个城市订单数量
     */
    private Long cityOrderNumber;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
