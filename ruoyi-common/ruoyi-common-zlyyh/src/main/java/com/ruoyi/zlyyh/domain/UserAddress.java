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
 * 用户地址对象 t_user_address
 *
 * @author yzg
 * @date 2023-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_address")
public class UserAddress extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "user_address_id")
    private Long userAddressId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 联系人
     */
    private String name;
    /**
     * 联系电话
     */
    @EncryptField()
    private String tel;
    /**
     * 是否默认 0-默认 1-不默认
     */
    private String isDefault;
    /**
     * 地址中文，省市县等，用空格隔开
     */
    private String address;
    /**
     * 地址四级联动ID，对应t_area表，多个之间用空格隔开
     */
    private String areaId;
    /**
     * 详细地址（街道门牌号啥的，全地址需要address+address_info）
     */
    private String addressInfo;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
