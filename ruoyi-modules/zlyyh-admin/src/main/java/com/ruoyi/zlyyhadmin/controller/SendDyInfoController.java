package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.SendDyInfoBo;
import com.ruoyi.zlyyh.domain.vo.SendDyInfoVo;
import com.ruoyi.zlyyhadmin.service.ISendDyInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 用户订阅控制器
 * 前端访问路由地址为:/zlyyh/sendDyInfo
 *
 * @author yzg
 * @date 2023-12-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/sendDyInfo")
public class SendDyInfoController extends BaseController {

    private final ISendDyInfoService iSendDyInfoService;

    /**
     * 查询用户订阅列表
     */
    @SaCheckPermission("zlyyh:sendDyInfo:list")
    @GetMapping("/list")
    public TableDataInfo<SendDyInfoVo> list(SendDyInfoBo bo, PageQuery pageQuery) {
        return iSendDyInfoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户订阅列表
     */
    @SaCheckPermission("zlyyh:sendDyInfo:export")
    @Log(title = "用户订阅", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SendDyInfoBo bo, HttpServletResponse response) {
        List<SendDyInfoVo> list = iSendDyInfoService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户订阅", SendDyInfoVo.class, response);
    }

    /**
     * 获取用户订阅详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:sendDyInfo:query")
    @GetMapping("/{id}")
    public R<SendDyInfoVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iSendDyInfoService.queryById(id));
    }

    /**
     * 新增用户订阅
     */
    @SaCheckPermission("zlyyh:sendDyInfo:add")
    @Log(title = "用户订阅", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SendDyInfoBo bo) {
        return toAjax(iSendDyInfoService.insertByBo(bo));
    }

    /**
     * 修改用户订阅
     */
    @SaCheckPermission("zlyyh:sendDyInfo:edit")
    @Log(title = "用户订阅", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SendDyInfoBo bo) {
        return toAjax(iSendDyInfoService.updateByBo(bo));
    }

    /**
     * 删除用户订阅
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:sendDyInfo:remove")
    @Log(title = "用户订阅", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iSendDyInfoService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
