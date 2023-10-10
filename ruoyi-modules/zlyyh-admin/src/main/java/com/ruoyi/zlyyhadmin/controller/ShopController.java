package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ColumnUtil;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.SelectListEntity;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.bo.ShopImportBo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyhadmin.domain.bo.ShopImportDataBo;
import com.ruoyi.zlyyhadmin.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 门店控制器
 * 前端访问路由地址为:/zlyyh/shop
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop")
public class ShopController extends BaseController {

    private final IShopService iShopService;

    /**
     * 特殊查询条件
     */
    @GetMapping("/lists")
    public TableDataInfo<ShopVo> lists(ShopBo bo, PageQuery pageQuery) {
        return iShopService.queryPageLists(bo, pageQuery);
    }

    /**
     * 查询门店列表
     */
    @SaCheckPermission("zlyyh:shop:list")
    @GetMapping("/list")
    public TableDataInfo<ShopVo> list(ShopBo bo, PageQuery pageQuery) {
        return iShopService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询门店下拉列表
     */
    @GetMapping("/selectShopList")
    public R<List<SelectListEntity>> selectShopList(ShopBo bo) {
        List<ShopVo> shopVoList = iShopService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(shopVoList, ColumnUtil.getFieldName(ShopVo::getShopId), ColumnUtil.getFieldName(ShopVo::getShopName), null));
    }

    /**
     * 查询门店下拉列表
     */
    @GetMapping("/selectShopListById")
    public R<List<SelectListEntity>> selectShopListById(@RequestParam("ids") List<String> ids) {
        List<ShopVo> shopVoList = iShopService.queryList(ids);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(shopVoList, ColumnUtil.getFieldName(ShopVo::getShopId), ColumnUtil.getFieldName(ShopVo::getShopName), null));
    }

    /**
     * 导出门店列表
     */
    @SaCheckPermission("zlyyh:shop:export")
    @Log(title = "门店", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShopBo bo, HttpServletResponse response) {
        List<ShopVo> list = iShopService.queryList(bo);
        ExcelUtil.exportExcel(list, "门店", ShopVo.class, response);
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "门店数据", ShopImportBo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @Log(title = "门店管理", businessType = BusinessType.IMPORT)
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file, ShopImportDataBo shopImportDataBo) throws Exception {
        if (null == shopImportDataBo.getPlatformKey()) {
            throw new ServiceException("请选择平台");
        }
        iShopService.importShopData(file, shopImportDataBo);
        return R.ok();
    }

    /**
     * 获取门店详细信息
     *
     * @param shopId 主键
     */
    @SaCheckPermission("zlyyh:shop:query")
    @GetMapping("/{shopId}")
    public R<ShopVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long shopId) {
        return R.ok(iShopService.queryById(shopId));
    }

    /**
     * 新增门店
     */
    @SaCheckPermission("zlyyh:shop:add")
    @Log(title = "门店", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShopBo bo) {
        return toAjax(iShopService.insertByBo(bo));
    }

    /**
     * 修改门店
     */
    @SaCheckPermission("zlyyh:shop:edit")
    @Log(title = "门店", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShopBo bo) {
        return toAjax(iShopService.updateByBo(bo));
    }

    /**
     * 删除门店
     *
     * @param shopIds 主键串
     */
    @SaCheckPermission("zlyyh:shop:remove")
    @Log(title = "门店", businessType = BusinessType.DELETE)
    @DeleteMapping("/{shopIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] shopIds) {
        return toAjax(iShopService.deleteWithValidByIds(Arrays.asList(shopIds), true));
    }
}
