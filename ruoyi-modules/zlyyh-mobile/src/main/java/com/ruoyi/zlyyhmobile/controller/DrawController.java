package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.zlyyh.domain.bo.DrawBo;
import com.ruoyi.zlyyh.domain.vo.DrawVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IDrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;

/**
 * 奖品管理控制器
 * 前端访问路由地址为:/zlyyh-mobile/draw
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/draw")
public class DrawController extends BaseController {

    private final IDrawService iDrawService;

    /**
     * 查询奖品管理列表
     */
    @GetMapping("/ignore/list")
    public R<List<DrawVo>> list(DrawBo bo) {
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        List<DrawVo> drawVos = iDrawService.queryList(bo);
        Iterator<DrawVo> iterator = drawVos.iterator();
        while (iterator.hasNext()) {
            DrawVo drawVo = iterator.next();
            if (null != drawVo.getShowStartDate() && DateUtils.compare(drawVo.getShowStartDate()) > 0) {
                iterator.remove();
                continue;
            }
            if (null != drawVo.getShowEndDate() && DateUtils.compare(drawVo.getShowEndDate()) < 0) {
                iterator.remove();
            }
        }
        return R.ok(drawVos);
    }
}
