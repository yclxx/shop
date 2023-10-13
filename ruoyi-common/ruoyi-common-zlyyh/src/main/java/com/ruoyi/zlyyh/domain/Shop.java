package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 门店对象 t_shop
 *
 * @author yzgnet
 * @date 2023-09-16
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
     * 展示开始时间
     */
    private Date showStartDate;
    /**
     * 展示结束时间
     */
    private Date showEndDate;
    /**
     * 指定周几: 0-不指定，1-指定周几
     */
    private String assignDate;
    /**
     * 周几展示：1-周日，2-周一，3-周二...7-周六，多个之间用英文逗号隔开
     */
    private String weekDate;
    /**
     * 几点开始展示,几点结束,格式：HH:mm:ss-HH:mm:ss
     */
    private String sellTime;
    /**
     * 门店图片
     */
    private String shopImgs;
    /**
     * 门店logo
     */
    private String shopLogo;
    /**
     * 排序：从小到大
     */
    private Long sort;
    /**
     * 标签，多个之间用英文逗号隔开
     */
    private String shopTags;
    /**
     * 门店类型：0-餐饮，1-便利店，2-茶饮，3-酒店，4-景点，5-演出，6-电影院
     */
    private String shopType;
    /**
     * 商户拓展服务商表
     */
    private Long extensionServiceProviderId;
    /**
     * 推荐类型：0-不推荐，1-商圈精选，2-品牌精选
     */
    private String pushType;
    /**
     * 是否共享
     */
    private String is_share;
    /**
     * 供应商（与supplierShopId无关系）
     */
    private String supplier;
    /**
     * 营业执照
     */
    private String license;
    /**
     * 性质
     */
    private String nature;
    /**
     * 发票类型
     */
    private String invoice;
    /**
     * 收款账户
     */
    private String account;
    /**
     * 活动类型
     */
    private String activity;
    /**
     * 支持端
     */
    private String supportChannel;
    /**
     * 供应商门店id
     */
    private String supplierShopId;
    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;

}
