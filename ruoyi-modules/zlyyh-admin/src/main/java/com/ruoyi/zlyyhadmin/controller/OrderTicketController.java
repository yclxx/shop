package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.LogisticsImportBo;
import com.ruoyi.zlyyh.domain.bo.OrderTicketBo;
import com.ruoyi.zlyyh.domain.vo.OrderIdcardVo;
import com.ruoyi.zlyyh.domain.vo.OrderTicketVo;
import com.ruoyi.zlyyhadmin.service.IOrderTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 演出票订单控制器
 * 前端访问路由地址为:/zlyyh/orderTicket
 *
 * @author yzg
 * @date 2023-09-23
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderTicket")
public class OrderTicketController extends BaseController {

    private final IOrderTicketService iOrderTicketService;

    /**
     * 查询演出票订单列表
     */
    @SaCheckPermission("zlyyh:orderTicket:list")
    @GetMapping("/list")
    public TableDataInfo<OrderTicketVo> list(OrderTicketBo bo, PageQuery pageQuery) {
        return iOrderTicketService.selectPageUserList(bo, pageQuery);
    }

    /**
     * 导出演出票订单列表
     */
    @SaCheckPermission("zlyyh:orderTicket:export")
    @Log(title = "演出票订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderTicketBo bo, HttpServletResponse response) {
        List<OrderTicketVo> list = iOrderTicketService.queryList(bo);
        ExcelUtil.exportExcel(list, "演出票订单", OrderTicketVo.class, response);
    }

    /**
     * 获取演出票订单详细信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:orderTicket:query")
    @GetMapping("/{number}")
    public R<OrderTicketVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iOrderTicketService.queryById(number));
    }

    /**
     * 观影人信息
     *
     * @param number 主键
     */
    @SaCheckPermission("zlyyh:orderTicket:query")
    @GetMapping("/getOrderIdCardList/{number}")
    public R<List<OrderIdcardVo>> getOrderIdCardList(@NotNull(message = "主键不能为空") @PathVariable Long number) {
        return R.ok(iOrderTicketService.getOrderIdCardList(number));
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "物流信息", LogisticsImportBo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @Log(title = "物流信息", businessType = BusinessType.IMPORT)
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file) throws Exception {
        iOrderTicketService.importData(file);
        return R.ok();
    }

    /**
     * 核销
     */
    @SaCheckPermission("zlyyh:orderTicket:writeOffCode")
    @GetMapping("/writeOffCode/{codeNo}")
    public R<Boolean> writeOffCode(@NotNull(message = "主键不能为空") @PathVariable String codeNo) {
        return R.ok(iOrderTicketService.writeOffCode(codeNo));
    }

    /**
     * 票券返还
     */
    @SaCheckPermission("zlyyh:orderTicket:voidCode")
    @GetMapping("/voidCode/{codeNo}")
    public R<Boolean> voidCode(@NotNull(message = "主键不能为空") @PathVariable String codeNo) {
        return R.ok(iOrderTicketService.voidCode(codeNo));
    }

    /**
     * 作废
     */
    @SaCheckPermission("zlyyh:orderTicket:returnCode")
    @GetMapping("/returnCode/{codeNo}")
    public R<Boolean> returnCode(@NotNull(message = "主键不能为空") @PathVariable String codeNo) {
        return R.ok(iOrderTicketService.returnCode(codeNo));
    }
}
