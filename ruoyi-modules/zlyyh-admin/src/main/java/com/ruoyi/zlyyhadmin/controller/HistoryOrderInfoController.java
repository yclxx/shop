package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.zlyyhadmin.service.IHistoryOrderInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderInfoVo;
import com.ruoyi.zlyyh.domain.bo.HistoryOrderInfoBo;

import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 历史订单扩展信息控制器
 * 前端访问路由地址为:/zlyyh/historyOrderInfo
 *
 * @author yzg
 * @date 2023-08-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/historyOrderInfo")
public class HistoryOrderInfoController extends BaseController {

    private final IHistoryOrderInfoService iHistoryOrderInfoService;

    /**
     * 查询历史订单扩展信息列表
     */
    @SaCheckPermission("zlyyh:historyOrderInfo:list")
    @GetMapping("/list")
    public TableDataInfo<HistoryOrderInfoVo> list(HistoryOrderInfoBo bo, PageQuery pageQuery) {
        return iHistoryOrderInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出历史订单扩展信息列表
     */
    @SaCheckPermission("zlyyh:historyOrderInfo:export")
    @Log(title = "历史订单扩展信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HistoryOrderInfoBo bo, HttpServletResponse response) {
        List<HistoryOrderInfoVo> list = iHistoryOrderInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "历史订单扩展信息", HistoryOrderInfoVo.class, response);
    }

    /**
     * 获取历史订单扩展信息详细信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:historyOrderInfo:query")
    @GetMapping("/{number}")
    public R<HistoryOrderInfoVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iHistoryOrderInfoService.queryById(number));
    }

    /**
     * 新增历史订单扩展信息
     */
    @SaCheckPermission("zlyyh:historyOrderInfo:add")
    @Log(title = "历史订单扩展信息", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HistoryOrderInfoBo bo) {
        return toAjax(iHistoryOrderInfoService.insertByBo(bo));
    }

    /**
     * 修改历史订单扩展信息
     */
    @SaCheckPermission("zlyyh:historyOrderInfo:edit")
    @Log(title = "历史订单扩展信息", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HistoryOrderInfoBo bo) {
        return toAjax(iHistoryOrderInfoService.updateByBo(bo));
    }

    /**
     * 删除历史订单扩展信息
     *
     * @param numbers 主键串
     */
    @SaCheckPermission("zlyyh:historyOrderInfo:remove")
    @Log(title = "历史订单扩展信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{numbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] numbers) {
        return toAjax(iHistoryOrderInfoService.deleteWithValidByIds(Arrays.asList(numbers), true));
    }
}
