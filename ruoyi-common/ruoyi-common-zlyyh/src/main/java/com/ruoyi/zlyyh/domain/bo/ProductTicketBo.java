package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 演出票业务对象
 *
 * @author yzg
 * @date 2023-09-11
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductTicketBo extends BaseEntity {

    /**
     * 演出票id
     */
    @NotNull(message = "演出票id不能为空", groups = {EditGroup.class})
    private Long ticketId;

    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long productId;
    /**
     * 状态
     */
    @NotBlank(message = "状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ticketStatus;
    /**
     * 票种
     */
    @NotBlank(message = "票种不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ticketTicketType;
    /**
     * 选座方式
     */
    @NotBlank(message = "选座方式不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ticketChooseSeat;
    /**
     * 票形式
     */
    @NotBlank(message = "票形式不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ticketForm;
    /**
     * 是否需要身份信息
     */
    @NotBlank(message = "身份信息不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ticketCard;
    /**
     * 服务方式
     */
    @NotBlank(message = "服务方式不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ticketService;
    /**
     * 快递方式
     */
    @NotBlank(message = "快递方式不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ticketPostWay;
    /**
     * 邮费
     */
    private BigDecimal ticketPostage;
    /**
     * 须知
     */
    private String ticketNotice;
}
