package com.ruoyi.zlyyhadmin.domain.bo;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 25487
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopImportDataBo extends BaseEntity {
    private Long platformKey;
    private Long productId;
}
