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
import com.ruoyi.zlyyhmobile.service.ICollectiveOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.CollectiveOrderVo;
import com.ruoyi.zlyyh.domain.bo.CollectiveOrderBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 大订单控制器
 * 前端访问路由地址为:/zlyyh/collectiveOrder
 *
 * @author yzg
 * @date 2023-10-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/collectiveOrder")
public class CollectiveOrderController extends BaseController {

    private final ICollectiveOrderService iCollectiveOrderService;

    /**
     * 查询大订单列表
     */
    @GetMapping("/getCollectiveOrderList")
    public TableDataInfo<CollectiveOrderVo> getCollectiveOrderList(CollectiveOrderBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        return iCollectiveOrderService.queryPageList(bo, pageQuery);
    }



    /**
     * 获取大订单详细信息
     *
     * @param collectiveNumber 主键
     */
    @GetMapping("/getCollectiveOrderInfo/{collectiveNumber}")
    public R<CollectiveOrderVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long collectiveNumber) {
        return R.ok(iCollectiveOrderService.queryById(collectiveNumber));
    }




}
