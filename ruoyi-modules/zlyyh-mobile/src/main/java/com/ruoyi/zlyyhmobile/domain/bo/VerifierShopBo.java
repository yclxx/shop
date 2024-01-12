package com.ruoyi.zlyyhmobile.domain.bo;

import com.ruoyi.common.core.validate.AppEditGroup;
import com.ruoyi.zlyyh.domain.bo.ShopMerchantBo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 添加门店
 */
@Data
public class VerifierShopBo {
    /**
     * 门店Id
     */
        private Long shopId;
    /**
     * 商户Id
     */
    @NotNull(message = "商户不能为空", groups = {AppEditGroup.class})
    private Long commercialTenantId;

    /**
     * 营业执照
     */
    @NotBlank(message = "营业执照不能为空", groups = {AppEditGroup.class})
    private String license;

    /**
     * 门店名称
     */
    @NotBlank(message = "门店名称不能为空", groups = {AppEditGroup.class})
    private String shopName;

    /**
     * 门店地址
     */
    @NotBlank(message = "门店地址不能为空", groups = {AppEditGroup.class})
    private String address;
    /**
     * 经度,基于高德地图
     */
    private BigDecimal longitude;

    /**
     * 纬度,基于高德地图
     */
    private BigDecimal latitude;
    /**
     * 门店电话
     */
    private String shopTel;
    /**
     * 门店图片 多个直接英文逗号隔开
     */
    private String shopImgs;
    /**
     * 营业时间 星期 多个直接英文逗号隔开
     */
    private String weekDate;
    /**
     * 营业时间 小时，格式 HH:mm-HH:mm
     */
    private String businessHours;
    /**
     * 节假日是否营业
     */
    private Boolean holiday;
    /**
     * 自动匹配商圈
     */
    private String autoBusiness;
    /**
     * 商圈Id集合
     */
    private List<Long> businessDistrictIds;
    /**
     * 门店商编
     */
    private List<ShopMerchantBo> shopMerchantBos;
}
