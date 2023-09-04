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
import com.ruoyi.zlyyh.domain.bo.CityMerchantBo;
import com.ruoyi.zlyyh.domain.vo.CityMerchantVo;
import com.ruoyi.zlyyhadmin.service.ICityMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 城市商户控制器
 * 前端访问路由地址为:/zlyyh/cityMerchant
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/cityMerchant")
public class CityMerchantController extends BaseController {

    private final ICityMerchantService iCityMerchantService;

    /**
     * 查询城市商户列表
     */
    @SaCheckPermission("zlyyh:cityMerchant:list")
    @GetMapping("/list")
    public TableDataInfo<CityMerchantVo> list(CityMerchantBo bo, PageQuery pageQuery) {
        return iCityMerchantService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出城市商户列表
     */
    @SaCheckPermission("zlyyh:cityMerchant:export")
    @Log(title = "城市商户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CityMerchantBo bo, HttpServletResponse response) {
        List<CityMerchantVo> list = iCityMerchantService.queryList(bo);
        ExcelUtil.exportExcel(list, "城市商户", CityMerchantVo.class, response);
    }

    /**
     * 获取城市商户详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:cityMerchant:query")
    @GetMapping("/{id}")
    public R<CityMerchantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iCityMerchantService.queryById(id));
    }

    /**
     * 新增城市商户
     */
    @SaCheckPermission("zlyyh:cityMerchant:add")
    @Log(title = "城市商户", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CityMerchantBo bo) {
        return toAjax(iCityMerchantService.insertByBo(bo));
    }

    /**
     * 修改城市商户
     */
    @SaCheckPermission("zlyyh:cityMerchant:edit")
    @Log(title = "城市商户", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CityMerchantBo bo) {
        return toAjax(iCityMerchantService.updateByBo(bo));
    }

    /**
     * 删除城市商户
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:cityMerchant:remove")
    @Log(title = "城市商户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iCityMerchantService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
