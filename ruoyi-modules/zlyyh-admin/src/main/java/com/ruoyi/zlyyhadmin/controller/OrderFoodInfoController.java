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
import com.ruoyi.zlyyhadmin.service.IOrderFoodInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.OrderFoodInfoVo;
import com.ruoyi.zlyyh.domain.bo.OrderFoodInfoBo;

import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 美食套餐详细订单控制器
 * 前端访问路由地址为:/zlyyh/orderFoodInfo
 *
 * @author yzg
 * @date 2023-05-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderFoodInfo")
public class OrderFoodInfoController extends BaseController {

    private final IOrderFoodInfoService iOrderFoodInfoService;

    /**
     * 查询美食套餐详细订单列表
     */
    @SaCheckPermission("zlyyh:orderFoodInfo:list")
    @GetMapping("/list")
    public TableDataInfo<OrderFoodInfoVo> list(OrderFoodInfoBo bo, PageQuery pageQuery) {
        return iOrderFoodInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出美食套餐详细订单列表
     */
    @SaCheckPermission("zlyyh:orderFoodInfo:export")
    @Log(title = "美食套餐详细订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderFoodInfoBo bo, HttpServletResponse response) {
        List<OrderFoodInfoVo> list = iOrderFoodInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "美食套餐详细订单", OrderFoodInfoVo.class, response);
    }

    /**
     * 获取美食套餐详细订单详细信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:orderFoodInfo:query")
    @GetMapping("/{number}")
    public R<OrderFoodInfoVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iOrderFoodInfoService.queryById(number));
    }

    /**
     * 新增美食套餐详细订单
     */
    @SaCheckPermission("zlyyh:orderFoodInfo:add")
    @Log(title = "美食套餐详细订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderFoodInfoBo bo) {
        return toAjax(iOrderFoodInfoService.insertByBo(bo));
    }

    /**
     * 修改美食套餐详细订单
     */
    @SaCheckPermission("zlyyh:orderFoodInfo:edit")
    @Log(title = "美食套餐详细订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderFoodInfoBo bo) {
        return toAjax(iOrderFoodInfoService.updateByBo(bo));
    }

    /**
     * 删除美食套餐详细订单
     *
     * @param numbers 主键串
     */
    @SaCheckPermission("zlyyh:orderFoodInfo:remove")
    @Log(title = "美食套餐详细订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{numbers}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] numbers) {
        return toAjax(iOrderFoodInfoService.deleteWithValidByIds(Arrays.asList(numbers), true));
    }
}
