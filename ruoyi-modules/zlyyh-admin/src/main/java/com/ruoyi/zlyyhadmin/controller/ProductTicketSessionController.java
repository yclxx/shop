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
import com.ruoyi.zlyyh.domain.bo.ProductTicketSessionBo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketSessionVo;
import com.ruoyi.zlyyhadmin.service.IProductTicketSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 演出场次控制器
 * 前端访问路由地址为:/zlyyh/productTicketSession
 *
 * @author yzg
 * @date 2023-09-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/productTicketSession")
public class ProductTicketSessionController extends BaseController {

    private final IProductTicketSessionService iProductTicketSessionService;

    /**
     * 查询演出场次列表
     */
    @SaCheckPermission("zlyyh:productTicketSession:list")
    @GetMapping("/list")
    public TableDataInfo<ProductTicketSessionVo> list(ProductTicketSessionBo bo, PageQuery pageQuery) {
        return iProductTicketSessionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出演出场次列表
     */
    @SaCheckPermission("zlyyh:productTicketSession:export")
    @Log(title = "演出场次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProductTicketSessionBo bo, HttpServletResponse response) {
        List<ProductTicketSessionVo> list = iProductTicketSessionService.queryList(bo);
        ExcelUtil.exportExcel(list, "演出场次", ProductTicketSessionVo.class, response);
    }

    /**
     * 获取演出场次详细信息
     * @param priceId 主键
     */
    @SaCheckPermission("zlyyh:productTicketSession:query")
    @GetMapping("/{priceId}")
    public R<ProductTicketSessionVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long priceId) {
        return R.ok(iProductTicketSessionService.queryById(priceId));
    }

    /**
     * 新增演出场次
     */
    @SaCheckPermission("zlyyh:productTicketSession:add")
    @Log(title = "演出场次", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProductTicketSessionBo bo) {
        return toAjax(iProductTicketSessionService.insertByBo(bo));
    }

    /**
     * 修改演出场次
     */
    @SaCheckPermission("zlyyh:productTicketSession:edit")
    @Log(title = "演出场次", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProductTicketSessionBo bo) {
        return toAjax(iProductTicketSessionService.updateByBo(bo));
    }

    /**
     * 删除演出场次
     *
     * @param priceIds 主键串
     */
    @SaCheckPermission("zlyyh:productTicketSession:remove")
    @Log(title = "演出场次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{priceIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] priceIds) {
        return toAjax(iProductTicketSessionService.deleteWithValidByIds(Arrays.asList(priceIds), true));
    }
}
