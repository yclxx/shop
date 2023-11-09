package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.TreeEntity;

/**
 * 分销员业务对象
 *
 * @author yzg
 * @date 2023-11-09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ShareUserBo extends TreeEntity<ShareUserBo> {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { EditGroup.class })
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
     * 备注
     */
    private String remark;

    /**
     * 平台标识
     */
    private Long platformKey;


}
