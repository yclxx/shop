package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.BrandBo;
import com.ruoyi.zlyyh.domain.vo.BrandVo;
import com.ruoyi.zlyyhmobile.service.IBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 品牌管理控制器
 * 前端访问路由地址为:/zlyyh/brand
 *
 * @author yzg
 * @date 2023-12-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {

    private final IBrandService iBrandService;

    /**
     * 查询品牌管理列表
     */
    @GetMapping("/list")
    public TableDataInfo<BrandVo> list(BrandBo bo, PageQuery pageQuery) {
        bo.setStatus("0");
        pageQuery.setIsAsc("asc");
        pageQuery.setOrderByColumn("sort");
        return iBrandService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取品牌管理详细信息
     *
     * @param brandId 主键
     */
    @GetMapping("/{brandId}")
    public R<BrandVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long brandId) {
        return R.ok(iBrandService.queryById(brandId));
    }

    /**
     * 新增品牌管理
     */
    @Log(title = "品牌管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/add")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BrandBo bo) {
        // todo 校验用户权限
        return toAjax(iBrandService.insertByBo(bo));
    }

    /**
     * 修改品牌管理
     */
    @Log(title = "品牌管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BrandBo bo) {
        // todo 校验用户权限
        return toAjax(iBrandService.updateByBo(bo));
    }
}
