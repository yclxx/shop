package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 平台信息对象 t_platform
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_platform")
public class Platform extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 平台标识
     */
    @TableId(value = "platform_key")
    private Long platformKey;
    /**
     * 平台名称
     */
    private String platformName;
    /**
     * 小程序标题
     */
    private String platformTitle;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * appId
     */
    private String appId;
    /**
     * 小程序ID
     */
    private String encryptAppId;
    /**
     * 密钥
     */
    private String secret;
    /**
     * 对称密钥
     */
    private String symmetricKey;
    /**
     * rsa签名私钥
     */
    private String rsaPrivateKey;
    /**
     * rsa签名公钥
     */
    private String rsaPublicKey;
    /**
     * 客服电话
     */
    private String serviceTel;
    /**
     * 客服服务时间
     */
    private String serviceTime;
    /**
     * 活动城市：ALL-全部、否则填城市行政区号，多个之间用英文逗号隔开
     */
    private String platformCity;
    /**
     * 默认支付商户
     */
    private Long merchantId;
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
     * 部门id
     */
    private Long manangerDeptId;

    /**
     * 云闪付62会员权限：0-无权限，1-有权限
     */
    private String unionPayVip;
    /**
     * 支持端
     */
    private String supportChannel;
    /**
     * 供应商
     */
    private String supportSupplier;
    /**
     * 首页瀑布流显示类型
     */
    private String indexShowType;
    /**
     * 分销：0-关闭，1-开启
     */
    private String sharePermission;
    /**
     * 核销之后，分销奖励多久到账，默认T+1,0为实时到账
     */
    private Integer shareUsedDate;
    /**
     * 分销奖励类型：0-云闪付红包
     */
    private String shareAwardType;
    /**
     * 分销奖励第三方产品编号,例如：云闪付红包活动编号(对应云闪付小程序开放平台配置：营销能力包-专享红包活动编码)
     */
    private String shareAwardProductId;
    /**
     * 机构账户代码，最大32位，对应云闪付小程序开放平台配置：营销能力包-红包接入方账户
     */
    private String shareAwardInsAcctId;
    /**
     * 同一用户每月可获奖励上限,0为不限制
     */
    private BigDecimal shareAwardMonthAmount;
}
