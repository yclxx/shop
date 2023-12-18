package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.zlyyh.domain.bo.UnionpayProductBo;
import com.ruoyi.zlyyh.domain.vo.UnionpayProductVo;
import com.ruoyi.zlyyhadmin.service.IUnionpayProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 银联活动控制器
 * 前端访问路由地址为:/zlyyh/unionpayProduct
 *
 * @author yzg
 * @date 2023-12-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionpayProduct")
public class UnionpayProductController extends BaseController {

    private final IUnionpayProductService iUnionpayProductService;

    /**
     * 查询银联活动列表
     */
    @SaCheckPermission("zlyyh:unionpayProduct:list")
    @GetMapping("/list")
    public TableDataInfo<UnionpayProductVo> list(UnionpayProductBo bo, PageQuery pageQuery) {
        TableDataInfo<UnionpayProductVo> unionpayProductVoTableDataInfo = iUnionpayProductService.queryPageList(bo, pageQuery);
        List<UnionpayProductVo> newRows = new ArrayList<>(unionpayProductVoTableDataInfo.getRows().size());
        for (UnionpayProductVo row : unionpayProductVoTableDataInfo.getRows()) {
            UnionpayProductBo productBo = new UnionpayProductBo();
            productBo.setActivityNo(row.getActivityNo());
            String value = CacheUtils.get(CacheNames.UNIONPAY_PRODUCT, row.getActivityNo());
            if (StringUtils.isBlank(value)) {
                CacheUtils.put(CacheNames.UNIONPAY_PRODUCT, row.getActivityNo(), DateUtil.now());
                try {
                    iUnionpayProductService.updateByBo(productBo);
                } catch (Exception ignored) {
                }
                newRows.add(iUnionpayProductService.queryById(row.getActivityNo()));
            } else {
                newRows.add(row);
            }
        }
        unionpayProductVoTableDataInfo.setRows(newRows);
        return unionpayProductVoTableDataInfo;
    }

    /**
     * 导出银联活动列表
     */
    @SaCheckPermission("zlyyh:unionpayProduct:export")
    @Log(title = "银联活动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionpayProductBo bo, HttpServletResponse response) {
        List<UnionpayProductVo> list = iUnionpayProductService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联活动", UnionpayProductVo.class, response);
    }

    /**
     * 获取银联活动详细信息
     *
     * @param activityNo 主键
     */
    @SaCheckPermission("zlyyh:unionpayProduct:query")
    @GetMapping("/{activityNo}")
    public R<UnionpayProductVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable String activityNo) {
        return R.ok(iUnionpayProductService.queryById(activityNo));
    }

    /**
     * 新增银联活动
     */
    @SaCheckPermission("zlyyh:unionpayProduct:add")
    @Log(title = "银联活动", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionpayProductBo bo) {
        return toAjax(iUnionpayProductService.insertByBo(bo));
    }

    /**
     * 修改银联活动
     */
    @SaCheckPermission("zlyyh:unionpayProduct:edit")
    @Log(title = "银联活动", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionpayProductBo bo) {
        return toAjax(iUnionpayProductService.updateByBo(bo));
    }

    /**
     * 删除银联活动
     *
     * @param activityNos 主键串
     */
    @SaCheckPermission("zlyyh:unionpayProduct:remove")
    @Log(title = "银联活动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityNos}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] activityNos) {
        return toAjax(iUnionpayProductService.deleteWithValidByIds(Arrays.asList(activityNos), true));
    }
}
