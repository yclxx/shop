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
import com.ruoyi.zlyyh.domain.bo.EquityProductBo;
import com.ruoyi.zlyyh.domain.vo.EquityProductVo;
import com.ruoyi.zlyyhadmin.service.IEquityProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 权益包商品控制器
 * 前端访问路由地址为:/zlyyh/equityProduct
 *
 * @author yzg
 * @date 2023-06-06
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/equityProduct")
public class EquityProductController extends BaseController {

    private final IEquityProductService iEquityProductService;

    /**
     * 查询权益包商品列表
     */
    @SaCheckPermission("zlyyh:equityProduct:list")
    @GetMapping("/list")
    public TableDataInfo<EquityProductVo> list(EquityProductBo bo, PageQuery pageQuery) {
        return iEquityProductService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出权益包商品列表
     */
    @SaCheckPermission("zlyyh:equityProduct:export")
    @Log(title = "权益包商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(EquityProductBo bo, HttpServletResponse response) {
        List<EquityProductVo> list = iEquityProductService.queryList(bo);
        ExcelUtil.exportExcel(list, "权益包商品", EquityProductVo.class, response);
    }

    /**
     * 获取权益包商品详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:equityProduct:query")
    @GetMapping("/{id}")
    public R<EquityProductVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iEquityProductService.queryById(id));
    }

    /**
     * 新增权益包商品
     */
    @SaCheckPermission("zlyyh:equityProduct:add")
    @Log(title = "权益包商品", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody EquityProductBo bo) {
        return toAjax(iEquityProductService.insertByBo(bo));
    }

    /**
     * 修改权益包商品
     */
    @SaCheckPermission("zlyyh:equityProduct:edit")
    @Log(title = "权益包商品", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody EquityProductBo bo) {
        return toAjax(iEquityProductService.updateByBo(bo));
    }

    /**
     * 删除权益包商品
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:equityProduct:remove")
    @Log(title = "权益包商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iEquityProductService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
