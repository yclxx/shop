package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.TreeEntity;
import com.ruoyi.common.encrypt.annotation.EncryptField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 分销员对象 t_share_user
 *
 * @author yzg
 * @date 2023-11-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_share_user")
public class ShareUser extends TreeEntity<ShareUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;
    /**
     * 商圈名称
     */
    private String businessDistrictName;
    /**
     * 品牌名称
     */
    private String commercialTenantName;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 云闪付手机号
     */
    @EncryptField()
    private String upMobile;
    /**
     * 状态
     */
    private String status;
    /**
     * 审核状态
     */
    private String auditStatus;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;
    /**
     * 部门id
     */
    private Long sysDeptId;
    /**
     * 用户id
     */
    private Long sysUserId;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄段
     */
    private String ageType;
}
