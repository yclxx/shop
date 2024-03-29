package com.ruoyi.common.core.web.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TreeCityEntity<T> extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单名称
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 父菜单ID
     */
    @TableField(exist = false)
    private Long parentId;

    /**
     * 子部门
     */
    @TableField(exist = false)
    private List<T> children = new ArrayList<>();
}
