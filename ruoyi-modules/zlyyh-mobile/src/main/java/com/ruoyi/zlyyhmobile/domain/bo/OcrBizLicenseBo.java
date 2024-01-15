package com.ruoyi.zlyyhmobile.domain.bo;

import com.ruoyi.common.core.validate.AppEditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OcrBizLicenseBo {
    /**
     * 需要识别的图片地址
     */
    @NotBlank(message = "识别图片不能为空", groups = {AppEditGroup.class})
    private String imgUrl;
}
