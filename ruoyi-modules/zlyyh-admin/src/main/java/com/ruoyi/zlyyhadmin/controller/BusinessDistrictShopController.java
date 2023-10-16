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
import com.ruoyi.zlyyh.domain.bo.BusinessDistrictShopBo;
import com.ruoyi.zlyyh.domain.vo.BusinessDistrictShopVo;
import com.ruoyi.zlyyhadmin.service.IBusinessDistrictShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 商圈门店关联控制器
 * 前端访问路由地址为:/zlyyh/businessDistrictShop
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/businessDistrictShop")
public class BusinessDistrictShopController extends BaseController {

    private final IBusinessDistrictShopService iBusinessDistrictShopService;

    /**
     * 查询商圈门店关联列表
     */
    @SaCheckPermission("zlyyh:businessDistrictShop:list")
    @GetMapping("/list")
    public TableDataInfo<BusinessDistrictShopVo> list(BusinessDistrictShopBo bo, PageQuery pageQuery) {
        return iBusinessDistrictShopService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商圈门店关联列表
     */
    @SaCheckPermission("zlyyh:businessDistrictShop:export")
    @Log(title = "商圈门店关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BusinessDistrictShopBo bo, HttpServletResponse response) {
        List<BusinessDistrictShopVo> list = iBusinessDistrictShopService.queryList(bo);
        ExcelUtil.exportExcel(list, "商圈门店关联", BusinessDistrictShopVo.class, response);
    }

    /**
     * 获取商圈门店关联详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:businessDistrictShop:query")
    @GetMapping("/{id}")
    public R<BusinessDistrictShopVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iBusinessDistrictShopService.queryById(id));
    }

    /**
     * 新增商圈门店关联
     */
    @SaCheckPermission("zlyyh:businessDistrictShop:add")
    @Log(title = "商圈门店关联", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BusinessDistrictShopBo bo) {
        return toAjax(iBusinessDistrictShopService.insertByBo(bo));
    }

    /**
     * 修改商圈门店关联
     */
    @SaCheckPermission("zlyyh:businessDistrictShop:edit")
    @Log(title = "商圈门店关联", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BusinessDistrictShopBo bo) {
        return toAjax(iBusinessDistrictShopService.updateByBo(bo));
    }

    /**
     * 删除商圈门店关联
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:businessDistrictShop:remove")
    @Log(title = "商圈门店关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iBusinessDistrictShopService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 为商品批量添加门店
     */
    @PostMapping("addShopByProduct")
    public R<Void> addShopByProduct(@RequestBody BusinessDistrictShopBo bo) {
        return toAjax(iBusinessDistrictShopService.addShopByProduct(bo));
    }

    /**
     * 为商品批量删除门店
     */
    @PostMapping("delByShopProduct")
    public R<Void> delByShopProduct(@RequestBody BusinessDistrictShopBo bo) {
        return toAjax(iBusinessDistrictShopService.delByShopProduct(bo));
    }

}
