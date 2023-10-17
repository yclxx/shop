package com.ruoyi.zlyyhmobile.controller;

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
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.CartVo;
import com.ruoyi.zlyyh.domain.bo.CartBo;

import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 购物车控制器
 * 前端访问路由地址为:/zlyyh/cart
 *
 * @author yzg
 * @date 2023-10-16
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController extends BaseController {

    private final ICartService iCartService;

    /**
     * 查询购物车列表
     */
    @GetMapping("/list")
    public TableDataInfo<CartVo> list(CartBo bo, PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        bo.setUserId(userId);
        return iCartService.queryPageList(bo, pageQuery);
    }



    /**
     * 新增购物车
     */
    @PostMapping("/add")
    public R<Void> add(@RequestBody CartBo bo) {
        Long userId = LoginHelper.getUserId();
        bo.setUserId(userId);
        return toAjax(iCartService.insertByBo(bo));
    }

    /**
     * 修改购物车
     */
    @PostMapping("/edit")
    public R<Void> edit(@RequestBody CartBo bo) {
        Long userId = LoginHelper.getUserId();
        bo.setUserId(userId);
        return toAjax(iCartService.updateByBo(bo));
    }

    /**
     * 删除购物车
     *
     * @param ids 主键串
     */
    @PostMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iCartService.deleteWithValidByIds(Arrays.asList(ids),LoginHelper.getUserId(),true));
    }
}
