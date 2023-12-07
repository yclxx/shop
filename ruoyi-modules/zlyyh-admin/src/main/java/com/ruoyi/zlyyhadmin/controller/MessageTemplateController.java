package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.MessageTemplateBo;
import com.ruoyi.zlyyh.domain.vo.MessageTemplateVo;
import com.ruoyi.zlyyhadmin.domain.bo.UserInfoBo;
import com.ruoyi.zlyyhadmin.service.IMessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 消息模板控制器
 * 前端访问路由地址为:/zlyyh/messageTemplate
 *
 * @author yzg
 * @date 2023-11-23
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/messageTemplate")
public class MessageTemplateController extends BaseController {

    private final IMessageTemplateService iMessageTemplateService;

    /**
     * 查询消息模板列表
     */
    @SaCheckPermission("zlyyh:messageTemplate:list")
    @GetMapping("/list")
    public TableDataInfo<MessageTemplateVo> list(MessageTemplateBo bo, PageQuery pageQuery) {
        return iMessageTemplateService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "用户手机号", UserInfoBo.class, response);
    }

    /**
     * 导入数据
     */
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(MessageTemplateBo bo) throws Exception {
        iMessageTemplateService.importData(bo);
        return R.ok();
    }

    /**
     * 导出消息模板列表
     */
    @SaCheckPermission("zlyyh:messageTemplate:export")
    @Log(title = "消息模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MessageTemplateBo bo, HttpServletResponse response) {
        List<MessageTemplateVo> list = iMessageTemplateService.queryList(bo);
        ExcelUtil.exportExcel(list, "消息模板", MessageTemplateVo.class, response);
    }

    /**
     * 获取消息模板详细信息
     *
     * @param templateId 主键
     */
    @SaCheckPermission("zlyyh:messageTemplate:query")
    @GetMapping("/{templateId}")
    public R<MessageTemplateVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long templateId) {
        return R.ok(iMessageTemplateService.queryById(templateId));
    }

    /**
     * 新增消息模板
     */
    @SaCheckPermission("zlyyh:messageTemplate:add")
    @Log(title = "消息模板", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MessageTemplateBo bo) {
        return toAjax(iMessageTemplateService.insertByBo(bo));
    }

    /**
     * 修改消息模板
     */
    @SaCheckPermission("zlyyh:messageTemplate:edit")
    @Log(title = "消息模板", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MessageTemplateBo bo) {
        return toAjax(iMessageTemplateService.updateByBo(bo));
    }

    /**
     * 删除消息模板
     *
     * @param templateIds 主键串
     */
    @SaCheckPermission("zlyyh:messageTemplate:remove")
    @Log(title = "消息模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] templateIds) {
        return toAjax(iMessageTemplateService.deleteWithValidByIds(Arrays.asList(templateIds), true));
    }
}
