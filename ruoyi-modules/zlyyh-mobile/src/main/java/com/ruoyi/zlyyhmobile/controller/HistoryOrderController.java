package com.ruoyi.zlyyhmobile.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.http.HttpStatus;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.HistoryOrder;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyhmobile.service.IHistoryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.HistoryOrderVo;
import com.ruoyi.zlyyh.domain.bo.HistoryOrderBo;

import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 历史订单控制器
 * 前端访问路由地址为:/zlyyh/historyOrder
 *
 * @author yzg
 * @date 2023-08-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/historyOrder")
public class HistoryOrderController extends BaseController {

    private final IHistoryOrderService iHistoryOrderService;

    /**
     * 查询历史订单列表
     */
    @GetMapping("/getOrderList")
    public TableDataInfo<HistoryOrderVo> getOrderList(HistoryOrderBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        return iHistoryOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取历史订单详情
     *
     * @return 订单信息
     */
    @GetMapping("/getOrderInfo/{number}")
    public R<HistoryOrderVo> getOrderInfo(@NotNull(message = "请求错误")
                                   @PathVariable("number") Long number) {
        HistoryOrderVo orderVo = iHistoryOrderService.queryById(number);
        if (null != orderVo && !orderVo.getUserId().equals(LoginHelper.getUserId())) {
            throw new ServiceException("登录超时,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        return R.ok(orderVo);
    }




}
