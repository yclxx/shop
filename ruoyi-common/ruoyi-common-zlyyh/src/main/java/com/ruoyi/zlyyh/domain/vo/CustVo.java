package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import lombok.Data;

import java.util.Date;

/**
 * 商户号视图对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@ExcelIgnoreUnannotated
public class CustVo {

    private static final long serialVersionUID = 1L;

    private Long custId;

    private String unionpayOpenid;

    private String mobile;

    private String faceUrl;

    private String realName;

    private Date createTime;

    private Date updateTime;
}
