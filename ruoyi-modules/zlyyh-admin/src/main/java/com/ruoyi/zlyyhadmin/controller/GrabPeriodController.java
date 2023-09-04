package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
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
import com.ruoyi.zlyyh.domain.bo.GrabPeriodBo;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodVo;
import com.ruoyi.zlyyhadmin.service.IGrabPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 秒杀配置控制器
 * 前端访问路由地址为:/zlyyh/grabPeriod
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/grabPeriod")
public class GrabPeriodController extends BaseController {

    private final IGrabPeriodService iGrabPeriodService;

    /**
     * 查询秒杀配置列表
     */
    @SaCheckPermission("zlyyh:grabPeriod:list")
    @GetMapping("/list")
    public TableDataInfo<GrabPeriodVo> list(GrabPeriodBo bo, PageQuery pageQuery) {
        return iGrabPeriodService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询秒杀配置下拉列表
     */
    @GetMapping("/selectList")
    public R<List<SelectListEntity>> selectList(GrabPeriodBo bo){
        List<GrabPeriodVo> grabPeriodVos = iGrabPeriodService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(grabPeriodVos, ColumnUtil.getFieldName(GrabPeriodVo::getId),ColumnUtil.getFieldName(GrabPeriodVo::getGrabPeriodName),null));
    }

    /**
     * 导出秒杀配置列表
     */
    @SaCheckPermission("zlyyh:grabPeriod:export")
    @Log(title = "秒杀配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GrabPeriodBo bo, HttpServletResponse response) {
        List<GrabPeriodVo> list = iGrabPeriodService.queryList(bo);
        ExcelUtil.exportExcel(list, "秒杀配置", GrabPeriodVo.class, response);
    }

    /**
     * 获取秒杀配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:grabPeriod:query")
    @GetMapping("/{id}")
    public R<GrabPeriodVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iGrabPeriodService.queryById(id));
    }

    /**
     * 新增秒杀配置
     */
    @SaCheckPermission("zlyyh:grabPeriod:add")
    @Log(title = "秒杀配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody GrabPeriodBo bo) {
        return toAjax(iGrabPeriodService.insertByBo(bo));
    }

    /**
     * 修改秒杀配置
     */
    @SaCheckPermission("zlyyh:grabPeriod:edit")
    @Log(title = "秒杀配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody GrabPeriodBo bo) {
        return toAjax(iGrabPeriodService.updateByBo(bo));
    }

    /**
     * 删除秒杀配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:grabPeriod:remove")
    @Log(title = "秒杀配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iGrabPeriodService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
