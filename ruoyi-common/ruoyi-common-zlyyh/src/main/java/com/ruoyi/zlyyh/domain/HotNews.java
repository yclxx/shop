package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.util.Date;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 热门搜索配置对象 t_hot_news
 *
 * @author yzg
 * @date 2023-07-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_hot_news")
public class HotNews extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "news_id")
    private Long newsId;
    /**
     * 显示名称
     */
    private String newsName;
    /**
     * 排序，从小到大
     */
    private Long newsRank;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 生效时间
     */
    private Date startTime;
    /**
     * 失效时间
     */
    private Date endTime;
    /**
     * 指定星期
     */
    private String assignDate;
    /**
     * 展示城市
     */
    private String showCity;
    /**
     * 展示星期
     */
    private String weekDate;

}
