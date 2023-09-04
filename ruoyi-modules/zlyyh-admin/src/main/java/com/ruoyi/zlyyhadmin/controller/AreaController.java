package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ColumnUtil;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.SelectListEntity;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.zlyyh.domain.Area;
import com.ruoyi.zlyyh.domain.bo.AreaBo;
import com.ruoyi.zlyyh.domain.vo.AreaVo;
import com.ruoyi.zlyyhadmin.service.IAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行政区控制器
 * 前端访问路由地址为:/zlyyh-admin/area
 *
 * @author ruoyi
 * @date 2023-03-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/area")
public class AreaController extends BaseController {

    private final IAreaService iAreaService;

    /**
     * 查询行政区列表
     */
    @SaCheckPermission("zlyyh:area:list")
    @GetMapping("/list")
    public R<List<AreaVo>> list(AreaBo bo) {
        List<AreaVo> list = iAreaService.queryList(bo);
        return R.ok(list);
    }

    /**
     * 查询城市下拉列表
     */
    @GetMapping("/selectCityList")
    public R<List<SelectListEntity>> selectCityList(AreaBo bo){
        List<AreaVo> areaVos = iAreaService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(areaVos, ColumnUtil.getFieldName(AreaVo::getId),ColumnUtil.getFieldName(AreaVo::getAreaName),ColumnUtil.getFieldName(AreaVo::getAdcode)));
    }

    /**
     * 查询行政区列表
     */
    @GetMapping("/listSelect")
    public R<List<SelectListEntity>> listSelect(AreaBo bo) {
        List<AreaVo> list = iAreaService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(list, ColumnUtil.getFieldName(AreaVo::getId),ColumnUtil.getFieldName(AreaVo::getAdcode),ColumnUtil.getFieldName(AreaVo::getAreaName)));
    }

    /**
     * 导出行政区列表
     */
    @SaCheckPermission("zlyyh:area:export")
    @Log(title = "行政区", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AreaBo bo, HttpServletResponse response) {
        List<AreaVo> list = iAreaService.queryList(bo);
        ExcelUtil.exportExcel(list, "行政区", AreaVo.class, response);
    }

    /**
     * 获取行政区详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:area:query")
    @GetMapping("/{id}")
    public R<AreaVo> getInfo(@NotNull(message = "主键不能为空")@PathVariable Long id) {
        return R.ok(iAreaService.queryById(id));
    }

    /**
     * 新增行政区
     */
    @SaCheckPermission("zlyyh:area:add")
    @Log(title = "行政区", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AreaBo bo) {
        return toAjax(iAreaService.insertByBo(bo));
    }

    /**
     * 修改行政区
     */
    @SaCheckPermission("zlyyh:area:edit")
    @Log(title = "行政区", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AreaBo bo) {
        return toAjax(iAreaService.updateByBo(bo));
    }

    /**
     * 删除行政区
     *
     * @param adcodes 主键串
     */
    @SaCheckPermission("zlyyh:area:remove")
    @Log(title = "行政区", businessType = BusinessType.DELETE)
    @DeleteMapping("/{adcodes}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] adcodes) {
        return toAjax(iAreaService.deleteWithValidByIds(Arrays.asList(adcodes), true));
    }

    /**
     * 获取行政区下拉树列表
     */
    @GetMapping("/treeselect")
    public R<List<Tree<Long>>> treeselect(Area area){
        List<Area> areaList = iAreaService.selectCityList(area);
        return R.ok(iAreaService.buildCityTreeSelect(areaList));
    }

    /**
     * 加载对应城市列表树
     */
    @GetMapping("/platformCityTreeselect/{platform}")
    public R<Map<String,Object>> platformCityTreeselect(@PathVariable("platform") Long platform){
        Area area = new Area();
        List<Area> areas = iAreaService.selectCityList(area);
        Map<String,Object> ajax = new HashMap<>();
        ajax.put("checkedKeys",iAreaService.selectPlatformCityByPlatform(platform));
        ajax.put("citys",iAreaService.buildCityTreeSelect(areas));
        return R.ok(ajax);
    }

    /**
     * 加载banner对应城市列表树
     */
    @GetMapping("/bannerShowCityTreeSelect/{bannerId}")
    public R<Map<String,Object>> bannerShowCityTreeSelect(@PathVariable("bannerId") Long bannerId){
        Area area = new Area();
        List<Area> areas = iAreaService.selectCityList(area);
        Map<String,Object> ajax = new HashMap<>();
        ajax.put("checkedKeys",iAreaService.selectPlatformCityByBannerId(bannerId));
        ajax.put("citys",iAreaService.buildCityTreeSelect(areas));
        return R.ok(ajax);
    }

    /**
     * 加载banner对应城市列表树
     */
    @GetMapping("/hotNewsShowCityTreeSelect/{newsId}")
    public R<Map<String,Object>> hotNewsShowCityTreeSelect(@PathVariable("newsId") Long newsId){
        Area area = new Area();
        List<Area> areas = iAreaService.selectCityList(area);
        Map<String,Object> ajax = new HashMap<>();
        ajax.put("checkedKeys",iAreaService.selectPlatformCityByNewsId(newsId));
        ajax.put("citys",iAreaService.buildCityTreeSelect(areas));
        return R.ok(ajax);
    }

    @GetMapping("/productShowCityTreeSelect/{productId}")
    public R<Map<String,Object>> productShowCityTreeSelect(@PathVariable("productId") Long productId){
        Area area = new Area();
        List<Area> areas = iAreaService.selectCityList(area);
        Map<String,Object> ajax = new HashMap<>();
        ajax.put("checkedKeys",iAreaService.selectPlatformCityByProductId(productId));
        ajax.put("citys",iAreaService.buildCityTreeSelect(areas));
        return R.ok(ajax);
    }

    @GetMapping("/searchShowCityTreeSelect/{searchId}")
    public R<Map<String,Object>> searchShowCityTreeSelect(@PathVariable("searchId") Long searchId){
        Area area = new Area();
        List<Area> areas = iAreaService.selectCityList(area);
        Map<String,Object> ajax = new HashMap<>();
        ajax.put("checkedKeys",iAreaService.selectPlatformCityBySearchId(searchId));
        ajax.put("citys",iAreaService.buildCityTreeSelect(areas));
        return R.ok(ajax);
    }

}
