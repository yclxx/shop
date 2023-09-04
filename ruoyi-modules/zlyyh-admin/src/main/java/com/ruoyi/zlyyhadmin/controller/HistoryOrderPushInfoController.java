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
import com.ruoyi.zlyyhadmin.service.IHistoryOrderPushInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderPushInfoVo;
import com.ruoyi.zlyyh.domain.bo.HistoryOrderPushInfoBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 历史订单取码记录控制器
 * 前端访问路由地址为:/zlyyh/historyOrderPushInfo
 *
 * @author yzg
 * @date 2023-08-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/historyOrderPushInfo")
public class HistoryOrderPushInfoController extends BaseController {

    private final IHistoryOrderPushInfoService iHistoryOrderPushInfoService;

    /**
     * 查询历史订单取码记录列表
     */
    @SaCheckPermission("zlyyh:historyOrderPushInfo:list")
    @GetMapping("/list")
    public TableDataInfo<HistoryOrderPushInfoVo> list(HistoryOrderPushInfoBo bo, PageQuery pageQuery) {
        return iHistoryOrderPushInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出历史订单取码记录列表
     */
    @SaCheckPermission("zlyyh:historyOrderPushInfo:export")
    @Log(title = "历史订单取码记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HistoryOrderPushInfoBo bo, HttpServletResponse response) {
        List<HistoryOrderPushInfoVo> list = iHistoryOrderPushInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "历史订单取码记录", HistoryOrderPushInfoVo.class, response);
    }

    /**
     * 获取历史订单取码记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:historyOrderPushInfo:query")
    @GetMapping("/{id}")
    public R<HistoryOrderPushInfoVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iHistoryOrderPushInfoService.queryById(id));
    }

    /**
     * 新增历史订单取码记录
     */
    @SaCheckPermission("zlyyh:historyOrderPushInfo:add")
    @Log(title = "历史订单取码记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody HistoryOrderPushInfoBo bo) {
        return toAjax(iHistoryOrderPushInfoService.insertByBo(bo));
    }

    /**
     * 修改历史订单取码记录
     */
    @SaCheckPermission("zlyyh:historyOrderPushInfo:edit")
    @Log(title = "历史订单取码记录", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody HistoryOrderPushInfoBo bo) {
        return toAjax(iHistoryOrderPushInfoService.updateByBo(bo));
    }

    /**
     * 删除历史订单取码记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:historyOrderPushInfo:remove")
    @Log(title = "历史订单取码记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iHistoryOrderPushInfoService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
