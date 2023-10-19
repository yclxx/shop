package com.ruoyi.zlyyh.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 购物车视图对象
 *
 * @author yzg
 * @date 2023-10-16
 */
@Data
@ExcelIgnoreUnannotated
public class CartVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 商品ID
     */
    @ExcelProperty(value = "商品ID")
    private Long productId;

    /**
     * 加入时售价
     */
    @ExcelProperty(value = "加入时售价")
    private BigDecimal createSellingPrice;

    /**
     * 数量
     */
    @ExcelProperty(value = "数量")
    private Long quantity;

    private ProductVo productVo;




}
