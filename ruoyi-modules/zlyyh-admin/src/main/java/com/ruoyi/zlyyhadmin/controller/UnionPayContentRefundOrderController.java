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
import com.ruoyi.zlyyh.domain.bo.UnionPayContentRefundOrderBo;
import com.ruoyi.zlyyh.domain.vo.UnionPayContentRefundOrderVo;
import com.ruoyi.zlyyhadmin.service.IUnionPayContentRefundOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 内容分销内容方退券订单控制器
 * 前端访问路由地址为:/zlyyh/unionPayContentRefundOrder
 *
 * @author yzg
 * @date 2023-09-23
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionPayContentRefundOrder")
public class UnionPayContentRefundOrderController extends BaseController {

    private final IUnionPayContentRefundOrderService iUnionPayContentRefundOrderService;

    /**
     * 查询内容分销内容方退券订单列表
     */
    @SaCheckPermission("zlyyh:unionPayContentRefundOrder:list")
    @GetMapping("/list")
    public TableDataInfo<UnionPayContentRefundOrderVo> list(UnionPayContentRefundOrderBo bo, PageQuery pageQuery) {
        return iUnionPayContentRefundOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出内容分销内容方退券订单列表
     */
    @SaCheckPermission("zlyyh:unionPayContentRefundOrder:export")
    @Log(title = "内容分销内容方退券订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionPayContentRefundOrderBo bo, HttpServletResponse response) {
        List<UnionPayContentRefundOrderVo> list = iUnionPayContentRefundOrderService.queryList(bo);
        ExcelUtil.exportExcel(list, "内容分销内容方退券订单", UnionPayContentRefundOrderVo.class, response);
    }

    /**
     * 获取内容分销内容方退券订单详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:unionPayContentRefundOrder:query")
    @GetMapping("/{id}")
    public R<UnionPayContentRefundOrderVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iUnionPayContentRefundOrderService.queryById(id));
    }

    /**
     * 新增内容分销内容方退券订单
     */
    @SaCheckPermission("zlyyh:unionPayContentRefundOrder:add")
    @Log(title = "内容分销内容方退券订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionPayContentRefundOrderBo bo) {
        return toAjax(iUnionPayContentRefundOrderService.insertByBo(bo));
    }

    /**
     * 修改内容分销内容方退券订单
     */
    @SaCheckPermission("zlyyh:unionPayContentRefundOrder:edit")
    @Log(title = "内容分销内容方退券订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionPayContentRefundOrderBo bo) {
        return toAjax(iUnionPayContentRefundOrderService.updateByBo(bo));
    }

    /**
     * 删除内容分销内容方退券订单
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:unionPayContentRefundOrder:remove")
    @Log(title = "内容分销内容方退券订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iUnionPayContentRefundOrderService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
