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
import com.ruoyi.zlyyh.domain.bo.MarketLogBo;
import com.ruoyi.zlyyh.domain.vo.MarketLogVo;
import com.ruoyi.zlyyhadmin.service.IMarketLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 奖励发放记录控制器
 * 前端访问路由地址为:/zlyyh/marketLog
 *
 * @author yzg
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/marketLog")
public class MarketLogController extends BaseController {

    private final IMarketLogService iMarketLogService;

    /**
     * 查询奖励发放记录列表
     */
    @SaCheckPermission("zlyyh:marketLog:list")
    @GetMapping("/list")
    public TableDataInfo<MarketLogVo> list(MarketLogBo bo, PageQuery pageQuery) {
        return iMarketLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出奖励发放记录列表
     */
    @SaCheckPermission("zlyyh:marketLog:export")
    @Log(title = "奖励发放记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MarketLogBo bo, HttpServletResponse response) {
        List<MarketLogVo> list = iMarketLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "奖励发放记录", MarketLogVo.class, response);
    }

    /**
     * 获取奖励发放记录详细信息
     *
     * @param logId 主键
     */
    @SaCheckPermission("zlyyh:marketLog:query")
    @GetMapping("/{logId}")
    public R<MarketLogVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long logId) {
        return R.ok(iMarketLogService.queryById(logId));
    }
}
