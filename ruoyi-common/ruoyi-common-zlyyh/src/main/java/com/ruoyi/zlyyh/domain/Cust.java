package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@TableName("t_cust")
public class Cust {

    private static final long serialVersionUID=1L;

    @TableId(value = "cust_id")
    private Long custId;

    private String unionpayOpenid;

    private String mobile;

    private String faceUrl;

    private String realName;

    private Date createTime;

    private Date updateTime;
}
