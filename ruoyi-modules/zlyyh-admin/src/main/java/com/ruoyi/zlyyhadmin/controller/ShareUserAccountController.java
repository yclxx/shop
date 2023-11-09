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
import com.ruoyi.zlyyh.domain.bo.ShareUserAccountBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserAccountVo;
import com.ruoyi.zlyyhadmin.service.IShareUserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 分销用户账户控制器
 * 前端访问路由地址为:/zlyyh/shareUserAccount
 *
 * @author yzg
 * @date 2023-11-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shareUserAccount")
public class ShareUserAccountController extends BaseController {

    private final IShareUserAccountService iShareUserAccountService;

    /**
     * 查询分销用户账户列表
     */
    @SaCheckPermission("zlyyh:shareUserAccount:list")
    @GetMapping("/list")
    public TableDataInfo<ShareUserAccountVo> list(ShareUserAccountBo bo, PageQuery pageQuery) {
        return iShareUserAccountService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出分销用户账户列表
     */
    @SaCheckPermission("zlyyh:shareUserAccount:export")
    @Log(title = "分销用户账户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShareUserAccountBo bo, HttpServletResponse response) {
        List<ShareUserAccountVo> list = iShareUserAccountService.queryList(bo);
        ExcelUtil.exportExcel(list, "分销用户账户", ShareUserAccountVo.class, response);
    }

    /**
     * 获取分销用户账户详细信息
     *
     * @param userId 主键
     */
    @SaCheckPermission("zlyyh:shareUserAccount:query")
    @GetMapping("/{userId}")
    public R<ShareUserAccountVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long userId) {
        return R.ok(iShareUserAccountService.queryById(userId));
    }

    /**
     * 新增分销用户账户
     */
    @SaCheckPermission("zlyyh:shareUserAccount:add")
    @Log(title = "分销用户账户", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShareUserAccountBo bo) {
        return toAjax(iShareUserAccountService.insertByBo(bo));
    }

    /**
     * 修改分销用户账户
     */
    @SaCheckPermission("zlyyh:shareUserAccount:edit")
    @Log(title = "分销用户账户", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShareUserAccountBo bo) {
        return toAjax(iShareUserAccountService.updateByBo(bo));
    }

    /**
     * 删除分销用户账户
     *
     * @param userIds 主键串
     */
    @SaCheckPermission("zlyyh:shareUserAccount:remove")
    @Log(title = "分销用户账户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] userIds) {
        return toAjax(iShareUserAccountService.deleteWithValidByIds(Arrays.asList(userIds), true));
    }
}
