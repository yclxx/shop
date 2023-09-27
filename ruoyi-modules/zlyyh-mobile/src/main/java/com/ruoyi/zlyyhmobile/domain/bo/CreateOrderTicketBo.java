package com.ruoyi.zlyyhmobile.domain.bo;

import com.ruoyi.common.core.utils.StringUtils;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateOrderTicketBo {
    /**
     * 票档ID
     */
    @NotNull(message = "票档不能为空")
    private Long lineId;
    /**
     * 产品ID
     */
    @NotNull(message = "产品不能为空")
    private Long productId;
    /**
     * 门店id
     */
    private Long shopId;
    /**
     * 收货地址id
     */
    private Long addressId;
    /**
     * 身份信息合集
     */
    private List<Long> cardList;
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
     * 购买数量
     */
    private Long payCount;

    /**
     * 平台标识
     */
    private Long platformKey;

    private String reservation;

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
