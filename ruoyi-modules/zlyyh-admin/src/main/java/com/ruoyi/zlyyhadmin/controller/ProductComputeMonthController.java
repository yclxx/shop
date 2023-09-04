package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ProductComputeMonthBo;
import com.ruoyi.zlyyh.domain.vo.ProductComputeMonthVo;
import com.ruoyi.zlyyhadmin.service.IProductComputeMonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单数据统计（月份）控制器
 * 前端访问路由地址为:/zlyyh/productComputeMonth
 *
 * @author yzg
 * @date 2023-07-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/productComputeMonth")
public class ProductComputeMonthController extends BaseController {

    private final IProductComputeMonthService iProductComputeMonthService;

    /**
     * 查询订单数据统计（月份）列表
     */
    @SaCheckPermission("zlyyh:productComputeMonth:list")
    @GetMapping("/list")
    public TableDataInfo<ProductComputeMonthVo> list(ProductComputeMonthBo bo, PageQuery pageQuery) {
        return iProductComputeMonthService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出订单数据统计（月份）列表
     */
    @SaCheckPermission("zlyyh:productComputeMonth:export")
    @Log(title = "订单数据统计（月份）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProductComputeMonthBo bo, HttpServletResponse response) {
        List<ProductComputeMonthVo> list = iProductComputeMonthService.queryList(bo);
        ExcelUtil.exportExcel(list, "订单数据统计（月份）", ProductComputeMonthVo.class, response);
    }

    /**
     * 获取订单数据统计（月份）详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:productComputeMonth:query")
    @GetMapping("/{id}")
    public R<ProductComputeMonthVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iProductComputeMonthService.queryById(id));
    }
}
