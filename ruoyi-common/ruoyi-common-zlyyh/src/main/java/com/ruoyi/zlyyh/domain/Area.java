package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.core.web.domain.TreeAreaEntity;
import com.ruoyi.common.core.web.domain.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行政区对象 t_area
 *
 * @author ruoyi
 * @date 2023-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_area")
public class Area extends TreeAreaEntity<Area> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id")
    private Long id;

    /**
     * 行政编码
     */
    private Long adcode;
    /**
     * 行政区名称
     */
    private String areaName;
    /**
     * 行政区划级别,country:国家,province:省份（直辖市会在province和city显示）,city:市（直辖市会在province和city显示）,district:区县,street:街道
     */
    private String level;
    /**
     * 首字母
     */
    private String firstLetter;
    /**
     * 上级区域编码,000000代表无上级
     */
    private Long parentCode;

}
