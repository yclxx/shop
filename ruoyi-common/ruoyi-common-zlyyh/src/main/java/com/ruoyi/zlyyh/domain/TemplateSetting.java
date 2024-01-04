package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 落地页配置对象 t_template_setting
 *
 * @author yzg
 * @date 2023-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_template_setting")
public class TemplateSetting extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 落地页
     */
    private Long templateId;
    /**
     * 图片
     */
    private String img;
    /**
     * 是否按钮
     */
    private String btn;
    /**
     * 跳转类型
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
     * 排序
     */
    private Long sort;

    /**
     * 图片宽度
     */
    private Long width;

    /**
     * 父级id
     */
    private Long parentId;

}
