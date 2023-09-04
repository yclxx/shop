package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.bo.PageSettingBo;
import com.ruoyi.zlyyh.domain.vo.PageSettingVo;
import com.ruoyi.zlyyh.domain.vo.PageVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IPageService;
import com.ruoyi.zlyyhmobile.service.IPageSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 页面控制器
 * 前端访问路由地址为:/zlyyh-mobile/page/ignore
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/page/ignore")
public class PageController extends BaseController {

    private final IPageService iPageService;
    private final IPageSettingService pageSettingService;

    /**
     * 获取页面详细信息
     *
     * @param pagePath 主键
     */
    @GetMapping("/titleInfo")
    public R<PageVo> titleInfo(@NotNull(message = "缺少关键信息[pagePath]") String pagePath) {
        return R.ok(iPageService.queryByPagePath(pagePath, ZlyyhUtils.getPlatformId()));
    }

    /**
     * 获取页面配置信息
     *
     * @param pagePath 主键
     */
    @GetMapping("/pageSettingInfo")
    public R<List<PageSettingVo>> pageSettingInfo(@NotNull(message = "缺少关键信息[pagePath]") String pagePath) {
        PageSettingBo bo = new PageSettingBo();
        bo.setPagePath(pagePath);
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setStatus("0");
        return R.ok(pageSettingService.queryList(bo));
    }
}
