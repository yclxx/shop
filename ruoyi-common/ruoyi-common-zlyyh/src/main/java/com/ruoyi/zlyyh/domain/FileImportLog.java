package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 文件导入记录对象 t_file_import_log
 *
 * @author yzg
 * @date 2024-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_file_import_log")
public class FileImportLog extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 文件导入记录id
     */
    @TableId(value = "import_log_id")
    private Long importLogId;
    /**
     * 文件地址
     */
    private String url;
    /**
     * 文件名
     */
    private String name;
    /**
     * 数据数量
     */
    private Long count;
    /**
     * 导入数据数量
     */
    private Long importCount;
    /**
     * 删除标志  0-存在  2-删除
     */
    @TableLogic
    private String delFlag;

    /**
     * 页面标题
     */
    private String pageTitle;

    private String pageUrl;

}
