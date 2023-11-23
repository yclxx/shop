package com.ruoyi.zlyyhadmin.domain.bo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户信息统一模板
 */
@Data
@ExcelIgnoreUnannotated
public class UserInfoBo {
    private static final long serialVersionUID = 1L;
    /**
     * 物流单号
     */
    @ExcelProperty(value = "用户手机号")
    private String mobile;
}
