package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.fastjson.JSONObject;
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
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhadmin.service.IProductService;
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
 * 商品控制器
 * 前端访问路由地址为:/zlyyh/product
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    private final IProductService iProductService;

    /**
     * 查询商品列表
     */
    @SaCheckPermission("zlyyh:product:list")
    @GetMapping("/list")
    public TableDataInfo<ProductVo> list(ProductBo bo, PageQuery pageQuery) {
        return iProductService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询商品下拉列表
     */
    @GetMapping("/selectListProduct")
    public R<List<SelectListEntity>> selectListProduct(ProductBo bo) {
        //查询下拉列表时加入时间和状态判断
        bo.setStatus("0");
        bo.setSearchStatus("0");
        List<ProductVo> productVos = iProductService.queryProductList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(productVos, ColumnUtil.getFieldName(ProductVo::getProductId), ColumnUtil.getFieldName(ProductVo::getProductName), null));
    }

    /**
     * 导出商品列表
     */
    @SaCheckPermission("zlyyh:product:export")
    @Log(title = "商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProductBo bo, HttpServletResponse response) {
        List<ProductVo> list = iProductService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品", ProductVo.class, response);
    }

    /**
     * 获取商品详细信息
     *
     * @param productId 主键
     */
    @SaCheckPermission("zlyyh:product:query")
    @GetMapping("/{productId}")
    public R<ProductVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long productId) {
        return R.ok(iProductService.queryById(productId));
    }

    /**
     * 新增商品
     */
    @SaCheckPermission("zlyyh:product:add")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProductBo bo) {
        return toAjax(iProductService.insertByBo(bo));
    }

    /**
     * 修改商品
     */
    @SaCheckPermission("zlyyh:product:edit")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProductBo bo) {
        return toAjax(iProductService.updateByBo(bo));
    }

    /**
     * 删除商品
     *
     * @param productIds 主键串
     */
    @SaCheckPermission("zlyyh:product:remove")
    @Log(title = "商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] productIds) {
        return toAjax(iProductService.deleteWithValidByIds(Arrays.asList(productIds), true));
    }

    /**
     * 设置今日已抢完
     *
     * @param dayCount 1 代表已抢完，其余任意值可抢
     */
    @SaCheckPermission("zlyyh:product:edit")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping("/setProductDayCount/{dayCount}")
    public R<Void> setProductDayCount(@NotEmpty(message = "主键不能为空") @PathVariable String dayCount, @RequestBody ProductBo bo) {
        RedisUtils.setCacheObject(ZlyyhUtils.getProductDayCount() + bo.getProductId(), dayCount);
        return R.ok();
    }


    /**
     * 联联产品更新
     */
    @PostMapping("/ignore/lianOrderCodeCall")
    public Map<String, Object> lianOrderCodeCall(@RequestBody JSONObject param) {
        iProductService.lianProductCall(param);
        return getMap();
    }

    /**
     * 联联产品状态通知(上架/下架/售空)
     *
     * @param param
     * @return
     */
    @PostMapping("/ignore/lianProductStatusCall")
    public Map<String, Object> lianProductStatusCall(@RequestBody JSONObject param) {
        iProductService.lianProductStatusCall(param);
        return getMap();
    }

    private Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "200");
        map.put("data", null);
        map.put("message", "请求响应成功!");
        map.put("success", true);
        return map;
    }
}
