package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

/**
 * 消息模板业务对象
 *
 * @author yzg
 * @date 2023-11-23
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageTemplateBo extends BaseEntity {
    private Long templateId;
    private String status;
    private Long platformKey;
    private String templateKey;
    private String templateName;
    private String channel;
    private String keyword;
    private MultipartFile file;
}
