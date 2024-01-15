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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.CityVo;
import com.ruoyi.zlyyh.domain.bo.CityBo;
import com.ruoyi.zlyyhadmin.service.ICityService;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 行政区控制器
 * 前端访问路由地址为:/zlyyh/city
 *
 * @author yzg
 * @date 2024-01-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/city")
public class CityController extends BaseController {

    private final ICityService iCityService;

    /**
     * 查询行政区列表
     */
    @SaCheckPermission("zlyyh:city:list")
    @GetMapping("/list")
    public R<List<CityVo>> list(CityBo bo) {
        List<CityVo> list = iCityService.queryList(bo);
        return R.ok(list);
    }

    /**
     * 导出行政区列表
     */
    @SaCheckPermission("zlyyh:city:export")
    @Log(title = "行政区", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CityBo bo, HttpServletResponse response) {
        List<CityVo> list = iCityService.queryList(bo);
        ExcelUtil.exportExcel(list, "行政区", CityVo.class, response);
    }

    /**
     * 获取行政区详细信息
     *
     * @param adcode 主键
     */
    @SaCheckPermission("zlyyh:city:query")
    @GetMapping("/{adcode}")
    public R<CityVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long adcode) {
        return R.ok(iCityService.queryById(adcode));
    }

    /**
     * 新增行政区
     */
    @SaCheckPermission("zlyyh:city:add")
    @Log(title = "行政区", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CityBo bo) {
        return toAjax(iCityService.insertByBo(bo));
    }

    /**
     * 修改行政区
     */
    @SaCheckPermission("zlyyh:city:edit")
    @Log(title = "行政区", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CityBo bo) {
        return toAjax(iCityService.updateByBo(bo));
    }

    /**
     * 删除行政区
     *
     * @param adcodes 主键串
     */
    @SaCheckPermission("zlyyh:city:remove")
    @Log(title = "行政区", businessType = BusinessType.DELETE)
    @DeleteMapping("/{adcodes}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] adcodes) {
        return toAjax(iCityService.deleteWithValidByIds(Arrays.asList(adcodes), true));
    }

    /**
     * 获取城市信息
     * @param adcode
     * @return
     */
    @GetMapping("/getDistrict/{adcode}")
    public R<Void> getDistrict(@PathVariable String adcode) {
        iCityService.getDistrict(adcode);
        return R.ok();
    }

}
