package com.ruoyi.auth.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录对象
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
public class AppLoginBody {

    /**
     * 授权code
     */
    @NotBlank(message = "{xcx.code.not.blank}")
    private String xcxCode;

    /**
     * 登录城市
     */
    private String cityName;

    /**
     * 登录城市 行政区号
     */
    private String cityCode;
}
