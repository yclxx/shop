package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 广告管理对象 t_banner
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_banner")
public class Banner extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "banner_id")
    private Long bannerId;
    /**
     * 显示名称
     */
    private String bannerName;
    /**
     * 角标
     */
    private String bannerMark;
    /**
     * banner图
     */
    private String bannerImage;
    /**
     * 排序，从小到大
     */
    private Long bannerRank;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * banner类型：0-顶部广告,1-icon,2-长图轮播，3-腰部广告(两张)，4-腰部广告(三张)，5-浮框，6-弹窗，7-银行专属优惠
     */
    private String bannerType;
    /**
     * 显示页面，对应t_page中page_path字段
     */
    private String pagePath;
    /**
     * 跳转类型：0-无需跳转，1-内部页面，2-外部页面，3-小程序跳转，4-图片页面，5-RN跳转，6-页面指定位置
     */
    private String toType;
    /**
     * 小程序ID
     */
    private String appId;
    /**
     * 页面地址
     */
    private String url;
    /**
     * 生效时间
     */
    private Date startTime;
    /**
     * 失效时间
     */
    private Date endTime;
    /**
     * 展示维度：0-全部、1-新用户、2-老用户
     */
    private String showDimension;
    /**
     * 展示城市：ALL-全部、否则填城市行政区号，多个之间用英文逗号隔开
     */
    private String showCity;
    /**
     * 平台标识
     */
    private Long platformKey;

    /**
     * 指定周几: 0-不指定，1-指定周几
     */
    private String assignDate;
    /**
     * 周几能领：1-周日，2-周一，3-周二...7-周六，多个之间用英文逗号隔开
     */
    private String weekDate;

}
