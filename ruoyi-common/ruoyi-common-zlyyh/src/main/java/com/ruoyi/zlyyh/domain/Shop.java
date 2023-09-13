package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 门店对象 t_shop
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop")
public class Shop extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "shop_id")
    private Long shopId;
    /**
     * 商户ID
     */
    private Long commercialTenantId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 门店电话
     */
    private String shopTel;
    /**
     * 营业时间
     */
    private String businessHours;
    /**
     * 门店地址
     */
    private String address;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码
     */
    private String formattedAddress;
    /**
     * 省份名
     */
    private String province;
    /**
     * 城市名
     */
    private String city;
    /**
     * 地址所在区
     */
    private String district;
    /**
     * 省份编码（行政区号例如：浙江330000）
     */
    private String procode;
    /**
     * 城市编码（行政区号例如：杭州330100）
     */
    private String citycode;
    /**
     * 区域编码（行政区号例如：拱墅区330105）
     */
    private String adcode;
    /**
     * 经度,基于高德地图
     */
    private BigDecimal longitude;
    /**
     * 纬度,基于高德地图
     */
    private BigDecimal latitude;
    /**
     * 平台标识
     */
    private Long platformKey;

    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;
}
