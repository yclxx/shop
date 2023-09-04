package com.ruoyi.zlyyhmobile.domain.bo;

import com.ruoyi.common.core.utils.StringUtils;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 创建订单
 *
 * @author 25487
 */
@Data
public class CreateOrderBo {

    /**
     * 产品ID
     */
    @NotNull(message = "产品不能为空")
    private Long productId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 城市编码
     */
    private String adcode;

    /**
     * 所在城市
     */
    private String cityName;

    /**
     * 父级订单号
     */
    private Long parentNumber;

    /**
     * 购买数量
     */
    private Long payCount;

    /**
     * 平台标识
     */
    private Long platformKey;

    /**
     * 获取城市编码
     *
     * @return 城市编码 例如杭州：330100
     */
    public String getCityCode() {
        if (StringUtils.isBlank(this.adcode)) {
            return this.adcode;
        }
        return this.adcode.substring(0, 4) + "00";
    }
}
