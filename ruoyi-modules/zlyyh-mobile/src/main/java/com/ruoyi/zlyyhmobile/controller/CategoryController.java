package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.CategoryBo;
import com.ruoyi.zlyyh.domain.vo.CategoryVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.ICategoryService;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类别管理控制器
 * 前端访问路由地址为:/zlyyh-mobile/category/ignore
 *
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/category/ignore")
public class CategoryController {

    private final ICategoryService categoryService;
    private final IPlatformService platformService;

    /**
     * 获取子分类列表
     *
     * @return 类别列表
     */
    @GetMapping("/getCategoryList")
    public R<List<CategoryVo>> getCategoryList(CategoryBo bo) {
        if (ObjectUtils.isEmpty(bo.getParentId())) {
            // 忘填类别id返回为空
            return R.ok(new ArrayList<>());
        }
        bo.setStatus("0");
        bo.setShowCity(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        bo.setWeekDate("" + DateUtil.dayOfWeek(new Date()));
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        return R.ok(categoryService.queryList(bo));
    }

    /**
     * 获取分类信息
     *
     * @return 类别列表
     */
    @GetMapping("/getCategory/{categoryId}")
    public R<CategoryVo> getCategory(@PathVariable("categoryId") Long categoryId) {
        return R.ok(categoryService.queryById(categoryId));
    }

    /**
     * 获取首页分类列表
     *
     * @return 类别列表
     */
    @GetMapping("/getIndexCategoryList")
    public R<List<CategoryVo>> getIndexCategoryList() {
        CategoryBo bo = new CategoryBo();
        bo.setStatus("0");
        bo.setShowCity(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        bo.setWeekDate("" + DateUtil.dayOfWeek(new Date()));
        bo.setShowIndex("1");
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        PlatformVo platformVo = platformService.queryById(ZlyyhUtils.getPlatformId(), ZlyyhUtils.getPlatformChannel());
        if (ObjectUtils.isNotEmpty(platformVo.getIndexShowType())){
            bo.setCategoryListType(platformVo.getIndexShowType());
        }
        return R.ok(categoryService.queryList(bo));
    }
}
