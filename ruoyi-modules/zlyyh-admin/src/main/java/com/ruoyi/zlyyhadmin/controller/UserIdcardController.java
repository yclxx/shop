package com.ruoyi.zlyyhadmin.controller;

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
import com.ruoyi.zlyyhadmin.service.IUserIdcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.UserIdcardVo;
import com.ruoyi.zlyyh.domain.bo.UserIdcardBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 观影用户信息控制器
 * 前端访问路由地址为:/zlyyh/userIdcard
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/userIdcard")
public class UserIdcardController extends BaseController {

    private final IUserIdcardService iUserIdcardService;

    /**
     * 查询观影用户信息列表
     */
    @SaCheckPermission("zlyyh:userIdcard:list")
    @GetMapping("/list")
    public TableDataInfo<UserIdcardVo> list(UserIdcardBo bo, PageQuery pageQuery) {
        return iUserIdcardService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出观影用户信息列表
     */
    @SaCheckPermission("zlyyh:userIdcard:export")
    @Log(title = "观影用户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UserIdcardBo bo, HttpServletResponse response) {
        List<UserIdcardVo> list = iUserIdcardService.queryList(bo);
        ExcelUtil.exportExcel(list, "观影用户信息", UserIdcardVo.class, response);
    }

    /**
     * 获取观影用户信息详细信息
     *
     * @param userIdcardId 主键
     */
    @SaCheckPermission("zlyyh:userIdcard:query")
    @GetMapping("/{userIdcardId}")
    public R<UserIdcardVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long userIdcardId) {
        return R.ok(iUserIdcardService.queryById(userIdcardId));
    }

    /**
     * 新增观影用户信息
     */
    @SaCheckPermission("zlyyh:userIdcard:add")
    @Log(title = "观影用户信息", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UserIdcardBo bo) {
        return toAjax(iUserIdcardService.insertByBo(bo));
    }

    /**
     * 修改观影用户信息
     */
    @SaCheckPermission("zlyyh:userIdcard:edit")
    @Log(title = "观影用户信息", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UserIdcardBo bo) {
        return toAjax(iUserIdcardService.updateByBo(bo));
    }

    /**
     * 删除观影用户信息
     *
     * @param userIdcardIds 主键串
     */
    @SaCheckPermission("zlyyh:userIdcard:remove")
    @Log(title = "观影用户信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIdcardIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] userIdcardIds) {
        return toAjax(iUserIdcardService.deleteWithValidByIds(Arrays.asList(userIdcardIds), true));
    }
}
