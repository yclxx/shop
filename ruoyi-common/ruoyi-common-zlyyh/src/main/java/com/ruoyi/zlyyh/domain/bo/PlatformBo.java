package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 平台信息业务对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatformBo extends BaseEntity {

    /**
     * 平台标识
     */
    @NotNull(message = "平台标识不能为空", groups = { EditGroup.class })
    private Long platformKey;

    /**
     * 平台名称
     */
    @NotBlank(message = "平台名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String platformName;

    /**
     * 小程序标题
     */
    @NotBlank(message = "小程序标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String platformTitle;

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * appId
     */
    @NotBlank(message = "appId不能为空", groups = { AddGroup.class, EditGroup.class })
    private String appId;

    /**
     * 小程序ID
     */
    @NotBlank(message = "小程序ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String encryptAppId;

    /**
     * 密钥
     */
    @NotBlank(message = "密钥不能为空", groups = { AddGroup.class, EditGroup.class })
    private String secret;

    /**
     * 对称密钥
     */
    @NotBlank(message = "对称密钥不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotNull(message = "默认支付商户不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long merchantId;

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
}
