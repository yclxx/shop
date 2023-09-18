package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.encrypt.annotation.EncryptField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 观影用户信息对象 t_user_idcard
 *
 * @author yzg
 * @date 2023-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_idcard")
public class UserIdcard extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "user_idcard_id")
    private Long userIdcardId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 真实姓名
     */
    private String name;
    /**
     * 证件类型0-身份证 1-护照 2-港澳台居民居住证 3-户口簿
     */
    private String cardType;
    /**
     * 证件号
     */
    @EncryptField()
    private String idCard;

}
