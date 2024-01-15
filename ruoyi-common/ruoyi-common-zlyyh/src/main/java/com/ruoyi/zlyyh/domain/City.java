package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.web.domain.TreeCityEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.TreeEntity;

/**
 * 行政区对象 t_city
 *
 * @author yzg
 * @date 2024-01-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_city")
public class City extends TreeCityEntity<City> {

    private static final long serialVersionUID=1L;

    /**
     * 区域编码
     */
    @TableId(value = "adcode")
    private Long adcode;
    /**
     * 城市编码
     */
    private String citycode;
    /**
     * 行政区名称
     */
    private String areaName;
    /**
     * 行政区划级别  country:国家  province:省份（直辖市会在province显示）  city:市（直辖市会在province显示）  district:区县  street:街道
     */
    private String level;
    /**
     * 上级区域编码  000000-代表无上级
     */
    private Long parentCode;
    /**
     * 首字母
     */
    private String firstLetter;
    /**
     * 删除标志  0-存在  2-删除
     */
    @TableLogic
    private String delFlag;

}
