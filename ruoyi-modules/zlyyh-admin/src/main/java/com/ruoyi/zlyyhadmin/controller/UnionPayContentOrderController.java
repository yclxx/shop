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
import com.ruoyi.zlyyh.domain.bo.UnionPayContentOrderBo;
import com.ruoyi.zlyyh.domain.vo.UnionPayContentOrderVo;
import com.ruoyi.zlyyhadmin.service.IUnionPayContentOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 内容分销内容方订单控制器
 * 前端访问路由地址为:/zlyyh/unionPayContentOrder
 *
 * @author yzg
 * @date 2023-09-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionPayContentOrder")
public class UnionPayContentOrderController extends BaseController {

    private final IUnionPayContentOrderService iUnionPayContentOrderService;

    /**
     * 查询内容分销内容方订单列表
     */
    @SaCheckPermission("zlyyh:unionPayContentOrder:list")
    @GetMapping("/list")
    public TableDataInfo<UnionPayContentOrderVo> list(UnionPayContentOrderBo bo, PageQuery pageQuery) {
        return iUnionPayContentOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出内容分销内容方订单列表
     */
    @SaCheckPermission("zlyyh:unionPayContentOrder:export")
    @Log(title = "内容分销内容方订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionPayContentOrderBo bo, HttpServletResponse response) {
        List<UnionPayContentOrderVo> list = iUnionPayContentOrderService.queryList(bo);
        ExcelUtil.exportExcel(list, "内容分销内容方订单", UnionPayContentOrderVo.class, response);
    }

    /**
     * 获取内容分销内容方订单详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:unionPayContentOrder:query")
    @GetMapping("/{id}")
    public R<UnionPayContentOrderVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iUnionPayContentOrderService.queryById(id));
    }

    /**
     * 新增内容分销内容方订单
     */
    @SaCheckPermission("zlyyh:unionPayContentOrder:add")
    @Log(title = "内容分销内容方订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionPayContentOrderBo bo) {
        return toAjax(iUnionPayContentOrderService.insertByBo(bo));
    }

    /**
     * 修改内容分销内容方订单
     */
    @SaCheckPermission("zlyyh:unionPayContentOrder:edit")
    @Log(title = "内容分销内容方订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionPayContentOrderBo bo) {
        return toAjax(iUnionPayContentOrderService.updateByBo(bo));
    }

    /**
     * 删除内容分销内容方订单
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:unionPayContentOrder:remove")
    @Log(title = "内容分销内容方订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iUnionPayContentOrderService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
