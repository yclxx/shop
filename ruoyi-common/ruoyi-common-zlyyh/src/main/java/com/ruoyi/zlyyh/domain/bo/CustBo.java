package com.ruoyi.zlyyh.domain.bo;


import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustBo extends BaseEntity {

    private Long custId;

    private String unionpayOpenid;

    private String mobile;

    private String faceUrl;

    private String realName;

    private Date createTime;

    private Date updateTime;
}
