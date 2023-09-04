package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.vo.TemplatePageVo;
import com.ruoyi.zlyyhmobile.service.ITemplatePageService;
import com.ruoyi.zlyyhmobile.service.ITemplateSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * 落地页控制器
 * 前端访问路由地址为:/zlyyh-mobile/templatePage
 *
 * @author yzg
 * @date 2023-06-09
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/templatePage")
public class TemplatePageController extends BaseController {

    private final ITemplatePageService iTemplatePageService;
    private final ITemplateSettingService templateSettingService;

    /**
     * 获取落地页详细信息
     *
     * @param templateId 主键
     */
    @GetMapping("/ignore/templateInfo/{templateId}")
    public R<TemplatePageVo> getInfo(@NotNull(message = "缺少关键信息[templateId]") @PathVariable Long templateId) {
        TemplatePageVo templatePageVo = iTemplatePageService.queryById(templateId);
        if (null != templatePageVo) {
            templatePageVo.setTemplateSettingVos(templateSettingService.queryListByTemplateId(templateId));
        }
        return R.ok(templatePageVo);
    }
}
