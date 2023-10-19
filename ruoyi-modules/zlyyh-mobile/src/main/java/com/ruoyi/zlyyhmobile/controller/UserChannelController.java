package com.ruoyi.zlyyhmobile.controller;

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
import com.ruoyi.zlyyh.domain.bo.UserChannelBo;
import com.ruoyi.zlyyh.domain.vo.UserChannelVo;
import com.ruoyi.zlyyhmobile.service.IUserChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 用户渠道信息控制器
 * 前端访问路由地址为:/zlyyh/userChannel
 *
 * @author yzg
 * @date 2023-10-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/userChannel")
public class UserChannelController extends BaseController {

    private final IUserChannelService iUserChannelService;

    /**
     * 查询用户渠道信息列表
     */
    @SaCheckPermission("zlyyh:userChannel:list")
    @GetMapping("/list")
    public TableDataInfo<UserChannelVo> list(UserChannelBo bo, PageQuery pageQuery) {
        return iUserChannelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户渠道信息列表
     */
    @SaCheckPermission("zlyyh:userChannel:export")
    @Log(title = "用户渠道信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UserChannelBo bo, HttpServletResponse response) {
        List<UserChannelVo> list = iUserChannelService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户渠道信息", UserChannelVo.class, response);
    }

    /**
     * 获取用户渠道信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zlyyh:userChannel:query")
    @GetMapping("/{id}")
    public R<UserChannelVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iUserChannelService.queryById(id));
    }

    /**
     * 新增用户渠道信息
     */
    @SaCheckPermission("zlyyh:userChannel:add")
    @Log(title = "用户渠道信息", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UserChannelBo bo) {
        return toAjax(iUserChannelService.insertByBo(bo));
    }

    /**
     * 修改用户渠道信息
     */
    @SaCheckPermission("zlyyh:userChannel:edit")
    @Log(title = "用户渠道信息", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UserChannelBo bo) {
        return toAjax(iUserChannelService.updateByBo(bo));
    }

    /**
     * 删除用户渠道信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:userChannel:remove")
    @Log(title = "用户渠道信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iUserChannelService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
